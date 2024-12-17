import { IQueryVariableRefInstance } from './../model/query-variable.model';
import { Injectable } from '@angular/core';
import { IThreshold } from '../../report-template/model/report-template.model';
import { ColumnType, IColumn, Row } from './data-table.service';

export interface ITableFlags {
  hasGroup: boolean;
  hasDenominatorVariable: boolean;
  hasNumeratorVariable: boolean;
}

export enum CSVHeaderFieldType {
  GROUP_ID = 'GROUP_ID',
  NUMERATOR = 'NUMERATOR',
  DENOMINATOR = 'DENOMINATOR',
  PERCENTAGE = 'PERCENTAGE',
  FIELD = 'FIELD',
  THRESHOLD_TYPE = 'THRESHOLD_TYPE',
  THRESHOLD_VALUE = 'THRESHOLD_VALUE',
  THRESHOLD_EVALUATION = 'THRESHOLD_EVALUATION',
  NUMERATOR_VARIABLE_ID = 'NUMERATOR_VARIABLE_ID',
  NUMERATOR_VARIABLE_NAME = 'NUMERATOR_VARIABLE_NAME',
  NUMERATOR_VARIABLE_DESCRIPTION = 'NUMERATOR_VARIABLE_DESCRIPTION',
  NUMERATOR_VARIABLE_TIMESTAMP = 'NUMERATOR_VARIABLE_TIMESTAMP',
  DENOMINATOR_VARIABLE_ID = 'DENOMINATOR_VARIABLE_ID',
  DENOMINATOR_VARIABLE_NAME = 'DENOMINATOR_VARIABLE_NAME',
  DENOMINATOR_VARIABLE_DESCRIPTION = 'DENOMINATOR_VARIABLE_DESCRIPTION',
  DENOMINATOR_VARIABLE_TIMESTAMP = 'DENOMINATOR_VARIABLE_TIMESTAMP',
}

export interface ICSVHeaderField {
  type: CSVHeaderFieldType;
  label: string;
  key: string;
}

@Injectable({
  providedIn: 'root'
})
export class DataExportCSVService {

  getTableFlags(columns: IColumn[]): ITableFlags {
    return {
      hasGroup: columns.findIndex((c) => c.type === ColumnType.GROUP) !== -1,
      hasDenominatorVariable: columns.findIndex((c) => c.type === ColumnType.DENOMINATOR_VARIABLE) !== -1,
      hasNumeratorVariable: columns.findIndex((c) => c.type === ColumnType.NUMERATOR_VARIABLE) !== -1,
    };
  }

  public getHeaders(columns: IColumn[]): ICSVHeaderField[] {
    const flags = this.getTableFlags(columns);
    const fields = columns.filter((c) => c.type === ColumnType.FIELD);

    const h = (type: CSVHeaderFieldType): ICSVHeaderField => {
      return {
        type,
        label: type,
        key: type,
      };
    };

    const pField = (column: IColumn): string => {
      return `${column.denominator ? 'GROUP_FIELD:' : 'FIELD:'}${column.key}`;
    };

    const FIELDS_HEADER = fields.map((f) => ({
      type: CSVHeaderFieldType.FIELD,
      label: pField(f),
      key: f.key,
    }));

    return [
      ...flags.hasGroup ? [
        h(CSVHeaderFieldType.GROUP_ID)
      ] : [],
      ...FIELDS_HEADER,
      ...flags.hasNumeratorVariable ? [
        h(CSVHeaderFieldType.NUMERATOR_VARIABLE_ID),
        h(CSVHeaderFieldType.NUMERATOR_VARIABLE_NAME),
        h(CSVHeaderFieldType.NUMERATOR_VARIABLE_DESCRIPTION),
        h(CSVHeaderFieldType.NUMERATOR_VARIABLE_TIMESTAMP),
      ] : [],
      ...flags.hasDenominatorVariable ? [
        h(CSVHeaderFieldType.DENOMINATOR_VARIABLE_ID),
        h(CSVHeaderFieldType.DENOMINATOR_VARIABLE_NAME),
        h(CSVHeaderFieldType.DENOMINATOR_VARIABLE_DESCRIPTION),
        h(CSVHeaderFieldType.DENOMINATOR_VARIABLE_TIMESTAMP),
      ] : [],
      h(CSVHeaderFieldType.NUMERATOR),
      h(CSVHeaderFieldType.DENOMINATOR),
      h(CSVHeaderFieldType.PERCENTAGE),
      h(CSVHeaderFieldType.THRESHOLD_TYPE),
      h(CSVHeaderFieldType.THRESHOLD_VALUE),
      h(CSVHeaderFieldType.THRESHOLD_EVALUATION),
    ];
  }

  // tslint:disable-next-line: cognitive-complexity
  getHeaderValue(row: Row, header: ICSVHeaderField): string {
    switch (header.type) {
      case CSVHeaderFieldType.GROUP_ID:
        return row.group + '';
      case CSVHeaderFieldType.NUMERATOR:
        return row.count + '';
      case CSVHeaderFieldType.DENOMINATOR:
        return row.total + '';
      case CSVHeaderFieldType.PERCENTAGE:
        return Number(row.percentage).toFixed(2) + '';
      case CSVHeaderFieldType.FIELD:
        return row[header.key];
      case CSVHeaderFieldType.THRESHOLD_TYPE:
        return row.threshold ? (row.threshold as IThreshold).comparator : 'NONE';
      case CSVHeaderFieldType.THRESHOLD_VALUE:
        return row.threshold ? (row.threshold as IThreshold).value + '' : 'NONE';
      case CSVHeaderFieldType.THRESHOLD_EVALUATION:
        return row.threshold ? (row.pass ? 'PASS' : 'FAIL') : 'NONE';
      case CSVHeaderFieldType.NUMERATOR_VARIABLE_ID:
        return row.numeratorVariable ? (row.numeratorVariable as IQueryVariableRefInstance).id : '';
      case CSVHeaderFieldType.NUMERATOR_VARIABLE_NAME:
        return row.numeratorVariable ? (row.numeratorVariable as IQueryVariableRefInstance).name : '';
      case CSVHeaderFieldType.NUMERATOR_VARIABLE_DESCRIPTION:
        return row.numeratorVariable ? (row.numeratorVariable as IQueryVariableRefInstance).description : '';
      case CSVHeaderFieldType.NUMERATOR_VARIABLE_TIMESTAMP:
        return row.numeratorVariable ? (new Date((row.numeratorVariable as IQueryVariableRefInstance).timestamp).toUTCString()) : '';
      case CSVHeaderFieldType.DENOMINATOR_VARIABLE_ID:
        return row.denominatorVariable ? (row.denominatorVariable as IQueryVariableRefInstance).id : '';
      case CSVHeaderFieldType.DENOMINATOR_VARIABLE_NAME:
        return row.denominatorVariable ? (row.denominatorVariable as IQueryVariableRefInstance).name : '';
      case CSVHeaderFieldType.DENOMINATOR_VARIABLE_DESCRIPTION:
        return row.denominatorVariable ? (row.denominatorVariable as IQueryVariableRefInstance).description : '';
      case CSVHeaderFieldType.DENOMINATOR_VARIABLE_TIMESTAMP:
        return row.denominatorVariable ? (new Date((row.denominatorVariable as IQueryVariableRefInstance).timestamp).toUTCString()) : '';
      default:
        return '';
    }
  }

  public writeCSV(rows: Row[], columns: IColumn[], includeColumns = true): string {
    const headers = this.getHeaders(columns);

    return [
      ...(includeColumns ? [headers.map((h) => h.label).join(',')] : []),
      ...(rows || []).map((row) => {
        return headers.map((header) => this.escapeValue(this.getHeaderValue(row, header))).join(',');
      })
    ].join('\n');
  }

  public writeCSVHeader(columns: IColumn[]): string {
    const headers = this.getHeaders(columns);
    return headers.map((h) => h.label).join(',');
  }

  public escapeValue(value: string) {
    const needsEscape = value.includes(',') || value.includes('"');
    return needsEscape ? `"${value.replace(/"/g, '""')}"` : value;
  }
}
