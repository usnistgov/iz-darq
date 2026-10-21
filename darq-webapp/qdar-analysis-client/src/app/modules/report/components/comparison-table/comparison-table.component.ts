import {Component, Input, OnInit} from '@angular/core';
import {Field} from '../../../report-template/model/analysis.values';
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
  // One column per row identifier field (Reporting Group, Detection Type, ...)
  identifierFields: Field[] = [];

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
      this.identifierFields = [];
      return;
    }
    this.identifierFields = this.comparisonService.identifierFields(this.ptable);
    this.rows = (this.ptable.rowDifferentials || [])
      .map((row) => this.comparisonService.toComparisonRow(this.ptable, row, this.plabelizer));
  }

  get visibleRows(): IComparisonRow[] {
    return this.changesOnly ? this.rows.filter((row) => this.comparisonService.rowHasChange(row)) : this.rows;
  }

  identifierFieldLabel(field: Field): string {
    return this.comparisonService.identifierFieldLabel(field);
  }

  identifierValue(row: IComparisonRow, field: Field): string {
    return (row.identifier && row.identifier[field]) || '-';
  }

  ngOnInit(): void {
  }

}
