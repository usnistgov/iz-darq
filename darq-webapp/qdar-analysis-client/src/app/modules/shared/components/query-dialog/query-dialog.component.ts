import { ValuesService } from 'src/app/modules/shared/services/values.service';
import { Labelizer } from './../../services/values.service';
import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SelectItem } from 'primeng/api/selectitem';
import { AnalysisType, names, Field } from '../../../report-template/model/analysis.values';
import { IDataSelector, QueryPayloadType, QueryType } from '../../../report-template/model/report-template.model';
import { IFieldInputOptions } from '../field-input/field-input.component';
import { UserMessage } from 'ngx-dam-framework';
import * as _ from 'lodash';
import { QueryService } from '../../services/query.service';

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
    }
  };
  labelizer: Labelizer;

  constructor(
    public dialogRef: MatDialogRef<QueryDialogComponent>,
    private queryService: QueryService,
    private valuesService: ValuesService,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.options = data.options;
    this.labelizer = valuesService.getQueryValuesLabel(this.options);
    this.value = _.cloneDeep(data.query);
    this.backUp = _.cloneDeep(this.value);
    this.paths = Object.keys(AnalysisType).map((key) => {
      return {
        label: names[key],
        value: AnalysisType[key],
      };
    });
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
    ];
  }

  get valid() {
    return this.tabsValid.general.valid &&
      this.tabsValid.simple.valid &&
      this.tabsValid.occurrences.valid &&
      this.tabsValid.groupBy.valid &&
      this.tabsValid.selectors.valid &&
      this.tabsValid.filters.valid &&
      this.tabsValid.thresholds.valid;
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
      };
    } else if (type === QueryPayloadType.ADVANCED) {
      this.value = this.queryService.getEmptyAdvancedQuery(analysis);
      this.tabsValid = {
        ...this.tabsValid,
        simple: {
          valid: true,
          messages: [],
        },
      };
    }
  }

  changeMode(type: QueryPayloadType) {
    if (type !== this.value.payloadType) {
      this.setEmptyValue(type, this.value.type);
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
