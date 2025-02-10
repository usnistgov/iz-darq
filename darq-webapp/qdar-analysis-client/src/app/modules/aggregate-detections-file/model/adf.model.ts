import { IConfigurationDescriptor, IConfigurationPayload } from '../../configuration/model/configuration.model';
import { IDamResource } from '@usnistgov/ngx-dam-framework-legacy';
import { EntityType } from '../../shared/model/entity.model';
import { IRange } from '../../shared/model/age-group.model';
import { JobStatus } from '../../report/model/report.model';

export interface IADFDescriptor extends IDamResource {
  id: string;
  type: EntityType.ADF;
  name: string;
  owner: string;
  analysedOn: Date;
  uploadedOn: Date;
  size: string;
  version: string;
  compatibilities: IConfigurationDescriptor[];
  facilityId: string;
  cliVersion: string;
  build: string;
  mqeVersion: string;
  tags?: string[];
}

export interface IADFMetadata extends IDamResource {
  id: string;
  type: EntityType.ADF;
  name: string;
  owner: string;
  analysedOn: Date;
  uploadedOn: Date;
  size: string;
  configuration: IConfigurationPayload;
  summary: IADFSummary;
  version: string;
  build: string;
  mqeVersion: string;
  facilityId: string;
  inactiveDetections: string[];
  composed: boolean;
  components: {
    id: string;
    name: string;
    ownerId: string;
    analysedOn: Date;
    uploadedOn: Date;
    size: string;
    summary: IADFSummary;
    facilityId: string;
  }[];
  totalAnalysisTime: number;
}

export interface IADFSummary {
  issues: string[];
  countByAgeGroup: IAgeGroupCount[];
  outOfRange: number;
  counts: ISummaryCounts;
  asOfDate: string;
  extract: {
    [id: string]: IExtractPercent,
  };
}

export interface IExtractPercent {
  valued: number;
  excluded: number;
  notCollected: number;
  notExtracted: number;
  valuePresent: number;
  valueNotPresent: number;
  valueLength: number;
  empty: number;
  total: number;
}

export interface ISummaryCounts {
  totalReadVaccinations: number;
  totalReadPatientRecords: number;
  totalSkippedPatientRecords: number;
  totalSkippedVaccinationRecords: number;
  maxVaccinationsPerRecord: number;
  minVaccinationsPerRecord: number;
  numberOfProviders: number;
  avgVaccinationsPerRecord: number;
  maxVaccinationsPerProvider: number;
  minVaccinationsPerProvider: number;
  avgVaccinationsPerProvider: number;
  historical: number;
  administered: number;
}

export interface IAgeGroupCount {
  range: IRange;
  nb: number;
}

export interface IADFMergeJobDescriptor {
  id: string;
  type: EntityType.MERGE_JOB;
  name: string;
  submitTime: Date;
  startTime: Date;
  endTime: Date;
  status: JobStatus;
  owner: string;
  failure: string;
  adfList: IADFDescriptor[];
  facilityId: string;
}
