import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from 'ngx-dam-framework';
import { IReport, IReportDescriptor, IReportSectionResult, IDataTableRow, IDataTable } from '../model/report.model';
import { Comparator, IValueContainer, IComparatorFilter, IThresholdFilter, IReportFilter, IReportFieldFilter } from '../../report-template/model/report-template.model';
import { Field } from '../../report-template/model/analysis.values';


@Injectable({
  providedIn: 'root'
})
export class ReportService {

  readonly URL_PREFIX = 'api/report/';

  constructor(private http: HttpClient) { }

  publish(id: string): Observable<Message<IReport>> {
    return this.http.post<Message<IReport>>(this.URL_PREFIX + 'publish/' + id, {});
  }

  published(): Observable<IReportDescriptor[]> {
    return this.http.post<IReportDescriptor[]>(this.URL_PREFIX + 'published', {});
  }

  publishedForFacility(facility: string): Observable<IReportDescriptor[]> {
    return this.http.post<IReportDescriptor[]>(this.URL_PREFIX + 'published/' + facility, {});
  }

  delete(id: string): Observable<Message<IReport>> {
    return this.http.delete<Message<IReport>>(this.URL_PREFIX + id);
  }


  getById(id: string): Observable<IReport> {
    return this.http.get<IReport>(this.URL_PREFIX + id);
  }

  save(report: IReport): Observable<Message<IReport>> {
    return this.http.post<Message<IReport>>(this.URL_PREFIX, report);
  }

  postProcessFilter(report: IReport, filter: IReportFilter): IReport {
    const activeFieldFilters = Object.keys(filter.fields.fields).filter((key) => filter.fields.fields[key].length > 0) as Field[];
    return {
      ...report,
      sections: report.sections.map((section) => this.filterSection(section, filter, activeFieldFilters))
    };
  }

  filterSection(section: IReportSectionResult, filter: IReportFilter, activeFieldFilters: Field[]): IReportSectionResult {
    return {
      ...section,
      data: section.data.map((data) => this.filterDataTable(data, filter, activeFieldFilters)),
      children: section.children.map((child) => this.filterSection(child, filter, activeFieldFilters))
    };
  }

  filterDataTable(table: IDataTable, filter: IReportFilter, activeFieldFilters: Field[]): IDataTable {
    const hasFilterField = filter.fields.active && filter.fields && Object.keys(filter.fields).length > 0 && table.groupBy.map((field) => {
      return activeFieldFilters.includes(field);
    }).includes(true);

    return {
      ...table,
      values: table.values.filter((row) => {
        return this.comparatorFilter((row.result.count / row.result.total) * 100, filter.percentage) &&
          this.comparatorFilter(row.result.total, filter.denominator) &&
          (!row.threshold || this.thresholdFilter(row, filter.threshold)) &&
          (!hasFilterField || this.fieldFilter(row, filter.fields));
      })
    };
  }

  comparatorFilter(value: number, filter: IComparatorFilter) {
    return !filter.active || this.eval(filter.comparator, filter.value, value);

  }

  thresholdFilter(row: IDataTableRow, filter: IThresholdFilter) {
    return !filter.active || row.pass === filter.pass;
  }

  fieldFilter(row: IDataTableRow, filter: IReportFieldFilter) {
    if (!filter.active || !filter.fields || Object.keys(filter.fields).length === 0) {
      return true;
    }

    if (this.match(filter.fields, row.values)) {
      return filter.keep;
    }
    return !filter.keep;
  }

  match(
    fields: {
      [field: string]: IValueContainer[];
    },
    row: {
      [key: string]: string;
    }
  ): boolean {
    return Object.keys(fields).map((key) => {
      return row[key] && !!fields[key].find((v) => v.value === row[key]);
    }).includes(true);
  }

  eval(comparator: Comparator, target: number, value: number): boolean {
    switch (comparator) {
      case Comparator.GT:
        return value > target;
      case Comparator.LT:
        return value < target;
    }
    return false;
  }



}
