import { Component, OnInit, forwardRef, ViewChildren, QueryList, AfterViewInit, ContentChildren, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import {
  IDamDataModel,
  DamWidgetComponent,
  selectIsAdmin,
  ConfirmDialogComponent,
  RxjsStoreHelperService,
  MessageType,
  LoadPayloadData
} from 'ngx-dam-framework';
import { Observable, of, BehaviorSubject, combineLatest } from 'rxjs';
import { IReport, IReportSectionResult } from '../../model/report.model';
import { selectReport, selectReportIsViewOnly, selectReportGeneralFilter } from '../../store/core.selectors';
import { ITocNode } from '../report-toc/report-toc.component';
import { map, concatMap, flatMap, take } from 'rxjs/operators';
import { ReportService } from '../../services/report.service';
import { IReportFilter, Comparator } from '../../../report-template/model/report-template.model';
import { selectAllDetections, selectAllCvx, selectPatientTables, selectVaccinationTables } from '../../../shared/store/core.selectors';
import { ValuesService, Labelizer } from '../../../shared/services/values.service';
import { ReportFilterDialogComponent } from '../report-filter-dialog/report-filter-dialog.component';
import { IFieldInputOptions } from '../../../shared/components/field-input/field-input.component';
import { SetValue } from 'ngx-dam-framework';

export const REPORT_WIDGET = 'REPORT_WIDGET';


@Component({
  selector: 'app-report-widget',
  templateUrl: './report-widget.component.html',
  styleUrls: ['./report-widget.component.scss'],
  providers: [
    { provide: DamWidgetComponent, useExisting: forwardRef(() => ReportWidgetComponent) },
  ],
})
export class ReportWidgetComponent extends DamWidgetComponent implements OnInit, AfterViewInit {

  report$: Observable<IReport>;
  filtered$: Observable<IReport>;
  isAdmin$: Observable<boolean>;
  nodes$: Observable<ITocNode[]>;
  isViewOnly$: Observable<boolean>;
  generalFilter$: Observable<IReportFilter>;
  labelizer$: Observable<IFieldInputOptions>;

  constructor(
    store: Store<IDamDataModel>,
    dialog: MatDialog,
    private helper: RxjsStoreHelperService,
    private reportService: ReportService,
    private valueService: ValuesService,
  ) {
    super(REPORT_WIDGET, store, dialog);
    this.generalFilter$ = this.store.select(selectReportGeneralFilter);
    this.report$ = this.store.select(selectReport);
    this.isAdmin$ = this.store.select(selectIsAdmin);
    this.isViewOnly$ = this.store.select(selectReportIsViewOnly);
    this.nodes$ = this.report$.pipe(
      map((report) => {
        return this.makeTocNode(report.sections);
      }),
    );
    this.labelizer$ = combineLatest([
      this.store.select(selectReport),
      this.store.select(selectAllDetections),
      this.store.select(selectAllCvx),
      this.store.select(selectPatientTables),
      this.store.select(selectVaccinationTables),
    ]).pipe(
      map(([report, detections, cvxCodes, patientTables, vaccinationTables]) => {
        return this.valueService.getFieldOptions({
          detections,
          ageGroups: report.configuration.ageGroups,
          cvxs: cvxCodes,
          tables: {
            vaccinationTables,
            patientTables,
          }
        });
      }),
    );
  }

  get filterIsActive$() {
    return this.generalFilter$.pipe(
      map((filter) => {
        return filter && (filter.denominator.active || filter.fields.active || filter.percentage.active || filter.threshold.active);
      })
    );
  }

  publish() {
    this.store.select(selectReport).pipe(
      take(1),
      flatMap((report) => {
        return this.dialog.open(ConfirmDialogComponent, {
          data: {
            action: 'Publish Report',
            question: 'Are you sure you want to publish report ' + report.name + ',<br> this will make it globally available for all users of the facility ?',
          },
        }).afterClosed().pipe(
          concatMap((answer) => {
            if (answer) {
              return this.helper.getMessageAndHandle<IReport>(
                this.store,
                () => {
                  return this.reportService.publish(report.id);
                },
                (message) => {
                  return message.status === MessageType.SUCCESS ? [new LoadPayloadData(message.data)] : [];
                }
              );
            }
            return of();
          }),
        );
      })
    ).subscribe();
  }

  ngAfterViewInit(): void {
  }

  makeTocNode(sections: IReportSectionResult[]): ITocNode[] {
    if (!sections) {
      return [];
    }

    return sections.map((s) => {
      return {
        id: s.id,
        header: s.header,
        path: s.path,
        warning: s.thresholdViolation,
        children: this.makeTocNode(s.children),
      };
    });
  }

  filterReport() {
    combineLatest([
      this.labelizer$,
      this.generalFilter$,
    ]).pipe(
      take(1),
      flatMap(([options, generalFilter]) => {
        return this.dialog.open(ReportFilterDialogComponent, {
          data: {
            value: generalFilter,
            options,
          }
        }).afterClosed().pipe(
          map((filters) => {
            if (filters) {
              this.store.dispatch(new SetValue({
                reportGeneralFilter: filters,
              }));
            }
          }),
        );
      }),
    ).subscribe();
  }

  ngOnInit(): void {
  }

}
