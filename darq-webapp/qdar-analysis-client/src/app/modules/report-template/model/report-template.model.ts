import { IQueryVariableRef } from './../../shared/model/query-variable.model';
import { EntityType } from '../../shared/model/entity.model';
import { AnalysisType, Field } from './analysis.values';
import { IDescriptor } from '../../shared/model/descriptor.model';
import { IConfigurationDescriptor } from '../../configuration/model/configuration.model';
import { IDamResource } from '@usnistgov/ngx-dam-framework-legacy';
import { IReportDescriptor } from '../../report/model/report.model';


export interface IReportTemplateDescriptor extends IDescriptor {
  type: EntityType.TEMPLATE;
  compatibilities: IConfigurationDescriptor[];
}

export interface IReportTemplateCreate {
  name: string;
  configurationId: string;
}

export interface IReportTemplate extends IReportDescriptor {
  type: EntityType.TEMPLATE;
  sections: IReportSection[];
}

export interface IReportSection extends ISection {
  data?: IQueryPayload[];
  children: IReportSection[];
}

export interface ISection extends IDamResource {
  id: string;
  type: EntityType.SECTION;
  path: string;
  position: number;
  header: string;
  text: string;
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

export type QueryType = IDataViewQuery | ISimpleViewQuery | IVariableQuery;

export interface IQueryPayload {
  payloadType: QueryPayloadType;
  type: AnalysisType;
  caption: string;
  paginate: boolean;
  rows: number;
  filter: IQueryResultFilter;
  denominatorVariable?: IQueryVariableRef;
  numeratorVariable?: IQueryVariableRef;
}

export interface IVariableQuery extends IQueryPayload {
  payloadType: QueryPayloadType.VARIABLE;
  denominatorVariable: IQueryVariableRef;
  numeratorVariable: IQueryVariableRef;
  threshold: {
    active: boolean,
    goal: IThreshold,
  };
}


export interface IDataViewQuery extends IQueryPayload {
  payloadType: QueryPayloadType.ADVANCED;
  selectors: IDataSelector[];
  occurrences: Field[];
  groupBy: Field[];
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

export interface ISimpleViewQuery extends IQueryPayload {
  payloadType: QueryPayloadType.SIMPLE;
  filterBy: IDataSingleSelector;
  nominator: Field;
  denominator: {
    active: boolean,
    field: Field,
  };
  threshold: {
    active: boolean,
    goal: IThreshold,
  };
}

export interface IDataSelector {
  field: Field;
  values: IValueContainer[];
}

export interface IDataSingleSelector {
  field: Field;
  value: string;
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

export enum QueryPayloadType {
  SIMPLE = 'SIMPLE',
  ADVANCED = 'ADVANCED',
  VARIABLE = 'VARIABLE',
}
