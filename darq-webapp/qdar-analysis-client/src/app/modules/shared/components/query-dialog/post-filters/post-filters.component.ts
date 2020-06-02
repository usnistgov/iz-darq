import { Component, OnInit, Input, ViewChild, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { QueryDialogTabComponent } from '../query-dialog-tab/query-dialog-tab.component';
import { IDataViewQuery, Comparator, IQueryResultFilter } from '../../../../report-template/model/report-template.model';
import { UserMessage, MessageType } from 'ngx-dam-framework';
import { IFieldInputOptions } from '../../field-input/field-input.component';
import { NgForm } from '@angular/forms';
import { map } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { Field } from '../../../../report-template/model/analysis.values';

@Component({
  selector: 'app-post-filters',
  templateUrl: './post-filters.component.html',
  styleUrls: ['./post-filters.component.scss']
})
export class PostFiltersComponent extends QueryDialogTabComponent<IQueryResultFilter> implements OnInit, OnDestroy, OnChanges {

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
  @Input()
  groupBy: Field[];

  constructor() {
    super();
  }

  validate(value: IQueryResultFilter) {
    const hasEmptyRow = value.groups.values.map((vMap) => this.isEmpty(vMap)).includes(true);
    const hasDuplicateRow = value.groups.values.map((vMap, i) => this.hasRow(vMap, i)).includes(true);
    const formValid = this.form.valid;
    const hasInvalidRow = hasEmptyRow && !value.groups.keep;

    return {
      status: !hasInvalidRow && !hasDuplicateRow && formValid,
      issues: [
        ...hasInvalidRow ? [
          new UserMessage(MessageType.FAILED, 'Current post process filter by group field values configuration will filter all records'),
        ] : [],
        ...hasDuplicateRow ? [
          new UserMessage(MessageType.FAILED, 'Current post process filter by group field values has duplicate filter row'),
        ] : [],
        ...(this.value.groups.keep && hasEmptyRow) ? [
          new UserMessage(MessageType.WARNING, 'Current post process filter by group field values configuration will keep all records'),
        ] : [],
      ]
    };
  }

  hasRow(vMap: any, except: number): boolean {
    return this.value.groups.values.findIndex((val, i) => {
      const sameObject = except === i;
      const sameKeySize = Object.keys(vMap).length === Object.keys(val).length;
      const fieldsMatch = !Object.keys(vMap).map((field) => {
        return !!val[field] && vMap[field].value === val[field].value;
      }).includes(false);

      return !sameObject && fieldsMatch && sameKeySize;
    }) !== -1;
  }

  isEmpty(valueMap: any) {
    return Object.keys(valueMap).length === 0;
  }

  addGroupFilter(groups: any[]) {
    groups.push({});
    this.emitChange(this.value);
  }

  removeGroupFilter(groups: any[], i: number) {
    groups.splice(i, 1);
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
      this.value.groups.values = [];
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
