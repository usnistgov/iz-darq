import {Injectable} from '@angular/core';
import {AgeGroupService} from '../../shared/services/age-group.service';
import {IADFDescriptor, IADFMetadata, IAgeGroupCount, IExtractPercent} from '../model/adf.model';
import {
  COUNT_LABELS,
  EXTRACT_COLUMNS,
  IAdfComparison,
  IAdfComparisonCandidate,
  IAgeGroupDifferential,
  IExtractDifferential,
  IStatDifferential,
} from '../model/adf-comparison.model';

/**
 * Computes the differentials between two ADF summaries on the client, so no
 * comparison endpoint is needed: both metadata payloads are already available
 * from the existing per-file endpoint.
 */
@Injectable({
  providedIn: 'root'
})
export class AdfComparisonService {

  /**
   * CLI versions whose ADF summaries share the same shape, so their statistics
   * can be meaningfully compared. Mirrors the grouping used when merging files.
   */
  private readonly compatibilityVersions = [
    ['2.0.0', '2.0.0-SNAPSHOT', '2.0.1', '2.0.2', '2.0.3'],
    ['2.1.0', '2.1.1', '2.1.2', '2.1.3'],
    ['3.0.0-SNAPSHOT', '3.0.0-SNAPSHOT.rev1'],
    ['3.0.0', '3.0.1'],
    ['3.1.0', '3.1.1', '3.1.2', '3.1.3', '3.1.4', '3.1.4.1', '3.1.4.2', '3.1.4.3', '3.1.4.4'],
  ];

  constructor(private ageGroupService: AgeGroupService) { }

  /**
   * True when two ADFs were produced by tool versions known to be compatible.
   * Files with no version recorded (older uploads) are not known to be
   * incompatible, so they are reported as a mismatch rather than excluded:
   * see candidatesFor, which flags instead of filtering.
   */
  areComparable(a: IADFDescriptor, b: IADFDescriptor): boolean {
    if (!a || !b || !a.cliVersion || !b.cliVersion) {
      return false;
    }
    const compatible = this.compatibilityVersions.find((list) => list.includes(a.cliVersion));
    if (compatible) {
      return a.mqeVersion === b.mqeVersion && compatible.includes(a.cliVersion) && compatible.includes(b.cliVersion);
    }
    return a.mqeVersion === b.mqeVersion && a.cliVersion === b.cliVersion;
  }

  /**
   * Every other ADF, each flagged with whether its tool version matches the
   * base. Comparing across versions still produces usable differentials, so the
   * mismatch is surfaced as a warning and the choice is left to the user.
   */
  candidatesFor(base: IADFDescriptor, files: IADFDescriptor[]): IAdfComparisonCandidate[] {
    return (files || [])
      .filter((file) => file.id !== base.id)
      .map((file) => ({
        file,
        compatible: this.areComparable(base, file),
      }))
      .sort((a, b) => {
        if (a.compatible !== b.compatible) {
          return a.compatible ? -1 : 1;
        }
        return (a.file.name || '').localeCompare(b.file.name || '');
      });
  }

  /**
   * Candidates whose tool version is known to match the base.
   */
  comparableTo(base: IADFDescriptor, files: IADFDescriptor[]): IADFDescriptor[] {
    return (files || []).filter((file) => file.id !== base.id && this.areComparable(base, file));
  }

  compare(baseMeta: IADFMetadata, newMeta: IADFMetadata): IAdfComparison {
    return {
      baseMeta,
      newMeta,
      counts: this.compareCounts(baseMeta, newMeta),
      ageGroups: this.compareAgeGroups(baseMeta, newMeta),
      outOfRange: this.differential(
        'Out Of Range',
        this.summaryOf(baseMeta) ? this.summaryOf(baseMeta).outOfRange : undefined,
        this.summaryOf(newMeta) ? this.summaryOf(newMeta).outOfRange : undefined,
      ),
      extract: this.compareExtract(baseMeta, newMeta),
    };
  }

  /**
   * Builds one differential, tolerating values missing on either side so a
   * partially populated summary still renders.
   */
  differential(label: string, base: number, newValue: number): IStatDifferential {
    const missingInBase = base === undefined || base === null;
    const missingInNew = newValue === undefined || newValue === null;
    return {
      label,
      base: missingInBase ? undefined : base,
      new: missingInNew ? undefined : newValue,
      delta: missingInBase || missingInNew ? undefined : newValue - base,
      missingInBase,
      missingInNew,
    };
  }

  private summaryOf(meta: IADFMetadata) {
    return meta ? meta.summary : undefined;
  }

  compareCounts(baseMeta: IADFMetadata, newMeta: IADFMetadata): IStatDifferential[] {
    const baseCounts = this.summaryOf(baseMeta) ? this.summaryOf(baseMeta).counts : undefined;
    const newCounts = this.summaryOf(newMeta) ? this.summaryOf(newMeta).counts : undefined;
    return COUNT_LABELS.map((entry) => this.differential(
      entry.label,
      baseCounts ? baseCounts[entry.key] : undefined,
      newCounts ? newCounts[entry.key] : undefined,
    ));
  }

  /**
   * Age groups are matched on their range label, since the two ADFs may not
   * list the same brackets in the same order.
   */
  compareAgeGroups(baseMeta: IADFMetadata, newMeta: IADFMetadata): IAgeGroupDifferential[] {
    const baseGroups = this.summaryOf(baseMeta) ? this.summaryOf(baseMeta).countByAgeGroup || [] : [];
    const newGroups = this.summaryOf(newMeta) ? this.summaryOf(newMeta).countByAgeGroup || [] : [];

    const keyOf = (group: IAgeGroupCount) => this.ageGroupService.getAgeGroupLabel(group.range);
    const newByKey = newGroups.reduce((acc, group) => ({ ...acc, [keyOf(group)]: group }), {} as Record<string, IAgeGroupCount>);
    const baseByKey = baseGroups.reduce((acc, group) => ({ ...acc, [keyOf(group)]: group }), {} as Record<string, IAgeGroupCount>);

    const orderedKeys = [
      ...baseGroups.map(keyOf),
      ...newGroups.map(keyOf).filter((key) => !baseByKey[key]),
    ];

    return orderedKeys.map((key) => {
      const baseGroup = baseByKey[key];
      const newGroup = newByKey[key];
      return {
        ...this.differential(key, baseGroup ? baseGroup.nb : undefined, newGroup ? newGroup.nb : undefined),
        range: (baseGroup || newGroup).range,
      };
    });
  }

  /**
   * Extract percentages are matched on the element key, so elements present in
   * only one of the two ADFs still appear.
   */
  compareExtract(baseMeta: IADFMetadata, newMeta: IADFMetadata): IExtractDifferential[] {
    const baseExtract = this.summaryOf(baseMeta) ? this.summaryOf(baseMeta).extract || {} : {};
    const newExtract = this.summaryOf(newMeta) ? this.summaryOf(newMeta).extract || {} : {};

    const keys = [
      ...Object.keys(baseExtract),
      ...Object.keys(newExtract).filter((key) => !baseExtract[key]),
    ];

    return keys.map((key) => {
      const basePercent: IExtractPercent = baseExtract[key];
      const newPercent: IExtractPercent = newExtract[key];
      const columns = EXTRACT_COLUMNS.reduce((acc, column) => {
        return {
          ...acc,
          [column.key]: this.differential(
            column.label,
            basePercent ? basePercent[column.key] : undefined,
            newPercent ? newPercent[column.key] : undefined,
          ),
        };
      }, {});
      return {
        label: key,
        missingInBase: !basePercent,
        missingInNew: !newPercent,
        columns,
      };
    });
  }
}
