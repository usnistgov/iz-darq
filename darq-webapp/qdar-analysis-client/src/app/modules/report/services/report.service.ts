import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from '@usnistgov/ngx-dam-framework-legacy';
import { IReport, IReportDescriptor, IReportSectionResult, IDataTableRow, IDataTable } from '../model/report.model';
import {
  Comparator,
  IValueContainer,
  IComparatorFilter,
  IThresholdFilter,
  IReportFilter,
  IReportFieldFilter,
  QueryPayloadType,
  QueryType,
} from '../../report-template/model/report-template.model';
import { Field } from '../../report-template/model/analysis.values';
import { NgxCsvParser } from 'ngx-csv-parser';
import { map, catchError } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class ReportService {

  readonly URL_PREFIX = 'api/report/';

  constructor(private http: HttpClient, private ngxCsvParser: NgxCsvParser) { }

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

  getReportingGroupHashMap(file: File): Observable<Record<string, string>> {
    return this.ngxCsvParser.parse(file, { header: false, delimiter: ',' }).pipe(
      map((result: Array<string[]>) => {
        return result.reduce((acc, row) => {
          if (row && row.length === 2) {
            acc[row[1].trim()] = row[0].trim();
          }
          return acc;
        }, {});
      })
    );
  }

  replaceReportingGroupWithHashedValue(filter: IReportFilter, reportingGroups: Record<string, string>) {
    const reverseReportingGroupMap = Object.keys(reportingGroups || {}).reduce((acc, k) => {
      return {
        ...acc,
        [reportingGroups[k]]: k
      };
    }, {});

    if (
      filter.fields &&
      filter.fields.active &&
      Object.keys(filter.fields.fields).includes(Field.PROVIDER) &&
      filter.fields.fields[Field.PROVIDER].length > 0
    ) {
      return {
        ...filter,
        fields: {
          ...filter.fields,
          fields: {
            ...filter.fields.fields,
            [Field.PROVIDER]: filter.fields.fields[Field.PROVIDER].map((cnt) => ({
              value: reverseReportingGroupMap[cnt.value] || cnt.value
            }))
          }
        }
      };
    }
    return filter;
  }

  filterIsActive(filter: IReportFilter): boolean {
    return filter && (filter.denominator.active || filter.fields.active || filter.percentage.active || filter.threshold.active);
  }

  postProcessFilter(report: IReport, filter: IReportFilter): IReport {
    const activeFieldFilters = Object.keys(filter.fields.fields).filter((key) => filter.fields.fields[key].length > 0) as Field[];
    return {
      ...report,
      sections: report.sections
        .map((section) => this.filterSection(section, filter, activeFieldFilters))
    };
  }

  hasValue(children: IReportSectionResult[], data: IDataTable[]) {
    const childHasValue = children?.length > 0 && children.map((child) => child.hasValue).includes(true);
    const hasData = data?.length > 0 && data.map((d) => d.values?.length > 0).includes(true);
    return childHasValue || hasData;
  }

  filterSection(section: IReportSectionResult, filter: IReportFilter, activeFieldFilters: Field[]): IReportSectionResult {
    const data = section.data
      .map((d) => this.filterDataTable(d, filter, activeFieldFilters));
    const children = section.children
      .map((child) => this.filterSection(child, filter, activeFieldFilters));

    return {
      ...section,
      thresholdViolation: data.map((d) => d.thresholdViolation).includes(true),
      data,
      children,
      hasValue: this.hasValue(children, data),
    };
  }

  filterDataTable(table: IDataTable, filter: IReportFilter, activeFieldFilters: Field[]): IDataTable {
    const hasFilterField =
      filter.fields.active &&
      filter.fields &&
      Object.keys(filter.fields).length > 0 &&
      this.getAllFields(table.query).map((field) => {
        return activeFieldFilters.includes(field);
      }).includes(true);
    const values = table.values.filter((row) => {
      return this.comparatorFilter((row.effectiveResult.count / row.effectiveResult.total) * 100, filter.percentage) &&
        this.comparatorFilter(row.effectiveResult.total, filter.denominator) &&
        this.thresholdFilter(row, filter.threshold) &&
        (!hasFilterField || this.fieldFilter(row, filter.fields));
    });

    return {
      ...table,
      thresholdViolation: values.map((d) => d.pass).includes(false),
      values,
    };
  }

  getAllFields(value: QueryType): Field[] {
    switch (value.payloadType) {
      case QueryPayloadType.SIMPLE:
        return [
          ...(value.denominator?.active && value.denominator.field ? [value.denominator.field] : []),
          ...(value.nominator ? [value.nominator] : []),
        ];
      case QueryPayloadType.ADVANCED:
        return [
          ...value.groupBy,
          ...value.occurrences,
        ];
    }
    return [];
  }

  comparatorFilter(value: number, filter: IComparatorFilter) {
    return !filter.active || this.eval(filter.comparator, filter.value, value);

  }

  thresholdFilter(row: IDataTableRow, filter: IThresholdFilter) {
    const rowThresholdActivation = row.threshold ? row.pass : true;
    return !filter.active || rowThresholdActivation === filter.pass;
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
