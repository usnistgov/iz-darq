import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import * as _ from 'lodash';
import { BehaviorSubject } from 'rxjs';
import { DataTableComponent } from 'src/app/modules/shared/components/data-table/data-table.component';
import { DataExportCSVService, ICSVHeaderField } from 'src/app/modules/shared/services/data-export-csv.service';

interface ITable {
  id: number;
  name: string;
  columns: ICSVHeaderField[];
}

@Component({
  selector: 'app-report-export-dialog',
  templateUrl: './report-export-dialog.component.html',
  styleUrls: ['./report-export-dialog.component.scss']
})
export class ReportExportDialogComponent implements OnInit {

  tables: ITable[];
  selectedTables: ITable[] = [];
  compatible: BehaviorSubject<ITable[]>;
  exclude = ['NUMERATOR', 'DENOMINATOR', 'PERCENTAGE', 'THRESHOLD_TYPE', 'THRESHOLD_VALUE', 'THRESHOLD_EVALUATION'];
  first = 0;
  pageFirst = 0;
  constructor(
    public dialogRef: MatDialogRef<ReportExportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    dataExportCSVService: DataExportCSVService
  ) {
    this.tables = data.tables.map((table: DataTableComponent) => ({
      id: table.id,
      name: table.tableId,
      columns: dataExportCSVService.getHeaders(table.getColumns()).filter((col) => !this.exclude.includes(col.label)),
    }));
    this.compatible = new BehaviorSubject(this.tables);
  }

  selectionChanged() {
    if (this.selectedTables && this.selectedTables.length > 0) {
      const selected = this.selectedTables[0];
      const filtered = this.tables
        .filter((table) => {
          return this.hasSameColumns(table, selected);
        });
      this.compatible.next(
        filtered
      );
      if (this.pageFirst >= filtered.length) {
        this.first = 0;
      }
    } else {
      this.compatible.next(
        this.tables
      );
    }
  }

  onPage({ first }) {
    this.pageFirst = first;
  }

  hasSameColumns(source: ITable, target: ITable): boolean {
    if (source.columns.length === target.columns.length) {
      for (const sourceColumn of source.columns) {
        if (!this.hasColumn(target.columns, sourceColumn)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  hasColumn(columns: ICSVHeaderField[], column: ICSVHeaderField): boolean {
    return !!columns.find((source) => column.label === source.label);
  }

  selectAll() {
    const tables = this.compatible.getValue();
    this.selectedTables = [
      ...tables,
    ];
  }

  cancel() {
    this.dialogRef.close();
  }

  export() {
    this.dialogRef.close(this.selectedTables.map((table) => table.id));
  }

  ngOnInit(): void {
  }

}
