import {Field} from '../../report-template/model/analysis.values';
import {IFraction} from './report.model';

export interface IReportComparisonResult {
  baseReportId: string;
  newReportId: string;
  templateId: string;
  sectionDifferentials: ISectionDifferential[];
}

export interface ISectionDifferential {
  sectionId: string;
  sectionPath: string;
  sectionHeader: string;
  missingInNewReport: boolean;
  thresholdViolationChanged: boolean;
  tableDifferentials: ITableDifferential[];
  children: ISectionDifferential[];
}

export interface ITableDifferential {
  tableIndex: number;
  tableCaption: string;
  missingInNewReport: boolean;
  thresholdViolationChanged: boolean;
  rowDifferentials: IRowDifferential[];
}

export interface IRowDifferential {
  rowIdentifier: {
    [key in Field]?: string;
  };
  missingInNewReport: boolean;
  passStatusChanged: boolean;
  baseResult: IFraction;
  newResult: IFraction;
  thresholdText: string;
  basePass: boolean;
  newPass: boolean;
  basePercent: number;
  newPercent: number;
}

export enum MeasureOutcome {
  BETTER = 'Better',
  WORSE = 'Worse',
  NO_CHANGE = 'No Change',
}

/**
 * Flattened view of a row differential, ready for tabular display.
 */
export interface IComparisonRow {
  measureName: string;
  thresholdText: string;
  basePercent: number;
  basePass: boolean;
  newPercent: number;
  newPass: boolean;
  pointChange: number;
  outcome: MeasureOutcome;
  missingInNewReport: boolean;
  passStatusChanged: boolean;
}

/**
 * Comparison counters used for the section and report level summaries.
 */
export interface IComparisonSummary {
  total: number;
  better: number;
  worse: number;
  noChange: number;
  missing: number;
}
