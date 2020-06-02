import { IConfigurationDescriptor, IConfigurationPayload } from '../../configuration/model/configuration.model';
import { IDamResource } from 'ngx-dam-framework';
import { EntityType } from '../../shared/model/entity.model';
import { IRange } from '../../shared/model/age-group.model';

export interface IADFDescriptor extends IDamResource {
  id: string;
  type: EntityType.ADF;
  name: string;
  owner: string;
  analysedOn: Date;
  uploadedOn: Date;
  size: string;
  compatibilities: IConfigurationDescriptor[];
  facilityId: string;
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
  inactiveDetections: string[];
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
}

export interface IAgeGroupCount {
  range: IRange;
  nb: number;
}
