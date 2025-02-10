import { IQueryVariableDisplay } from './../variable-ref-display/variable-ref-display.component';
import { QueryVariableService } from './../../services/query-variable.service';
import { QuerySaveDialogComponent, IQuerySaveDetails } from './../query-save-dialog/query-save-dialog.component';
import { IQuerySaveRequest } from './../../services/query.service';
import { IConfigurationPayload } from './../../../configuration/model/configuration.model';
import { map, flatMap, catchError } from 'rxjs/operators';
import { IQuery } from './../../model/query.model';
import { QueryListDialogComponent } from './../query-list-dialog/query-list-dialog.component';
import { ValuesService } from 'src/app/modules/shared/services/values.service';
import { Labelizer } from './../../services/values.service';
import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { SelectItem } from 'primeng/api/selectitem';
import { AnalysisType, names, Field } from '../../../report-template/model/analysis.values';
import { IDataSelector, QueryPayloadType, QueryType } from '../../../report-template/model/report-template.model';
import { IFieldInputOptions } from '../field-input/field-input.component';
import { UserMessage, MessageService, IMessage } from '@usnistgov/ngx-dam-framework-legacy';
import * as _ from 'lodash';
import { QueryService } from '../../services/query.service';
import { of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-query-dialog',
  templateUrl: './query-dialog.component.html',
  styleUrls: ['./query-dialog.component.scss']
})
export class QueryDialogComponent implements OnInit {
  typeNames = names;
  AnalysisType = AnalysisType;
  QueryPayloadType = QueryPayloadType;
  paths: SelectItem[];
  options: IFieldInputOptions;
  value: QueryType;
  backUp: QueryType;
  tabsValid = {
    general: {
      valid: true,
      messages: [],
    },
    selectors: {
      valid: true,
      messages: [],
    },
    occurrences: {
      valid: true,
      messages: [],
    },
    groupBy: {
      valid: true,
      messages: [],
    },
    filters: {
      valid: true,
      messages: [],
    },
    thresholds: {
      valid: true,
      messages: [],
    },
    simple: {
      valid: true,
      messages: [],
    },
    variables: {
      valid: true,
      messages: [],
    },
  };
  labelizer: Labelizer;
  query: IQuery;
  configuration: IConfigurationPayload;
  variables: IQueryVariableDisplay[];

  httpApiErrorMessage: UserMessage[] = [];

  constructor(
    public dialogRef: MatDialogRef<QueryDialogComponent>,
    private dialog: MatDialog,
    private queryService: QueryService,
    private messageService: MessageService,
    private queryVariableService: QueryVariableService,
    valuesService: ValuesService,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.options = data.options;
    this.configuration = data.configuration;
    this.labelizer = valuesService.getQueryValuesLabel(this.options);
    this.value = _.cloneDeep(data.query);
    this.backUp = _.cloneDeep(this.value);
    this.paths = Object.keys(AnalysisType).map((key) => {
      return {
        label: names[key],
        value: AnalysisType[key],
      };
    });
    this.queryVariableService.getVariablesDisplay().pipe(
      map((variables) => {
        this.variables = variables;
      })
    ).subscribe();
  }

  clearLoadedQuery() {
    this.query = null;
    this.clearValidationInfo();
    this.value = _.cloneDeep(this.queryService.getEmptySimpleQuery(AnalysisType.VACCINCATIONS_DETECTIONS));
    this.backUp = _.cloneDeep(this.value);
  }

  reset() {
    this.value = _.cloneDeep(this.backUp);
  }

  get messages() {
    return [
      ...this.tabsValid.general.messages,
      ...this.tabsValid.simple.messages,
      ...this.tabsValid.occurrences.messages,
      ...this.tabsValid.groupBy.messages,
      ...this.tabsValid.selectors.messages,
      ...this.tabsValid.filters.messages,
      ...this.tabsValid.thresholds.messages,
      ...this.tabsValid.variables.messages,
      ...this.httpApiErrorMessage,
    ];
  }

  get valid() {
    return this.tabsValid.general.valid &&
      this.tabsValid.simple.valid &&
      this.tabsValid.occurrences.valid &&
      this.tabsValid.groupBy.valid &&
      this.tabsValid.selectors.valid &&
      this.tabsValid.filters.valid &&
      this.tabsValid.thresholds.valid &&
      this.tabsValid.variables.valid;
  }

  clearValidationInfo() {
    this.httpApiErrorMessage = [];
    this.tabsValid = {
      general: {
        valid: true,
        messages: [],
      },
      selectors: {
        valid: true,
        messages: [],
      },
      occurrences: {
        valid: true,
        messages: [],
      },
      groupBy: {
        valid: true,
        messages: [],
      },
      filters: {
        valid: true,
        messages: [],
      },
      thresholds: {
        valid: true,
        messages: [],
      },
      simple: {
        valid: true,
        messages: [],
      },
      variables: {
        valid: true,
        messages: [],
      },
    };
  }

  loadSavedQuery() {
    this.dialog.open(QueryListDialogComponent, {
      minWidth: '60vw',
      maxWidth: '93vw',
      maxHeight: '95vh',
      panelClass: 'query-list-dialog',
      data: {
        labelizer: this.labelizer,
        configuration: this.configuration,
        current: this.query,
      }
    }).afterClosed().pipe(
      map((value) => {
        if (value) {
          this.query = value;
          this.clearValidationInfo();
          this.value = _.cloneDeep(this.query.query) as QueryType;
          this.backUp = _.cloneDeep(this.value);
        }
      })
    ).subscribe();
  }

  displayHTTPApiMessage(message: IMessage<any>) {
    const m = this.messageService.mergeUserMessage(UserMessage.fromMessage(
      message, {
      closable: true,
    }));
    this.httpApiErrorMessage.push(m);
  }

  displayHTTPApiError(response: HttpErrorResponse) {
    const m = this.messageService.fromError(response, null, {
      closable: true,
    });
    this.httpApiErrorMessage.push(m);
  }

  closeNotification(id: string) {
    const deleteInList = (list: any[]) => {
      const idx = list.findIndex((e) => e.id === id);
      if (idx !== -1) {
        list.splice(idx, 1);
      }
    };

    deleteInList(this.tabsValid.general.messages);
    deleteInList(this.tabsValid.selectors.messages);
    deleteInList(this.tabsValid.occurrences.messages);
    deleteInList(this.tabsValid.groupBy.messages);
    deleteInList(this.tabsValid.filters.messages);
    deleteInList(this.tabsValid.thresholds.messages);
    deleteInList(this.tabsValid.simple.messages);
    deleteInList(this.httpApiErrorMessage);
  }

  saveQuery() {
    this.dialog.open(QuerySaveDialogComponent, {
      data: {
        query: this.query
      }
    }).afterClosed().pipe(
      flatMap((result: IQuerySaveDetails) => {
        if (result) {
          const query: IQuerySaveRequest = {
            id: result.replace ? this.query.id : null,
            name: result.name,
            query: this.value,
            configuration: this.configuration,
          };
          return this.queryService.saveQuery(query).pipe(
            map((m) => {
              this.displayHTTPApiMessage(m);
              this.query = m.data;
            }),
            catchError((e) => {
              this.displayHTTPApiError(e);
              return throwError(e);
            })
          );
        }
        return of();
      })
    ).subscribe();
  }

  valueOfAnalysis(str: string): AnalysisType {
    return Object.keys(AnalysisType).find((key) => {
      return AnalysisType[key] === str;
    }) as AnalysisType;
  }

  nameOf(type: AnalysisType) {
    return names[this.valueOfAnalysis(type)];
  }

  setValidStatus(key: string, status: boolean) {
    this.tabsValid[key].valid = status;
  }

  setMessages(key: string, messages: UserMessage[]) {
    this.tabsValid[key].messages = messages;
  }

  selectorsHasField(selectors: IDataSelector[], field: Field) {
    return selectors.findIndex((selector) => selector.field === field) !== -1;
  }

  groupsHasField(groups: Field[], field: Field) {
    return groups.indexOf(field) !== -1;
  }

  changeType(type: AnalysisType) {
    this.setEmptyValue(this.value.payloadType, type);
  }

  setEmptyValue(type: QueryPayloadType, analysis: AnalysisType) {
    if (type === QueryPayloadType.SIMPLE) {
      this.value = this.queryService.getEmptySimpleQuery(analysis);
      this.tabsValid = {
        ...this.tabsValid,
        selectors: {
          valid: true,
          messages: [],
        },
        occurrences: {
          valid: true,
          messages: [],
        },
        groupBy: {
          valid: true,
          messages: [],
        },
        thresholds: {
          valid: true,
          messages: [],
        },
        variables: {
          valid: true,
          messages: [],
        },
      };
    } else if (type === QueryPayloadType.ADVANCED) {
      this.value = this.queryService.getEmptyAdvancedQuery(analysis);
      this.tabsValid = {
        ...this.tabsValid,
        simple: {
          valid: true,
          messages: [],
        },
        variables: {
          valid: true,
          messages: [],
        },
      };
    } else if (type === QueryPayloadType.VARIABLE) {
      this.value = this.queryService.getEmptyVariableQuery();
      this.tabsValid = {
        ...this.tabsValid,
        selectors: {
          valid: true,
          messages: [],
        },
        occurrences: {
          valid: true,
          messages: [],
        },
        groupBy: {
          valid: true,
          messages: [],
        },
        thresholds: {
          valid: true,
          messages: [],
        },
        simple: {
          valid: true,
          messages: [],
        },
      };
    }
  }

  changeMode(type: QueryPayloadType) {
    if (type !== this.value.payloadType) {
      this.setEmptyValue(type, this.value.type || AnalysisType.VACCINCATIONS_DETECTIONS);
    }
  }

  getAllFields(value: QueryType): Field[] {
    switch (value.payloadType) {
      case QueryPayloadType.SIMPLE:
        return [
          ...(value.denominator?.active && value.denominator.field ? [value.denominator.field] : []),
          ...(value.nominator ? [value.nominator] : []),
        ];
      case QueryPayloadType.ADVANCED:
        return [
          ...value.groupBy,
          ...value.occurrences,
        ];
    }
    return [];
  }

  ngOnInit(): void {
  }

}
