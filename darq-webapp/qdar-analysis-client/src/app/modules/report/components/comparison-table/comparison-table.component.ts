import {Component, Input, OnInit} from '@angular/core';
import {Field} from '../../../report-template/model/analysis.values';
import {Comparator} from '../../../report-template/model/report-template.model';
import {Labelizer} from '../../../shared/services/values.service';
import {SelectItem} from 'primeng/api/selectitem';
import {Table} from 'primeng/table/table';
import {IComparisonRow, ITableDifferential, MeasureOutcome} from '../../model/comparison.model';
import {ReportComparisonService} from '../../services/report-comparison.service';

/**
 * Flat shape handed to p-table: identifier values are spread as top level keys so
 * they can be sorted and filtered by field name like the analysis report tables.
 */
export interface IComparisonTableRow extends IComparisonRow {
  [field: string]: any;
  baseOutcome: string;
  newOutcome: string;
}

@Component({
  selector: 'app-comparison-table',
  templateUrl: './comparison-table.component.html',
  styleUrls: ['./comparison-table.component.scss']
})
export class ComparisonTableComponent implements OnInit {

  outcomes = MeasureOutcome;
  rows: IComparisonTableRow[] = [];
  // One column per row identifier field (Reporting Group, Detection Type, ...)
  identifierFields: Field[] = [];
  searchFields: string[] = [];

  outcomeOptions: SelectItem[] = [
    { label: MeasureOutcome.BETTER, value: MeasureOutcome.BETTER },
    { label: MeasureOutcome.WORSE, value: MeasureOutcome.WORSE },
    { label: MeasureOutcome.NO_CHANGE, value: MeasureOutcome.NO_CHANGE },
  ];

  passOptions: SelectItem[] = [
    { label: 'Met', value: true },
    { label: 'Not Met', value: false },
  ];

  comparatorOptions: SelectItem[] = [
    { label: 'GT', value: Comparator.GT },
    { label: 'LT', value: Comparator.LT },
    { label: 'EQ', value: Comparator.EQ },
  ];

  search: {
    fields: { [key: string]: string };
    outcome: MeasureOutcome;
    basePass: boolean;
    newPass: boolean;
    value: { [key: string]: { comparator: Comparator, value: number } };
  } = {
    fields: {},
    outcome: null,
    basePass: null,
    newPass: null,
    value: {
      basePercent: { comparator: null, value: null },
      newPercent: { comparator: null, value: null },
      pointChange: { comparator: null, value: null },
    },
  };

  @Input()
  baseLabel: string;

  @Input()
  newLabel: string;

  @Input()
  set changesOnly(changesOnly: boolean) {
    this.pchangesOnly = changesOnly;
    this.buildRows();
  }

  get changesOnly() {
    return this.pchangesOnly;
  }

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
  private pchangesOnly: boolean;

  constructor(private comparisonService: ReportComparisonService) { }

  buildRows() {
    if (!this.ptable) {
      this.rows = [];
      this.identifierFields = [];
      this.searchFields = [];
      return;
    }
    this.identifierFields = this.comparisonService.identifierFields(this.ptable);
    this.searchFields = [...this.identifierFields, 'thresholdText', 'outcome'];
    this.rows = (this.ptable.rowDifferentials || [])
      .map((row) => this.comparisonService.toComparisonRow(this.ptable, row, this.plabelizer))
      .filter((row) => !this.pchangesOnly || this.comparisonService.rowHasChange(row))
      .map((row) => this.toTableRow(row));
  }

  private toTableRow(row: IComparisonRow): IComparisonTableRow {
    return {
      ...row,
      ...row.identifier,
      baseOutcome: row.basePass ? 'Met' : 'Not Met',
      newOutcome: row.missingInNewReport ? '' : (row.newPass ? 'Met' : 'Not Met'),
    };
  }

  identifierFieldLabel(field: Field): string {
    return this.comparisonService.identifierFieldLabel(field);
  }

  valueFilterIsSet(key: string): boolean {
    const filter = this.search.value[key];
    return filter.comparator !== null && filter.value !== null;
  }

  filterValue(table: Table, key: string) {
    if (this.valueFilterIsSet(key)) {
      table.filter(this.search.value[key].value, key, this.getFilterMatchMode(this.search.value[key].comparator));
    } else {
      table.filter('', key, 'contains');
    }
  }

  clearValue(table: Table, key: string) {
    this.search.value[key] = { comparator: null, value: null };
    this.filterValue(table, key);
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

  filterPass(table: Table, key: string, value: boolean) {
    table.filter(value === null || value === undefined ? '' : value, key, value === null ? 'contains' : 'equals');
  }

  filterOutcome(table: Table) {
    table.filter(this.search.outcome ? this.search.outcome : '', 'outcome', this.search.outcome ? 'equals' : 'contains');
  }

  get columnCount(): number {
    // identifier columns + threshold, base value/outcome, new value/outcome, change, outcome
    return this.identifierFields.length + 7;
  }

  ngOnInit(): void {
  }

}
