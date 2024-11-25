import { Component, Input, OnChanges, OnDestroy, OnInit, Optional } from '@angular/core';
import { IDataTable } from '../../../report/model/report.model';
import { Labelizer } from '../../services/values.service';
import { fieldDisplayName } from '../../../report-template/model/analysis.values';
import { SelectItem } from 'primeng/api/selectitem';
import { IFieldInputOptions } from '../field-input/field-input.component';
import { Comparator } from '../../../report-template/model/report-template.model';
import { Table } from 'primeng/table/table';
import { ColumnType, DataTableService, IColumn, Row, IRowTree, IRowTreeData } from '../../services/data-table.service';
import { TreeTable } from 'primeng/treetable/treetable';
import { DataExportCSVService } from '../../services/data-export-csv.service';
import { ReportTablesService } from 'src/app/modules/report/services/report-tables.service';

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.scss']
})
export class DataTableComponent implements OnChanges, OnInit, OnDestroy {

  ColumnType = ColumnType;
  @Input()
  table: IDataTable;
  @Input()
  labelizer: Labelizer;
  @Input()
  tableId: string;
  uniqueId: string;
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

  constructor(
    private dataTableService: DataTableService,
    @Optional() private reportTablesService: ReportTablesService,
    private csvExportService: DataExportCSVService) {
    this.uniqueId = new Date().getTime() + "";
  }

  ngOnInit(): void {
    if (this.reportTablesService) {
      this.reportTablesService.addTable(this.uniqueId, this);
    }
  }

  ngOnDestroy(): void {
    if (this.reportTablesService) {
      this.reportTablesService.removeTable(this.uniqueId);
    }
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

  public getFileName() {
    return this.tableId ? this.tableId.replace(/ /g, "_").replace(/\./g, "-").toLowerCase() + ".csv" : `qdar_table_${new Date().getTime()}.csv`;
  }

  public getCSVFileContent() {
    return this.csvExportService.writeCSV(this.rows, this.columns);
  }

  public downloadCSV() {
    const value = this.getCSVFileContent();
    const filename = this.getFileName();
    const file = new window.Blob([value], { type: 'text/csv' });
    const a = document.createElement('a');
    a.style.display = 'none';
    const fileURL = URL.createObjectURL(file);
    a.href = fileURL;
    a.download = filename;
    a.click();
    a.remove();
  }

}
