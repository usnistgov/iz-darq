import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import * as _ from 'lodash';
import { IFieldInputOptions } from '../../../shared/components/field-input/field-input.component';
import { IQueryResultFilter, Comparator, IReportFilter, IValueContainer } from '../../../report-template/model/report-template.model';
import { AnalysisType, Field } from '../../../report-template/model/analysis.values';

@Component({
  selector: 'app-report-filter-dialog',
  templateUrl: './report-filter-dialog.component.html',
  styleUrls: ['./report-filter-dialog.component.scss']
})
export class ReportFilterDialogComponent implements OnInit {

  options: IFieldInputOptions;
  value: IReportFilter;
  analysis: AnalysisType;
  groupBy: Field[];
  fields = [
    'PROVIDER',
    'AGE_GROUP',
    'CODE',
    'EVENT',
    'GENDER',
    'VACCINE_CODE',
    'VACCINATION_YEAR',
  ];

  constructor(
    public dialogRef: MatDialogRef<ReportFilterDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.options = data.options;
    this.value = _.cloneDeep(data.value);
    this.analysis = data.analysis;
    this.groupBy = data.groupBy;
  }

  comparatorOptions = [{
    label: 'Greater Than',
    value: Comparator.GT,
  },
  {
    label: 'Lower Than',
    value: Comparator.LT,
  }];

  addFieldValue(field: string) {
    this.value.fields.fields[field].push({
      value: undefined,
    });
  }

  isValid(values: IValueContainer[]) {
    return values.filter((v) => !v.value).length === 0;
  }

  removeFieldValue(values: IValueContainer[], i: number) {
    values.splice(i, 1);
  }

  triggerChange() {

  }

  ngOnInit(): void {
  }

}
