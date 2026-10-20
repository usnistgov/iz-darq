import {Injectable} from '@angular/core';
import {Field} from '../../report-template/model/analysis.values';
import {Labelizer} from '../../shared/services/values.service';
import {
  IComparisonRow,
  IComparisonSummary,
  IRowDifferential,
  ISectionDifferential,
  ITableDifferential,
  MeasureOutcome,
} from '../model/comparison.model';

@Injectable({
  providedIn: 'root'
})
export class ReportComparisonService {

  /**
   * Builds the measure name shown for a row, resolving the row identifier values
   * through the labelizer the same way the analysis report display does.
   */
  measureName(table: ITableDifferential, row: IRowDifferential, labelizer: Labelizer): string {
    const caption = table.tableCaption || '';
    const identifier = this.rowIdentifierLabel(row, labelizer);
    return identifier ? caption + ' — ' + identifier : caption;
  }

  rowIdentifierLabel(row: IRowDifferential, labelizer: Labelizer): string {
    if (!row.rowIdentifier) {
      return '';
    }
    return Object.keys(row.rowIdentifier)
      .map((field) => {
        const value = row.rowIdentifier[field];
        return labelizer ? labelizer.for(field as Field, value) : value;
      })
      .filter((label) => !!label)
      .join(', ');
  }

  /**
   * A measure improves when it goes from failing to passing, and worsens on the
   * reverse transition. This mirrors the outcome logic used for the CSV export.
   */
  outcome(row: IRowDifferential): MeasureOutcome {
    if (!row.basePass && row.newPass) {
      return MeasureOutcome.BETTER;
    }
    if (row.basePass && !row.newPass) {
      return MeasureOutcome.WORSE;
    }
    return MeasureOutcome.NO_CHANGE;
  }

  pointChange(row: IRowDifferential): number {
    if (row.newPercent === undefined || row.newPercent === null ||
        row.basePercent === undefined || row.basePercent === null) {
      return 0;
    }
    return row.newPercent - row.basePercent;
  }

  toComparisonRow(table: ITableDifferential, row: IRowDifferential, labelizer: Labelizer): IComparisonRow {
    return {
      measureName: this.measureName(table, row, labelizer),
      thresholdText: row.thresholdText,
      basePercent: row.basePercent,
      basePass: row.basePass,
      newPercent: row.newPercent,
      newPass: row.newPass,
      pointChange: this.pointChange(row),
      outcome: this.outcome(row),
      missingInNewReport: row.missingInNewReport,
      passStatusChanged: row.passStatusChanged,
    };
  }

  /**
   * Flattens every table of a section into display rows, excluding children.
   */
  sectionRows(section: ISectionDifferential, labelizer: Labelizer): IComparisonRow[] {
    if (!section || !section.tableDifferentials) {
      return [];
    }
    return section.tableDifferentials.reduce((rows, table) => {
      const tableRows = (table.rowDifferentials || []).map((row) => this.toComparisonRow(table, row, labelizer));
      return [...rows, ...tableRows];
    }, [] as IComparisonRow[]);
  }

  summarize(rows: IComparisonRow[]): IComparisonSummary {
    return (rows || []).reduce((summary, row) => {
      if (row.missingInNewReport) {
        return { ...summary, total: summary.total + 1, missing: summary.missing + 1 };
      }
      switch (row.outcome) {
        case MeasureOutcome.BETTER:
          return { ...summary, total: summary.total + 1, better: summary.better + 1 };
        case MeasureOutcome.WORSE:
          return { ...summary, total: summary.total + 1, worse: summary.worse + 1 };
        default:
          return { ...summary, total: summary.total + 1, noChange: summary.noChange + 1 };
      }
    }, { total: 0, better: 0, worse: 0, noChange: 0, missing: 0 });
  }

  /**
   * Summary for a section including every descendant, used for the section header
   * badges and the report level totals.
   */
  deepSummarize(sections: ISectionDifferential[], labelizer: Labelizer): IComparisonSummary {
    return (sections || []).reduce((summary, section) => {
      const own = this.summarize(this.sectionRows(section, labelizer));
      const children = this.deepSummarize(section.children, labelizer);
      return {
        total: summary.total + own.total + children.total,
        better: summary.better + own.better + children.better,
        worse: summary.worse + own.worse + children.worse,
        noChange: summary.noChange + own.noChange + children.noChange,
        missing: summary.missing + own.missing + children.missing,
      };
    }, { total: 0, better: 0, worse: 0, noChange: 0, missing: 0 });
  }

  /**
   * True when the section, or any of its descendants, carries a change worth
   * surfacing. Drives the "changes only" filter.
   */
  sectionHasChange(section: ISectionDifferential, labelizer: Labelizer): boolean {
    if (!section) {
      return false;
    }
    const own = this.sectionRows(section, labelizer).some((row) => this.rowHasChange(row));
    if (own) {
      return true;
    }
    return (section.children || []).some((child) => this.sectionHasChange(child, labelizer));
  }

  rowHasChange(row: IComparisonRow): boolean {
    return row.missingInNewReport || row.outcome !== MeasureOutcome.NO_CHANGE;
  }
}
