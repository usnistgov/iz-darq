import {Component, Input, OnInit} from '@angular/core';
import {Labelizer} from '../../../shared/services/values.service';
import {IComparisonRow, ITableDifferential, MeasureOutcome} from '../../model/comparison.model';
import {ReportComparisonService} from '../../services/report-comparison.service';

@Component({
  selector: 'app-comparison-table',
  templateUrl: './comparison-table.component.html',
  styleUrls: ['./comparison-table.component.scss']
})
export class ComparisonTableComponent implements OnInit {

  outcomes = MeasureOutcome;
  rows: IComparisonRow[] = [];

  @Input()
  baseLabel: string;

  @Input()
  newLabel: string;

  @Input()
  changesOnly: boolean;

  @Input()
  set table(table: ITableDifferential) {
    this.ptable = table;
    this.buildRows();
  }

  get table() {
    return this.ptable;
  }

  @Input()
  set labelizer(labelizer: Labelizer) {
    this.plabelizer = labelizer;
    this.buildRows();
  }

  get labelizer() {
    return this.plabelizer;
  }

  private ptable: ITableDifferential;
  private plabelizer: Labelizer;

  constructor(private comparisonService: ReportComparisonService) { }

  buildRows() {
    if (!this.ptable) {
      this.rows = [];
      return;
    }
    this.rows = (this.ptable.rowDifferentials || [])
      .map((row) => this.comparisonService.toComparisonRow(this.ptable, row, this.plabelizer));
  }

  get visibleRows(): IComparisonRow[] {
    return this.changesOnly ? this.rows.filter((row) => this.comparisonService.rowHasChange(row)) : this.rows;
  }

  rowIdentifier(row: IComparisonRow): string {
    // The caption is already shown as the table header, only the identifier varies per row
    const caption = this.ptable && this.ptable.tableCaption ? this.ptable.tableCaption : '';
    if (caption && row.measureName.startsWith(caption)) {
      const rest = row.measureName.substring(caption.length).replace(/^\s*—\s*/, '');
      return rest || caption;
    }
    return row.measureName;
  }

  ngOnInit(): void {
  }

}
