import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { catchError, concatMap, flatMap, take, map } from 'rxjs/operators';
import { CoreActionTypes, LoadReport, LoadReportSuccess, LoadReportFailure, OpenReportEditor } from './core.actions';
import { Store } from '@ngrx/store';
import { ReportTemplateService } from '../../report-template/services/report-template.service';
import { SupportDataService } from '../../shared/services/support-data.service';
import {
  MessageService,
  DamWidgetEffect,
  LoadPayloadData,
  SetValue,
  LoadResourcesInRepository,
  OpenEditor,
  DamActionTypes,
  GlobalSave,
  EditorSave
} from 'ngx-dam-framework';
import { combineLatest } from 'rxjs';
import { ReportService } from '../services/report.service';
import { REPORT_WIDGET } from '../components/report-widget/report-widget.component';
import { IDetectionResource, ICvxResource } from '../../shared/model/public.model';
import { IReportSection, Comparator } from '../../report-template/model/report-template.model';
import { handleError } from '../../shared/services/helper.functions';
import { selectReport } from './core.selectors';


@Injectable()
export class CoreEffects extends DamWidgetEffect {

  @Effect()
  save$ = this.actions$.pipe(
    ofType(DamActionTypes.GlobalSave),
    map((action: GlobalSave) => {
      return new EditorSave({});
    })
  );

  @Effect()
  openReportEditor$ = this.actions$.pipe(
    ofType(CoreActionTypes.OpenReportEditor),
    concatMap((action: OpenReportEditor) => {
      return this.store.select(selectReport).pipe(
        take(1),
        flatMap((report) => {
          return [
            new SetValue({
              reportGeneralFilter: {
                denominator: {
                  active: false,
                  value: 0,
                  comparator: Comparator.GT,
                },
                percentage: {
                  active: false,
                  value: 0,
                  comparator: Comparator.GT,
                },
                threshold: {
                  active: false,
                  pass: true,
                },
                fields: {
                  keep: false,
                  active: false,
                  fields: {
                    PROVIDER: [],
                    AGE_GROUP: [],
                    CODE: [],
                    EVENT: [],
                    GENDER: [],
                    VACCINE_CODE: [],
                    VACCINATION_YEAR: [],
                  },
                }
              }
            }),
            new OpenEditor({
              id: action.payload.id,
              editor: action.payload.editor,
              display: { name: report.name },
              initial: report,
            })
          ];
        })
      );
    }),
  );

  @Effect()
  loadReportTemplate$ = this.actions$.pipe(
    ofType(CoreActionTypes.LoadReport),
    concatMap((action: LoadReport) => {
      return combineLatest([
        this.reportService.getById(action.id),
        this.supportDataService.getCvxCodes(),
        this.supportDataService.getDetections(),
        this.supportDataService.getPatientTables(),
        this.supportDataService.getVaccinationTables(),
      ]).pipe(
        flatMap(([report, cvx, detections, patientTables, vaccinationTables]) => {
          this.reportTemplateService.sortList(report.sections, undefined, false);
          return [
            new LoadPayloadData(report),
            new SetValue({
              patientTables,
              vaccinationTables,
            }),
            new LoadResourcesInRepository<IDetectionResource | ICvxResource | IReportSection>({
              collections: [{
                key: 'detections',
                values: detections,
              },
              {
                key: 'cvx',
                values: cvx,
              },
              {
                key: 'sections',
                values: this.reportTemplateService.flattenSections(report.sections),
              }]
            }),
            new LoadReportSuccess(report),
          ];
        }),
        catchError(handleError(this.messageService, LoadReportFailure)),
      );
    })
  );



  constructor(
    actions$: Actions,
    private store: Store<any>,
    private reportService: ReportService,
    private reportTemplateService: ReportTemplateService,
    private supportDataService: SupportDataService,
    private messageService: MessageService
  ) {
    super(REPORT_WIDGET, actions$);
  }

}
