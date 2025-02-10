import { IVariableQuery } from './../../report-template/model/report-template.model';
import { IConfigurationPayload } from './../../configuration/model/configuration.model';
import { IQuery } from './../model/query.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AnalysisType, Field } from '../../report-template/model/analysis.values';
import { ISimpleViewQuery, IDataViewQuery, Comparator, IQueryResultFilter, IQueryPayload, QueryPayloadType } from '../../report-template/model/report-template.model';
import { IMessage } from '@usnistgov/ngx-dam-framework-legacy';

export interface IQueryFlag {
  detections: boolean;
  codes: boolean;
  vaccineEvents: boolean;
  provider: boolean;
}

export interface IQuerySaveRequest {
  id?: string;
  name: string;
  query: IQueryPayload;
  configuration: IConfigurationPayload;
}

@Injectable({
  providedIn: 'root'
})
export class QueryService {

  readonly URL = 'api/query/';

  constructor(private http: HttpClient) { }

  getQueries(): Observable<IQuery[]> {
    return this.http.get<IQuery[]>(this.URL);
  }

  getQueriesForConfiguration(config: IConfigurationPayload): Observable<IQuery[]> {
    return this.http.post<IQuery[]>(this.URL + 'for-configuration', config);
  }

  getQuery(id: string): Observable<IQuery> {
    return this.http.get<IQuery>(this.URL + id);
  }

  deleteQuery(id: string): Observable<IMessage<IQuery>> {
    return this.http.delete<IMessage<IQuery>>(this.URL + id);
  }

  saveQuery(query: IQuerySaveRequest): Observable<IMessage<IQuery>> {
    return this.http.post<IMessage<IQuery>>(this.URL, query);
  }

  getEmptySimpleQuery(type: AnalysisType): ISimpleViewQuery {
    const flags = this.getQueryFlags(type);
    return {
      ...this.getDefaultGeneralSettings(QueryPayloadType.SIMPLE, type),
      payloadType: QueryPayloadType.SIMPLE,
      filterBy: flags.detections || flags.codes ? {
        field: flags.detections ? Field.DETECTION : Field.TABLE,
        value: undefined,
      } : undefined,
      nominator: flags.matchSignature ? Field.MATCH_SIGNATURE : flags.detections ? Field.DETECTION : Field.CODE,
      denominator: {
        active: flags.provider,
        field: flags.provider ? Field.PROVIDER : undefined,
      },
      threshold: {
        active: false,
        goal: {
          value: 1,
          comparator: Comparator.LT,
        }
      }
    };
  }

  getEmptyVariableQuery(): IVariableQuery {
    return {
      ...this.getDefaultGeneralSettings(QueryPayloadType.VARIABLE),
      payloadType: QueryPayloadType.VARIABLE,
      denominatorVariable: undefined,
      numeratorVariable: undefined,
      threshold: {
        active: false,
        goal: {
          value: 1,
          comparator: Comparator.LT,
        }
      }
    };
  }

  getEmptyAdvancedQuery(type: AnalysisType): IDataViewQuery {
    return {
      ...this.getDefaultGeneralSettings(QueryPayloadType.ADVANCED, type),
      payloadType: QueryPayloadType.ADVANCED,
      occurrences: [],
      groupBy: [],
      selectors: [],
      threshold: {
        global: {
          active: false,
          goal: {
            value: 1,
            comparator: Comparator.LT,
          }
        },
        custom: {
          active: false,
          thresholds: [],
        }
      }
    };
  }

  getEmptyPostFilter(): IQueryResultFilter {
    return {
      denominator: {
        active: false,
        value: 0,
        comparator: Comparator.GT,
      },
      percentage: {
        active: false,
        value: 0,
        comparator: Comparator.GT,
      },
      threshold: {
        active: false,
        pass: true,
      },
      groups: {
        keep: true,
        active: false,
        values: [],
      }
    };
  }

  getDefaultGeneralSettings(payloadType: QueryPayloadType, type?: AnalysisType): IQueryPayload {
    return {
      payloadType,
      type,
      caption: '',
      paginate: true,
      rows: 10,
      filter: this.getEmptyPostFilter(),
    };
  }

  validateSimpleQuery(simple: ISimpleViewQuery): string[] {
    const messages = [];
    const flags = this.getQueryFlags(simple.type);
    const filterIsNotSet = (!simple.filterBy || !simple.filterBy.value);
    if (flags.codes && filterIsNotSet) {
      messages.push('Coded Field is required');
    }
    if (flags.detections && filterIsNotSet) {
      messages.push('Detection is required');
    }
    if (flags.vaccineEvents && !simple.nominator) {
      messages.push('Nominator field is required');
    }
    if (simple.denominator && simple.denominator.active && !simple.denominator.field) {
      messages.push('Select "Breakdown" field');
    }
    if (simple.threshold && simple.threshold.active &&
      (!simple.threshold.goal || !simple.threshold.goal.comparator || simple.threshold.goal.value === undefined)) {
      messages.push('Fill "Threshold" field');
    }
    if (flags.provider &&
      (!simple.denominator || !simple.denominator.active || !simple.denominator.field || simple.denominator.field !== Field.PROVIDER)) {
      messages.push('"Breakdown" field should be REPORTING_GROUP');
    }
    return messages;
  }


  getQueryFlags(type: AnalysisType) {
    return {
      detections: [AnalysisType.PATIENTS_DETECTIONS, AnalysisType.VACCINCATIONS_DETECTIONS, AnalysisType.PATIENTS_PROVIDER_DETECTIONS]
        .includes(type),
      codes: [AnalysisType.PATIENTS_VOCABULARY, AnalysisType.VACCINCATIONS_VOCABULARY, AnalysisType.PATIENTS_PROVIDER_VOCABULARY]
        .includes(type),
      provider: [AnalysisType.PATIENTS_PROVIDER_VOCABULARY, AnalysisType.PATIENTS_PROVIDER_DETECTIONS]
        .includes(type),
      vaccineEvents: [AnalysisType.VACCINCATIONS]
        .includes(type),
      matchSignature: [AnalysisType.PATIENTS_MATCHING]
        .includes(type),
    };
  }

}
