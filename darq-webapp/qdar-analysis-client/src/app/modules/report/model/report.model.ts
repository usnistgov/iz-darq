import { IReportTemplate, IReportSection, IDataViewQuery, IThreshold, IReportTemplateDescriptor, ISection, QueryType } from '../../report-template/model/report-template.model';
import { Field } from '../../report-template/model/analysis.values';
import { IDamResource } from 'ngx-dam-framework';
import { EntityType } from '../../shared/model/entity.model';
import { IConfigurationPayload } from '../../configuration/model/configuration.model';

export interface IReportDescriptor extends IDamResource {
  id: string;
  name: string;
  description: string;
  owner: string;
  lastUpdated: Date;
  published: boolean;
  public: boolean;
  type: EntityType;
  configuration: IConfigurationPayload;
  customDetectionLabels: Record<string, string>;
}
export interface IReport extends IReportDescriptor {
  publishDate: Date;
  facilityId: string;
  adfName: string;
  type: EntityType.REPORT;
  sections: IReportSectionResult[];
  reportTemplate: IReportTemplate;
}

export interface IReportSectionResult extends ISection {
  data: IDataTable[];
  comment: string;
  thresholdViolation: boolean;
  children: IReportSectionResult[];
  hasValue: boolean;
}

export interface IDataTable {
  nominator: Field[];
  denominator: Field[];
  query: QueryType;
  values: IDataTableRow[];
  issues: {
    inactiveDetections: string[];
  };
  thresholdViolation: boolean;
}

export interface IDataTableRow {
  values: {
    [key: string]: string;
  };
  groupId: number;
  result: IFraction;
  threshold: IThreshold;
  pass: boolean;
}

export interface IFraction {
  count: number;
  total: number;
}

export interface IAnalysisJob extends IDamResource {
  id: string;
  type: EntityType.JOB;
  name: string;
  adfId: string;
  adfName: string;
  template: IReportTemplate;
  submitTime: Date;
  startTime: Date;
  endTime: Date;
  status: JobStatus;
  owner: string;
  failure: string;
  reportId: string;
}

export interface IAnalysisJobRequest {
  name: string;
  templateId: string;
  adfId: string;
}

export enum JobStatus {
  QUEUED = 'QUEUED',
  RUNNING = 'RUNNING',
  FAILED = 'FAILED',
  STOPPED = 'STOPPED',
  FINISHED = 'FINISHED',
}
