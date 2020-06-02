import { Component, OnInit, ViewChild, Input, OnDestroy, SimpleChanges, OnChanges } from '@angular/core';
import { QueryDialogTabComponent } from '../query-dialog-tab/query-dialog-tab.component';
import { IDataViewQuery, Comparator, IComplexThreshold } from '../../../../report-template/model/report-template.model';
import { NgForm } from '@angular/forms';
import { IFieldInputOptions } from '../../field-input/field-input.component';
import { UserMessage } from 'ngx-dam-framework';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-query-threshold',
  templateUrl: './query-threshold.component.html',
  styleUrls: ['./query-threshold.component.scss']
})
export class QueryThresholdComponent extends QueryDialogTabComponent<IDataViewQuery> implements OnInit, OnDestroy, OnChanges {

  comparatorOptions = [{
    label: 'Greater Than',
    value: Comparator.GT,
  },
  {
    label: 'Lower Than',
    value: Comparator.LT,
  }];
  @ViewChild('form', { static: true })
  form: NgForm;
  @Input()
  options: IFieldInputOptions;
  vSub: Subscription;

  constructor() {
    super();
  }

  validate(value: IDataViewQuery): { status: boolean; issues: UserMessage<any>[]; } {
    return {
      status: this.form.valid,
      issues: [],
    };
  }

  addCustomThreshold(customThreshold: IComplexThreshold[]) {
    customThreshold.push({
      values: {},
      goal: {
        comparator: Comparator.GT,
        value: 0,
      }
    });
    this.emitChange(this.value);
  }

  removeCustomThreshold(customThreshold: any[], i: number) {
    customThreshold.splice(i, 1);
    this.emitChange(this.value);
  }

  addFieldValue(valuesMap, field) {
    valuesMap[field] = {
      value: undefined,
    };
    this.emitChange(this.value);
  }

  clearFieldValue(valuesMap, field) {
    delete valuesMap[field];
    this.emitChange(this.value);
  }

  triggerChange() {
    this.emitChange(this.value);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['analysis'] && !changes['analysis'].isFirstChange()) {
      this.value.threshold.custom.thresholds = [];
      this.emitChange(this.value);
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
