import { Component, OnInit, forwardRef, AfterViewInit, ElementRef } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import {
  IDamDataModel,
  DamWidgetComponent,
  ConfirmDialogComponent,
  RxjsStoreHelperService,
  MessageType,
  LoadPayloadData,
  TurnOffLoader
} from 'ngx-dam-framework';
import { Observable, of, combineLatest, from, throwError } from 'rxjs';
import { IReport } from '../../model/report.model';
import { selectReportPayload, selectReportGeneralFilter, selectReportTocNodes, selectReportingGroups } from '../../store/core.selectors';
import { ITocNode } from '../report-toc/report-toc.component';
import { map, concatMap, flatMap, take, tap, catchError } from 'rxjs/operators';
import { ReportService } from '../../services/report.service';
import { IReportFilter } from '../../../report-template/model/report-template.model';
import { selectAllDetections, selectAllCvx, selectPatientTables, selectVaccinationTables } from '../../../shared/store/core.selectors';
import { ValuesService } from '../../../shared/services/values.service';
import { ReportFilterDialogComponent } from '../report-filter-dialog/report-filter-dialog.component';
import { IFieldInputOptions } from '../../../shared/components/field-input/field-input.component';
import { SetValue, MessageService, Message } from 'ngx-dam-framework';
import { PermissionService } from '../../../core/services/permission.service';
import { ResourceType } from 'src/app/modules/core/model/resouce-type.enum';
import { Action } from '../../../core/model/action.enum';
import { IUserFacilityDescriptor } from 'src/app/modules/facility/model/facility.model';
import { selectUserFacilityById } from '../../../aggregate-detections-file/store/core.selectors';
import { PRIVATE_FACILITY_ID } from '../../../aggregate-detections-file/services/file.service';
import { ViewChild } from '@angular/core';
import { NgxCSVParserError } from 'ngx-csv-parser';

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
  nodes$: Observable<ITocNode[]>;
  isViewOnly$: Observable<boolean>;
  generalFilter$: Observable<IReportFilter>;
  dashboardRoute$: Observable<string[]>;
  labelizer$: Observable<IFieldInputOptions>;
  facility$: Observable<IUserFacilityDescriptor>;
  reportingGroupCsvFile: File;
  @ViewChild('fileImportInput', { static: false }) fileImportInput: ElementRef;

  constructor(
    store: Store<IDamDataModel>,
    dialog: MatDialog,
    private helper: RxjsStoreHelperService,
    private permission: PermissionService,
    private reportService: ReportService,
    private valueService: ValuesService,
    private messageService: MessageService,
  ) {
    super(REPORT_WIDGET, store, dialog);
    this.generalFilter$ = this.store.select(selectReportGeneralFilter);
    this.report$ = this.store.select(selectReportPayload);
    this.isViewOnly$ = combineLatest([
      this.permission.abilities$,
      this.report$,
    ]).pipe(
      map(([ability, report]) => {
        return ability.onResourceCant(Action.COMMENT, ResourceType.REPORT, report);
      })
    );

    this.facility$ = this.report$.pipe(
      flatMap((report) => this.store.select(selectUserFacilityById, { id: report.facilityId ? report.facilityId : PRIVATE_FACILITY_ID })),
    );
    this.dashboardRoute$ = this.facility$.pipe(
      map((facility) => {
        return facility.id ? ['/', 'adf', 'dashboard', facility.id] : ['/', 'adf', 'dashboard'];
      })
    );
    this.nodes$ = this.store.select(selectReportTocNodes);
    this.labelizer$ = combineLatest([
      this.store.select(selectReportPayload),
      this.store.select(selectAllDetections),
      this.store.select(selectAllCvx),
      this.store.select(selectPatientTables),
      this.store.select(selectVaccinationTables),
      this.store.select(selectReportingGroups),
    ]).pipe(
      map(([report, detections, cvxCodes, patientTables, vaccinationTables, reportingGroups]) => {
        return this.valueService.getFieldOptions({
          detections,
          ageGroups: report.configuration.ageGroups,
          cvxs: cvxCodes,
          reportingGroups,
          tables: {
            vaccinationTables,
            patientTables,
          }
        }, report.customDetectionLabels);
      }),
    );
  }

  get filterIsActive$() {
    return this.generalFilter$.pipe(
      map((filter) => {
        return this.reportService.filterIsActive(filter);
      })
    );
  }

  fileChangeListener(event: any): void {
    if (event?.target?.files && event.target.files.length === 1) {
      this.reportService.getReportingGroupHashMap(event?.target?.files[0]).pipe(
        take(1),
        map((value) => {
          this.reportingGroupCsvFile = event?.target?.files[0];
          this.store.dispatch(new SetValue({
            reportingGroups: value,
          }));
        }),
        catchError((error: NgxCSVParserError) => {
          this.fileImportInput.nativeElement.value = '';
          this.store.dispatch(this.messageService.messageToAction(
            new Message(MessageType.FAILED, error.message, {}),
            {
              closable: true,
            }
          ));
          return throwError(error);
        })
      ).subscribe();
    }
  }

  clearReportingGroupCsvFile() {
    this.reportingGroupCsvFile = null;
    this.store.dispatch(new SetValue({
      reportingGroups: {},
    }));
  }

  publish() {
    this.store.select(selectReportPayload).pipe(
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
                  return from(message.status === MessageType.SUCCESS ? [new LoadPayloadData(message.data)] : []);
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
    this.store.dispatch(new TurnOffLoader());
  }

  filterByThreshold(value: boolean) {
    this.generalFilter$.pipe(
      take(1),
      tap((filter) => {
        this.store.dispatch(new SetValue({
          reportGeneralFilter: {
            ...filter,
            threshold: {
              active: value !== null,
              pass: value,
            },
          },
        }));
      })
    ).subscribe();
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
