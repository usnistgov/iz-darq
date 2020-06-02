import { EntityType } from '../../shared/model/entity.model';
import { AnalysisType, Field } from './analysis.values';
import { IDescriptor } from '../../shared/model/descriptor.model';
import { IConfigurationPayload, IConfigurationDescriptor } from '../../configuration/model/configuration.model';
import { IDamResource } from 'ngx-dam-framework';


export interface IReportTemplateDescriptor extends IDescriptor {
  type: EntityType.TEMPLATE;
  compatibilities: IConfigurationDescriptor[];
}

export interface IReportTemplateCreate {
  name: string;
  configurationId: string;
}

export interface IReportTemplate {
  id: string;
  name: string;
  description: string;
  owner: string;
  lastUpdated: Date;
  published: boolean;
  type: EntityType.TEMPLATE;
  viewOnly: boolean;
  sections: IReportSection[];
  configuration: IConfigurationPayload;
}

export interface IReportSection extends IDamResource {
  id: string;
  type: EntityType.SECTION;
  path: string;
  position: number;
  header: string;
  text: string;
  data?: IDataViewQuery[];
  children: IReportSection[];
}

export interface IFilter {
  active: boolean;
}

export interface IComparatorFilter extends IFilter {
  value: number;
  comparator: Comparator;
}

export interface IThresholdFilter extends IFilter {
  pass: boolean;
}

export interface IFieldFilter extends IFilter {
  keep: boolean;
  values: {
    [field: string]: IValueContainer,
  }[];
}

export interface IReportFieldFilter extends IFilter {
  keep: boolean;
  fields: {
    [field: string]: IValueContainer[]
  };
}

export interface IQueryResultFilter {
  denominator: IComparatorFilter;
  percentage: IComparatorFilter;
  threshold: IThresholdFilter;
  groups: IFieldFilter;
}

export interface IReportFilter {
  denominator: IComparatorFilter;
  percentage: IComparatorFilter;
  threshold: IThresholdFilter;
  fields: IReportFieldFilter;
}

export interface IDataViewQuery {
  type: AnalysisType;
  caption: string;
  paginate: boolean;
  rows: number;
  selectors: IDataSelector[];
  groupBy: Field[];
  filter: IQueryResultFilter;
  threshold: {
    custom: {
      active: boolean,
      thresholds: IComplexThreshold[],
    },
    global: {
      active: boolean,
      goal: IThreshold,
    }
  };
}


export interface IDataSelector {
  field: Field;
  values: IValueContainer[];
}

export interface IValueContainer {
  value: any;
}

export interface IComplexThreshold {
  values: {
    [field: string]: IValueContainer,
  };
  goal: IThreshold;
}

export interface IThreshold {
  comparator: Comparator;
  value: number;
}

export enum Comparator {
  GT = 'GT',
  LT = 'LT',
  EQ = 'EQ'
}
