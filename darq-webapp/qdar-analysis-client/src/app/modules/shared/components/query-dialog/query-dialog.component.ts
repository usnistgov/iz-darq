import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SelectItem } from 'primeng/api/selectitem';
import { AnalysisType, names, Field } from '../../../report-template/model/analysis.values';
import { IDataViewQuery, IDataSelector } from '../../../report-template/model/report-template.model';
import { IFieldInputOptions } from '../field-input/field-input.component';
import { UserMessage } from 'ngx-dam-framework';
import * as _ from 'lodash';

@Component({
  selector: 'app-query-dialog',
  templateUrl: './query-dialog.component.html',
  styleUrls: ['./query-dialog.component.scss']
})
export class QueryDialogComponent implements OnInit {
  typeNames = names;
  paths: SelectItem[];
  options: IFieldInputOptions;
  value: IDataViewQuery;
  backUp: IDataViewQuery;
  tabsValid = {
    general: {
      valid: true,
      messages: [],
    },
    selectors: {
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
    }
  };

  constructor(
    public dialogRef: MatDialogRef<QueryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.options = data.options;
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
      ...this.tabsValid.groupBy.messages,
      ...this.tabsValid.selectors.messages,
      ...this.tabsValid.filters.messages,
      ...this.tabsValid.thresholds.messages,
    ];
  }

  get valid() {
    return this.tabsValid.general.valid &&
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

  ngOnInit(): void {
  }

}
