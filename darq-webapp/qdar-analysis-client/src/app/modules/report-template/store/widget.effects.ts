import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import {
  DamWidgetEffect,
  DamActionTypes,
  GlobalSave,
  EditorSave,
  selectWorkspaceCurrentIsChanged,
  MessageService,
  OpenEditor,
  OpenEditorFailure,
  LoadPayloadData,
  SetValue,
  LoadResourcesInRepository,
  EditorReset
} from 'ngx-dam-framework';
import { RT_WIDGET_ID } from '../components/report-template-widget/report-template-widget.component';
import { map, concatMap, take, catchError, flatMap } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { combineLatest, of } from 'rxjs';
import { selectReportTemplate, selectTableOfContentIsChanged, selectInitSections, selectSectionById } from './core.selectors';
import { ReportTemplateService } from '../services/report-template.service';
import { SupportDataService } from '../../shared/services/support-data.service';
import {
  CoreActionTypes,
  OpenReportTemplateMetadata,
  LoadReportTemplateSuccess,
  LoadReportTemplate,
  LoadReportTemplateFailure,
  OpenReportTemplateSection,
} from './core.actions';
import { IDetectionResource, ICvxResource } from '../../shared/model/public.model';
import { IReportSection } from '../model/report-template.model';
import { handleError } from '../../shared/services/helper.functions';


@Injectable()
export class WidgetEffects extends DamWidgetEffect {

  @Effect()
  reset$ = this.actions$.pipe(
    ofType(DamActionTypes.EditorReset),
    concatMap((action: EditorReset) => {
      return combineLatest([
        this.store.select(selectTableOfContentIsChanged),
        this.store.select(selectReportTemplate),
        this.store.select(selectInitSections),
      ]).pipe(
        take(1),
        flatMap(([toc, rt, initial]) => {
          if (toc) {
            return [
              new LoadPayloadData({
                ...rt,
                sections: initial,
              }),
              new SetValue({ tableOfContentChanged: false }),
              new LoadResourcesInRepository({
                collections: [{
                  key: 'sections',
                  values: this.reportTemplateService.flattenSections(initial),
                }]
              }),
            ];
          } else {
            return [];
          }
        })
      );
    }),
  );

  @Effect()
  save$ = this.actions$.pipe(
    ofType(DamActionTypes.GlobalSave),
    concatMap((action: GlobalSave) => {
      return combineLatest([
        this.store.select(selectTableOfContentIsChanged),
        this.store.select(selectWorkspaceCurrentIsChanged),
        this.store.select(selectReportTemplate),
      ]).pipe(
        take(1),
        concatMap(([toc, ws, rt]) => {
          if (ws) {
            return of(new EditorSave(rt));
          } else if (toc) {
            return this.reportTemplateService.save(rt).pipe(
              flatMap((message) => {
                return [
                  this.messageService.messageToAction(message),
                  new LoadPayloadData(message.data),
                  new SetValue({ sections: message.data.sections }),
                  new SetValue({ tableOfContentChanged: false }),
                  new LoadResourcesInRepository({
                    collections: [{
                      key: 'sections',
                      values: this.reportTemplateService.flattenSections(message.data.sections),
                    }]
                  }),
                ];
              }),
              catchError(handleError(this.messageService, LoadReportTemplateFailure))
            );
          } else {
            return of();
          }
        }),
      );
    })
  );

  @Effect()
  loadReportTemplate$ = this.actions$.pipe(
    ofType(CoreActionTypes.LoadReportTemplate),
    concatMap((action: LoadReportTemplate) => {
      return combineLatest([
        this.reportTemplateService.getById(action.id),
        this.supportDataService.getCvxCodes(),
        this.supportDataService.getDetections(),
        this.supportDataService.getPatientTables(),
        this.supportDataService.getVaccinationTables(),
      ]).pipe(
        flatMap(([template, cvx, detections, patientTables, vaccinationTables]) => {
          this.reportTemplateService.sortList(template.sections, undefined, false);
          return [
            new LoadPayloadData(template),
            new SetValue({
              sections: template.sections,
              tableOfContentChanged: false,
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
                values: this.reportTemplateService.flattenSections(template.sections),
              }]
            }),
            new LoadReportTemplateSuccess(template),
          ];
        }),
        catchError(handleError(this.messageService, LoadReportTemplateFailure)),
      );
    })
  );

  @Effect()
  openRtMetadataEditor$ = this.actions$.pipe(
    ofType(CoreActionTypes.OpenReportTemplateMetadata),
    concatMap((action: OpenReportTemplateMetadata) => {
      return combineLatest([
        this.reportTemplateService.getDescriptorById(action.payload.id),
        this.store.select(selectReportTemplate)
      ]).pipe(
        take(1),
        map(([desc, rt]) => {
          return new OpenEditor({
            id: action.payload.id,
            display: {
              ...desc,
            },
            editor: action.payload.editor,
            initial: {
              ...rt,
            },
          });
        }),
        catchError((error) => {
          return of(this.messageService.actionFromError(error), new OpenEditorFailure(action.payload));
        })
      );
    })
  );

  @Effect()
  openRtSectionEditor$ = this.actions$.pipe(
    ofType(CoreActionTypes.OpenReportTemplateSection),
    concatMap((action: OpenReportTemplateSection) => {
      return combineLatest([
        this.store.select(selectSectionById, { id: action.payload.id })
      ]).pipe(
        take(1),
        map(([section]) => {
          return new OpenEditor({
            id: action.payload.id,
            display: this.reportTemplateService.getSectionDisplay(section),
            editor: action.payload.editor,
            initial: {
              ...section,
            },
          });
        }),
        catchError((error) => {
          return of(this.messageService.actionFromError(error), new OpenEditorFailure(action.payload));
        })
      );
    })
  );


  constructor(
    actions$: Actions,
    private store: Store<any>,
    private reportTemplateService: ReportTemplateService,
    private supportDataService: SupportDataService,
    private messageService: MessageService,
  ) {
    super(RT_WIDGET_ID, actions$);
  }

}
