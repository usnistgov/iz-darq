import { Component, OnInit, Input, Output, EventEmitter, SimpleChanges, OnChanges, AfterViewInit } from '@angular/core';
import { Field, fieldsForAnalysis, AnalysisType } from '../../../../report-template/model/analysis.values';
import { UserMessage, MessageType } from 'ngx-dam-framework';
import { QueryDialogTabComponent } from '../query-dialog-tab/query-dialog-tab.component';
import { IValueContainer, IComplexThreshold } from '../../../../report-template/model/report-template.model';

@Component({
  selector: 'app-query-group',
  templateUrl: './query-group.component.html',
  styleUrls: ['./query-group.component.scss']
})
export class QueryGroupComponent extends QueryDialogTabComponent<Field[]> implements OnInit, OnChanges {

  @Input()
  filterGoup: {
    [field: string]: IValueContainer;
  }[];

  @Input()
  thresholds: IComplexThreshold[];

  fieldsList: Field[];

  constructor() {
    super();
  }

  validate() {
    const imp = this.getImportantField();
    const valid = this.value.includes(imp);

    return {
      status: valid,
      issues: [
        ...!valid ? [
          new UserMessage(MessageType.FAILED, 'Query should be grouped by at least ' + imp + ' field'),
        ] : []
      ],
    };
  }

  isInFilter(field: Field, filters: any[]) {
    return filters.findIndex((vMap) => {
      return vMap[field];
    }) !== -1;
  }

  addGroup(field: Field) {
    const index = this.fieldsList.indexOf(field);
    this.fieldsList.splice(index, 1);
    this.value.push(field);
    this.emitChange(this.value);
  }

  removeGroup(i: number) {
    const group = this.value[i];
    this.fieldsList.push(group);
    this.filterGoup.forEach((vMap) => {
      delete vMap[group];
    });
    this.thresholds.forEach((t) => {
      delete t.values[group];
    });
    this.value.splice(i, 1);
    if (this.value.length === 0) {
      this.filterGoup.length = 0;
      this.thresholds.length = 0;
    }
    this.emitChange(this.value);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['analysis']) {
      this.fieldsList = [...fieldsForAnalysis[changes['analysis'].currentValue]].filter((field) => field !== Field.TABLE);
      if (changes['analysis'].isFirstChange()) {
        this.value.forEach((field) => {
          const index = this.fieldsList.indexOf(field);
          if (index !== -1) {
            this.fieldsList.splice(index, 1);
          }
        });
      } else {
        this.value = [];
        this.emitChange(this.value);
      }
    }

    if (changes['value']) {
      this.emitValid(this.value);
    }
  }

  getImportantField() {
    switch (this.analysis) {
      case AnalysisType.PATIENTS_DETECTIONS:
      case AnalysisType.VACCINCATIONS_DETECTIONS:
        return Field.DETECTION;
      case AnalysisType.PATIENTS_VOCABULARY:
      case AnalysisType.VACCINCATIONS_VOCABULARY:
        return Field.CODE;
      case AnalysisType.VACCINCATIONS:
        return Field.VACCINE_CODE;
    }
  }

  ngOnInit(): void {
  }

}
