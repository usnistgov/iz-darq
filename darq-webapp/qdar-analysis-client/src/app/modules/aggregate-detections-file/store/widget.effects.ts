import { Injectable } from '@angular/core';
import { Effect, ofType, Actions } from '@ngrx/effects';
import { Store, Action } from '@ngrx/store';
import { DamWidgetEffect, SetValue, LoadResourcesInRepository, OpenEditor, OpenEditorFailure, MessageService } from 'ngx-dam-framework';
import { combineLatest, of, Observable } from 'rxjs';
import { concatMap, flatMap, catchError, take } from 'rxjs/operators';
import { AnalysisService } from '../../shared/services/analysis.service';
import { SupportDataService } from '../../shared/services/support-data.service';
import { ADF_WIDGET } from '../components/adf-widget/adf-widget.component';
import { FileService } from '../services/file.service';
import {
  LoadADFDashboard,
  LoadADFDashboardSuccess,
  OpenADFListEditor,
  OpenAnalysisJobEditor,
  CoreActionTypes,
  CoreActions,
  OpenMergeJobEditor,
} from './core.actions';

import { selectUserFacilityById } from './core.selectors';
import { OpenReportsEditor } from './core.actions';
import { ReportService } from '../../report/services/report.service';
import { IUserFacilityDescriptor } from '../../facility/model/facility.model';

@Injectable()
export class WidgetEffects extends DamWidgetEffect {

  constructor(
    private fileService: FileService,
    private store: Store<any>,
    private analysisService: AnalysisService,
    private messageService: MessageService,
    private supportService: SupportDataService,
    private reportService: ReportService,
    actions$: Actions<CoreActions>
  ) {
    super(ADF_WIDGET, actions$);
  }

  @Effect()
  loadAdfDashboard$ = this.actions$.pipe(
    ofType(CoreActionTypes.LoadADFDashboard),
    concatMap((action: LoadADFDashboard) => {
      return combineLatest([
        this.supportService.getCvxCodes(),
        this.supportService.getDetections(),
        this.supportService.getPatientTables(),
        this.supportService.getVaccinationTables(),
        this.fileService.getFacilitiesForUser(),
      ]).pipe(
        take(1),
        flatMap(([cvx, detections, patientTables, vaccinationTables, facilityList]) => {
          return [
            new SetValue({
              patientTables,
              vaccinationTables,
              facilityList,
            }),
            new LoadResourcesInRepository<any>({
              collections: [{
                key: 'detections',
                values: detections,
              },
              {
                key: 'cvx',
                values: cvx,
              },
              {
                key: 'user-facilities',
                values: [
                  ...facilityList,
                ],
              },
              ]
            }),
            new LoadADFDashboardSuccess(),
          ];
        }),
        catchError((error) => {
          return this.handleError(error, action);
        })
      );
    })
  );

  @Effect()
  openAdfListEditor$ = this.actions$.pipe(
    ofType(CoreActionTypes.OpenADFListEditor),
    concatMap((action: OpenADFListEditor) => {
      return combineLatest([
        this.fileService.getListByFacility(action.payload.id),
        this.store.select(selectUserFacilityById, { id: action.payload.id }),
      ]).pipe(
        take(1),
        flatMap(([files, facility]) => {
          return [
            new SetValue({
              facilityId: action.payload.id,
            }),
            new LoadResourcesInRepository<any>({
              collections: [
                {
                  key: 'files',
                  values: files,
                }
              ]
            }),
            new OpenEditor({
              id: action.payload.id,
              editor: action.payload.editor,
              initial: {
                files,
              },
              display: {
                ...facility,
              },
            })
          ];
        }),
        catchError((error) => {
          return this.handleError(error, action);
        })
      );
    })
  );

  @Effect()
  openAnalysisJobEditor$ = this.actions$.pipe(
    ofType(CoreActionTypes.OpenAnalysisJobEditor),
    concatMap((action: OpenAnalysisJobEditor) => {
      return combineLatest([
        this.analysisService.getJobsByFacility(action.payload.id),
        this.store.select(selectUserFacilityById, { id: action.payload.id }),
      ]).pipe(
        take(1),
        flatMap(([jobs, facility]) => {
          return this.openJobEditor(action, jobs, facility);
        }),
        catchError((error) => {
          return this.handleError(error, action);
        })
      );
    })
  );

  @Effect()
  openMergeJobEditor$ = this.actions$.pipe(
    ofType(CoreActionTypes.OpenMergeJobEditor),
    concatMap((action: OpenMergeJobEditor) => {
      return combineLatest([
        this.fileService.getJobsByFacility(action.payload.id),
        this.store.select(selectUserFacilityById, { id: action.payload.id }),
      ]).pipe(
        take(1),
        flatMap(([jobs, facility]) => {
          return this.openJobEditor(action, jobs, facility);
        }),
        catchError((error) => {
          return this.handleError(error, action);
        })
      );
    })
  );

  @Effect()
  openReportsEditor$ = this.actions$.pipe(
    ofType(CoreActionTypes.OpenReportsEditor),
    concatMap((action: OpenReportsEditor) => {
      return combineLatest([
        this.reportService.publishedForFacility(action.payload.id),
        this.store.select(selectUserFacilityById, { id: action.payload.id }),
      ]).pipe(
        take(1),
        flatMap(([reports, facility]) => {
          return [
            new SetValue({
              facilityId: action.payload.id,
            }),
            new LoadResourcesInRepository<any>({
              collections: [
                {
                  key: 'reports',
                  values: reports,
                }
              ]
            }),
            new OpenEditor({
              id: action.payload.id,
              editor: action.payload.editor,
              initial: {
                reports,
              },
              display: {
                ...facility,
              },
            })
          ];
        }),
        catchError((error) => {
          return this.handleError(error, action);
        })
      );
    })
  );

  openJobEditor(action: OpenMergeJobEditor | OpenAnalysisJobEditor, jobs: any[], facility: IUserFacilityDescriptor) {
    return [
      new SetValue({
        facilityId: action.payload.id,
      }),
      new LoadResourcesInRepository<any>({
        collections: [
          {
            key: 'merge-jobs',
            values: jobs,
          }
        ]
      }),
      new OpenEditor({
        id: action.payload.id,
        editor: action.payload.editor,
        initial: {
          jobs,
        },
        display: {
          ...facility,
        },
      })
    ];
  }

  handleError(error, action): Observable<Action> {
    return of(
      this.messageService.actionFromError(error),
      new OpenEditorFailure({ id: action.payload.editor.id }),
    );
  }

}

