import { IReportTemplate, IReportSection, IDataViewQuery, IThreshold, IReportTemplateDescriptor } from '../../report-template/model/report-template.model';
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
  type: EntityType.REPORT;
  viewOnly: boolean;
  configuration: IConfigurationPayload;
  template: IReportTemplateDescriptor;
  adfName: string;
  publishDate: Date;
  facilityId: string;
}

export interface IReport extends IReportTemplate {
  templateId: string;
  facilityId: string;
  sections: IReportSectionResult[];
}

export interface IReportSectionResult extends IReportSection {
  data: IDataTable[];
  comment: string;
  thresholdViolation: boolean;
  children: IReportSectionResult[];
}

export interface IDataTable extends IDataViewQuery {
  headers: Field[];
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
