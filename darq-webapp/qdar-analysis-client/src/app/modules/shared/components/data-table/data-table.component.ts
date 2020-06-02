import { Component, OnInit, Input, Inject } from '@angular/core';
import { IDataTable, IFraction } from '../../../report/model/report.model';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Labelizer } from '../../services/values.service';
import { Field, AnalysisType } from '../../../report-template/model/analysis.values';
import { SelectItem } from 'primeng/api/selectitem';
import { IFieldInputOptions } from '../field-input/field-input.component';
import { Comparator, IThreshold } from '../../../report-template/model/report-template.model';
import { Table } from 'primeng/table/table';

export interface IDataTableRowDisplay {
  fraction: IFraction;
  count: number;
  total: number;
  percentage: number;
  pass: boolean;
  threshold: IThreshold;
}

export interface IFieldValue {
  [key: string]: string;
}

export interface IColumn {
  key: string;
  type: ColumnType;
  label: string;
}

export enum ColumnType {
  FIELD, BAR, VALUE, THRESHOLD,
}

export type Row = IDataTableRowDisplay | IFieldValue;

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.scss']
})
export class DataTableComponent implements OnInit {

  ColumnType = ColumnType;

  @Input()
  table: IDataTable;

  @Input()
  labelizer: Labelizer;

  columns: IColumn[];
  searchFields: string[];
  rows: Row[] = [];
  fieldValues: {
    [field: string]: SelectItem[];
  } = {};
  searchOptions: IFieldInputOptions;
  initialized = false;
  thresholdOptions = [{
    label: 'Pass',
    value: true,
  },
  {
    label: 'Fail',
    value: false,
  }];

  comparatorOptions = [{
    label: 'GT',
    value: Comparator.GT,
  },
  {
    label: 'LT',
    value: Comparator.LT,
  },
  {
    label: 'EQ',
    value: Comparator.EQ,
  }];
  search: {
    fields: {
      [key: string]: string;
    },
    threshold: boolean;
    showValue: boolean;
    value: {
      comparator: Comparator;
      value: number;
    }
  } = {
      fields: {},
      threshold: null,
      showValue: false,
      value: {
        comparator: null,
        value: null,
      }
    };

  constructor() {
  }

  valueFilterIsSet(): boolean {
    return this.search.value.comparator !== null && this.search.value.value !== null;
  }

  thresholdFilterIsSet(): boolean {
    return this.search.threshold !== null;
  }

  clearField(table: Table, key: string) {
    this.search.fields[key] = '';
    table.filter('', key, 'contains');
  }

  clearValue(table: Table) {
    this.search.value.comparator = null;
    this.search.value.value = null;
    this.filterValue(table);
  }

  filterValue(table: Table) {
    if (this.valueFilterIsSet()) {
      table.filter(this.search.value.value, 'percentage', this.getFilterMatchMode(this.search.value.comparator));
    } else {
      table.filter('', 'percentage', 'contains');
    }
  }


  filterThreshold(table: Table) {
    if (this.thresholdFilterIsSet()) {
      table.filter(this.search.threshold, 'pass', 'equals');
    } else {
      table.filter('', 'pass', 'contains');
    }
  }

  getFilterMatchMode(comparator: Comparator): string {
    switch (comparator) {
      case Comparator.EQ:
        return 'equals';
      case Comparator.LT:
        return 'lt';
      case Comparator.GT:
        return 'gt';
      default:
        return 'contains';
    }
  }

  ngOnInit(): void {
    this.columns = [
      ...this.table.headers.map((group) => {
        return {
          key: group,
          type: ColumnType.FIELD,
          label: group,
        };
      }),
      {
        key: 'percentage',
        type: ColumnType.VALUE,
        label: 'Value',
      },
      {
        key: 'pass',
        type: ColumnType.THRESHOLD,
        label: 'Threshold',
      },
      {
        key: 'percentage',
        type: ColumnType.BAR,
        label: 'Bar',
      }
    ];

    const values: {
      [field: string]: {
        [value: string]: string;
      }
    } = {};

    this.rows = this.table.values.map((row) => {
      const labelized = {};
      Object.keys(row.values).forEach((field) => {
        labelized[field] = this.labelizer.for(field as Field, row.values[field]);
        if (!values[field]) {
          values[field] = {};
        }
        values[field][row.values[field]] = labelized[field];
      });

      return {
        ...labelized,
        count: row.result.count,
        total: row.result.total,
        percentage: (row.result.count / row.result.total) * 100,
        pass: row.pass,
        threshold: row.threshold,
        fraction: row.result,
      };
    });

    const tableOptions = Object.keys(values[Field.TABLE] || {}).map((value) => {
      return {
        value: values[Field.TABLE][value],
        label: values[Field.TABLE][value],
      };
    });

    this.searchOptions = {
      ageGroupOptions: Object.keys(values[Field.AGE_GROUP] || {}).map((value) => {
        return {
          value: values[Field.AGE_GROUP][value],
          label: values[Field.AGE_GROUP][value],
        };
      }),
      vaccinationDetectionOptions:
        this.table.type === AnalysisType.VACCINCATIONS_DETECTIONS ? Object.keys(values[Field.DETECTION] || {}).map((value) => {
          return {
            value: values[Field.DETECTION][value],
            label: values[Field.DETECTION][value],
          };
        }) : [],
      patientDetectionOptions:
        this.table.type === AnalysisType.PATIENTS_DETECTIONS ? Object.keys(values[Field.DETECTION] || {}).map((value) => {
          return {
            value: values[Field.DETECTION][value],
            label: values[Field.DETECTION][value],
          };
        }) : [],
      cvxOptions: Object.keys(values[Field.VACCINE_CODE] || {}).map((value) => {
        return {
          value: values[Field.VACCINE_CODE][value],
          label: values[Field.VACCINE_CODE][value],
        };
      }),
      eventOptions: this.labelizer.options.eventOptions,
      patientTableOptions: this.table.type === AnalysisType.PATIENTS_VOCABULARY ? tableOptions : [],
      vaccinationTableOptions: this.table.type === AnalysisType.VACCINCATIONS_VOCABULARY ? tableOptions : [],
    };

    this.searchFields = this.columns.map((c) => c.key);
    this.initialized = true;
  }

}
