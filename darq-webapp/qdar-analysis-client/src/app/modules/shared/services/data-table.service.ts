import { Injectable } from '@angular/core';
import { Field, AnalysisType } from '../../report-template/model/analysis.values';
import { IThreshold } from '../../report-template/model/report-template.model';
import { IDataTable, IFraction } from '../../report/model/report.model';
import { Labelizer } from './values.service';

export interface IColumn {
  key: string;
  type: ColumnType;
  label: string;
  denominator?: boolean;
  hide?: boolean;
}

export interface IDataTableRowDisplay {
  id: string;
  group?: number;
  fraction: IFraction;
  count: number;
  total: number;
  percentage: number;
  pass: boolean;
  threshold: IThreshold;
}
export interface IFieldValue<T> {
  [key: string]: T;
}

export interface IRowTree {
  data: IRowTreeData;
  children?: IRowTree[];
}

export interface IRowTreeData extends Record<string, any> {
  groupHeader?: boolean;
}

export type Row = IDataTableRowDisplay | IFieldValue<string>;

export enum ColumnType {
  FIELD, BAR, VALUE, THRESHOLD, GROUP
}

@Injectable({
  providedIn: 'root'
})
export class DataTableService {

  getColumns(table: IDataTable): IColumn[] {
    return [
      ...table.denominator && table.denominator.length > 0 ? [{
        key: 'group',
        type: ColumnType.GROUP,
        label: 'Group',
      }] : [],
      ...table.denominator.map((field) => {
        return {
          key: field,
          type: ColumnType.FIELD,
          denominator: true,
          label: field,
        };
      }),
      ...table.nominator.map((field) => {
        return {
          key: field,
          type: ColumnType.FIELD,
          label: field,
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
  }

  getTreeFromRows(rows: Row[], columns: IColumn[]): IRowTree[] {
    const rootFields = columns.filter((column) => column.denominator);
    if (rootFields.length > 0) {
      const groups: Record<string, Row[]> = rows.reduce((acc, row) => {
        acc[row.group] = [
          ...(acc[row.group] || []),
          row,
        ];
        return acc;
      }, {} as Record<string, Row[]>);
      return Object.keys(groups).map((groupId) => {
        const root: IRowTreeData = rootFields.reduce((acc, field) => {
          acc[field.key] = groups[groupId][0][field.key];
          return acc;
        }, { group: groupId, groupHeader: true } as IRowTreeData);
        return {
          data: root,
          expanded: true,
          children: groups[groupId].map((row) => ({
            data: {
              ...row
            }
          })),
        };
      });
    } else {
      return [];
    }
  }


  getTableRows(table: IDataTable, labelizer: Labelizer): { rows: Row[], values: IFieldValue<Record<string, string>> } {
    const values: IFieldValue<Record<string, string>> = {};
    const rows: Row[] = table.values.map((row, i) => {
      const labelized = {};
      Object.keys(row.values).forEach((field) => {
        labelized[field] = labelizer.for(field as Field, row.values[field]);

        // Collect Row Values
        if (!values[field]) {
          values[field] = {};
        }
        values[field][row.values[field]] = labelized[field];
      });

      return {
        ...labelized,
        group: row.groupId,
        id: i + '',
        count: row.result.count,
        total: row.result.total,
        percentage: (row.result.count / row.result.total) * 100,
        pass: row.pass,
        threshold: row.threshold,
        fraction: row.result,
      };
    });

    return { rows, values };
  }


  getSearchOptions(values: IFieldValue<Record<string, string>>, analysisType: AnalysisType) {
    const getOptionsForField = (field: Field) => {
      return Object.keys(values[field] || {}).map((value) => {
        return {
          value: values[field][value],
          label: values[field][value],
        };
      });
    };
    const tableOptions = getOptionsForField(Field.TABLE);
    const detectionOptions = getOptionsForField(Field.DETECTION);
    return {
      ageGroupOptions: getOptionsForField(Field.AGE_GROUP),
      vaccinationDetectionOptions:
        [AnalysisType.VACCINCATIONS_DETECTIONS]
          .includes(analysisType) ? detectionOptions : [],
      patientDetectionOptions:
        [AnalysisType.PATIENTS_DETECTIONS, AnalysisType.PATIENTS_PROVIDER_DETECTIONS]
          .includes(analysisType) ? detectionOptions : [],
      cvxOptions: getOptionsForField(Field.VACCINE_CODE),
      reportingGroupOptions: getOptionsForField(Field.PROVIDER),
      eventOptions: getOptionsForField(Field.EVENT),
      patientTableOptions: [AnalysisType.PATIENTS_PROVIDER_VOCABULARY, AnalysisType.PATIENTS_VOCABULARY]
        .includes(analysisType) ? tableOptions : [],
      vaccinationTableOptions: [AnalysisType.VACCINCATIONS_VOCABULARY]
        .includes(analysisType) ? tableOptions : [],
    };
  }

}
