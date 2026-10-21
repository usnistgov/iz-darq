import {IADFDescriptor, IADFMetadata, IExtractPercent, ISummaryCounts} from './adf.model';
import {IRange} from '../../shared/model/age-group.model';

/**
 * A single value compared across two ADFs. `delta` is new - base, and is
 * undefined when the value is absent on either side.
 */
export interface IStatDifferential {
  label: string;
  base: number;
  new: number;
  delta: number;
  missingInBase: boolean;
  missingInNew: boolean;
}

export interface IAgeGroupDifferential extends IStatDifferential {
  range: IRange;
}

/**
 * Extract percentages compared per element; one differential per column of the
 * extract table.
 */
export interface IExtractDifferential {
  label: string;
  missingInBase: boolean;
  missingInNew: boolean;
  columns: {
    [P in keyof IExtractPercent]?: IStatDifferential;
  };
}

/**
 * An ADF offered for comparison, with whether its tool version matches the base.
 */
export interface IAdfComparisonCandidate {
  file: IADFDescriptor;
  compatible: boolean;
}

export interface IAdfComparison {
  baseMeta: IADFMetadata;
  newMeta: IADFMetadata;
  counts: IStatDifferential[];
  ageGroups: IAgeGroupDifferential[];
  outOfRange: IStatDifferential;
  extract: IExtractDifferential[];
}

/**
 * Labels for the general counts, in the order the summary page lists them.
 */
export const COUNT_LABELS: { key: keyof ISummaryCounts, label: string }[] = [
  { key: 'totalReadPatientRecords', label: 'Total Read Patient Records' },
  { key: 'totalSkippedPatientRecords', label: 'Total Skipped Patient Records Due To Errors' },
  { key: 'totalReadVaccinations', label: 'Total Read Vaccination Records' },
  { key: 'minVaccinationsPerRecord', label: 'Minimum Vaccinations per Patient' },
  { key: 'maxVaccinationsPerRecord', label: 'Maximum Vaccinations per Patient' },
  { key: 'administered', label: 'Total Number of Administered Vaccinations' },
  { key: 'historical', label: 'Total Number of Historical Vaccinations' },
  { key: 'avgVaccinationsPerRecord', label: 'Average Vaccinations per Patient' },
  { key: 'numberOfProviders', label: 'Total Number of Providers' },
];

/**
 * Extract table columns, in the order the summary page lists them.
 */
export const EXTRACT_COLUMNS: { key: keyof IExtractPercent, label: string }[] = [
  { key: 'valued', label: 'Valued' },
  { key: 'empty', label: 'Empty' },
  { key: 'valueLength', label: 'Value Length' },
  { key: 'valuePresent', label: 'Present' },
  { key: 'valueNotPresent', label: 'Not Present' },
  { key: 'excluded', label: 'Excluded' },
  { key: 'notCollected', label: 'Not Collected' },
  { key: 'notExtracted', label: 'Not Extracted' },
];
