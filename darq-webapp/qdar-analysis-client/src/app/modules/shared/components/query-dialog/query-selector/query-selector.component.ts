import { Component, OnInit, Input, ViewChild, EventEmitter, OnDestroy, Output, OnChanges, SimpleChanges, AfterViewInit } from '@angular/core';
import { Field, fieldsForAnalysis, AnalysisType } from '../../../../report-template/model/analysis.values';
import { IDataSelector } from '../../../../report-template/model/report-template.model';
import { IFieldInputOptions } from '../../field-input/field-input.component';
import { NgForm } from '@angular/forms';
import { map } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { UserMessage, MessageType } from 'ngx-dam-framework';
import { QueryDialogTabComponent } from '../query-dialog-tab/query-dialog-tab.component';

@Component({
  selector: 'app-query-selector',
  templateUrl: './query-selector.component.html',
  styleUrls: ['./query-selector.component.scss']
})
export class QuerySelectorComponent extends QueryDialogTabComponent<IDataSelector[]> implements OnInit, OnDestroy, OnChanges {
  @Input()
  options: IFieldInputOptions;
  @Input()
  analysis: AnalysisType;
  @ViewChild('form', { static: true })
  form: NgForm;
  fieldsList: Field[];
  vSub: Subscription;

  constructor() {
    super();
  }

  addValue(selector: IDataSelector) {
    selector.values.push({
      value: undefined,
    });
  }

  removeValue(list: any[], i: number) {
    list.splice(i, 1);
  }

  removeSelector(i: number) {
    const selector = this.value[i];
    this.fieldsList.push(selector.field);
    this.value.splice(i, 1);
  }

  addSelector(field: Field) {
    const index = this.fieldsList.indexOf(field);
    this.fieldsList.splice(index, 1);
    this.value.push({
      field,
      values: [{
        value: undefined,
      }],
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['analysis']) {
      this.fieldsList = [...fieldsForAnalysis[changes['analysis'].currentValue]];
      if (changes['analysis'].isFirstChange()) {
        this.value.forEach((v) => {
          const index = this.fieldsList.indexOf(v.field);
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

  validate(): { status: boolean; issues: UserMessage<any>[]; } {
    const imp = this.getImportantField();
    const valid = !imp || this.value.findIndex((selector) => selector.field === imp) !== -1;

    return {
      status: valid && this.form.valid,
      issues: [
        ...!valid ? [
          new UserMessage(MessageType.FAILED, 'Query should be filtered by at least ' + imp + ' field'),
        ] : []
      ],
    };
  }

  getImportantField() {
    switch (this.analysis) {
      case AnalysisType.PATIENTS_VOCABULARY:
      case AnalysisType.VACCINCATIONS_VOCABULARY:
        return Field.TABLE;
      default:
        return undefined;
    }
  }

  ngOnInit(): void {
    this.vSub = this.form.valueChanges.pipe(
      map(() => {
        this.emitChange(this.value);
      }),
    ).subscribe();
  }

  ngOnDestroy() {
    if (this.vSub) {
      this.vSub.unsubscribe();
    }
  }

}
