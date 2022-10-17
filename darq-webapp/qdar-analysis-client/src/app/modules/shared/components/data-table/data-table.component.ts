import { IThreshold } from 'src/app/modules/report-template/model/report-template.model';
import { Component, Input, OnChanges } from '@angular/core';
import { IDataTable } from '../../../report/model/report.model';
import { Labelizer } from '../../services/values.service';
import { fieldDisplayName } from '../../../report-template/model/analysis.values';
import { SelectItem } from 'primeng/api/selectitem';
import { IFieldInputOptions } from '../field-input/field-input.component';
import { Comparator } from '../../../report-template/model/report-template.model';
import { Table } from 'primeng/table/table';
import { ColumnType, DataTableService, IColumn, Row, IRowTree, IRowTreeData } from '../../services/data-table.service';
import { TreeTable } from 'primeng/treetable/treetable';

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.scss']
})
export class DataTableComponent implements OnChanges {

  ColumnType = ColumnType;
  @Input()
  table: IDataTable;
  @Input()
  labelizer: Labelizer;
  fieldDisplayName = fieldDisplayName;
  columns: IColumn[];
  searchFields: string[];
  rows: Row[] = [];
  tree: IRowTree[] = [];
  showTable = true;
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

  constructor(private dataTableService: DataTableService) {
  }

  valueFilterIsSet(): boolean {
    return this.search.value.comparator !== null && this.search.value.value !== null;
  }

  thresholdFilterIsSet(): boolean {
    return this.search.threshold !== null;
  }

  clearField(table: Table | TreeTable, key: string) {
    this.search.fields[key] = '';
    table.filter('', key, 'contains');
  }

  clearValue(table: Table | TreeTable) {
    this.search.value.comparator = null;
    this.search.value.value = null;
    this.filterValue(table);
  }

  filterValue(table: Table | TreeTable) {
    if (this.valueFilterIsSet()) {
      table.filter(this.search.value.value, 'percentage', this.getFilterMatchMode(this.search.value.comparator));
    } else {
      table.filter('', 'percentage', 'contains');
    }
  }


  filterThreshold(table: Table | TreeTable) {
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

  disabledTreeCell(column: IColumn, value: IRowTreeData): boolean {
    switch (column.type) {
      case ColumnType.BAR: return value.groupHeader;
      case ColumnType.FIELD: return value.groupHeader !== column.denominator;
      case ColumnType.GROUP: return !value.groupHeader;
      case ColumnType.THRESHOLD: return value.groupHeader;
      case ColumnType.VALUE: return value.groupHeader;
    }
  }

  ngOnChanges(): void {
    this.columns = this.dataTableService.getColumns(this.table);
    const { rows, values } = this.dataTableService.getTableRows(this.table, this.labelizer);
    this.rows = rows;
    this.tree = this.dataTableService.getTreeFromRows(rows, this.columns);
    this.searchOptions = this.dataTableService.getSearchOptions(values, this.table.query.type);
    this.searchFields = this.columns.map((c) => c.key);
    this.initialized = true;
  }


  public writeCSV(): string {
    const rows = this.rows;
    const columns = this.columns;
    const hasGroup = columns.findIndex((c) => c.key === 'group') !== -1;
    const fields = columns.filter((c) => c.type === ColumnType.FIELD);
    const fieldsHeader = fields.reduce((acc, f) => `${acc}${f.denominator ? 'GROUP_FIELD:' : 'FIELD:'}${f.key},`, '');
    const header = `${hasGroup ? 'GROUP_ID,' : ''}${fieldsHeader}NUMERATOR,DENOMINATOR,PERCENTAGE,THRESHOLD_TYPE,THRESHOLD_VALUE,THRESHOLD_EVALUATION`;

    const rowsValues = rows.map((r) => {
      const group = hasGroup ? `${r.group},` : '';
      const fieldsValue = fields.reduce((acc, f) => `${acc}${this.escapeValue(r[f.key])},`, '');
      const thresholdType = r.threshold ? (r.threshold as IThreshold).comparator : 'NONE';
      const thresholdValue = r.threshold ? (r.threshold as IThreshold).value : 'NONE';
      const thresholdActivation = r.threshold ? (r.pass ? 'PASS' : 'FAIL') : 'NONE';
      return `${group}${fieldsValue}${r.count},${r.total},${Number(r.percentage).toFixed(2)},${thresholdType},${thresholdValue},${thresholdActivation}`;
    });

    return [header, ...rowsValues].join('\n');
  }

  public downloadCSV() {
    const value = this.writeCSV();
    const file = new window.Blob([value], { type: 'text/csv' });
    const a = document.createElement('a');
    a.style.display = 'none';
    const fileURL = URL.createObjectURL(file);
    a.href = fileURL;
    a.download = `qdar_table.csv`;
    a.click();
    a.remove();
  }

  public escapeValue(value: string) {
    const needsEscape = value.includes(',') || value.includes('"');
    return needsEscape ? `"${value.replace(/"/g, '""')}"` : value;
  }


}
