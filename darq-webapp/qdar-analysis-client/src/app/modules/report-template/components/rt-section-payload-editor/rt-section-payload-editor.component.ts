import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  DamAbstractEditorComponent,
  IEditorMetadata,
  MessageService,
  EditorSave,
  selectIsAdmin,
  LoadPayloadData,
  InsertResourcesInCollection
} from 'ngx-dam-framework';
import { Store, Action } from '@ngrx/store';
import { Actions } from '@ngrx/effects';
import { ReportTemplateService } from '../../services/report-template.service';
import { Observable, Subscription, throwError, of, combineLatest } from 'rxjs';
import { IReportSectionDisplay } from '../../model/state.model';
import { selectSectionById, selectRtIsViewOnly, selectRtIsPublished, selectRtIsOwned, selectReportTemplateConfiguration } from '../../store/core.selectors';
import { switchMap, map, take, concatMap, catchError, flatMap } from 'rxjs/operators';
import { IReportSection, IDataViewQuery } from '../../model/report-template.model';
import { EntityType } from '../../../shared/model/entity.model';
import { IDetectionResource, ICvxResource } from 'src/app/modules/shared/model/public.model';
import { selectAllDetections, selectAllCvx, selectPatientTables, selectVaccinationTables } from '../../../shared/store/core.selectors';
import { MatDialog } from '@angular/material/dialog';
import { QueryDialogComponent } from '../../../shared/components/query-dialog/query-dialog.component';
import { AnalysisType, names } from '../../model/analysis.values';
import { IFieldInputOptions } from 'src/app/modules/shared/components/field-input/field-input.component';
import { ValuesService, IValueLabelMap, Labelizer } from '../../../shared/services/values.service';
import { ConfirmDialogComponent } from 'ngx-dam-framework';

export const RT_SECTION_PAYLOAD_EDITOR_METADATA: IEditorMetadata = {
  id: 'RT_SECTION_PAYLOAD_EDITOR_ID',
  title: 'Data Tables'
};

@Component({
  selector: 'app-rt-section-payload-editor',
  templateUrl: './rt-section-payload-editor.component.html',
  styleUrls: ['./rt-section-payload-editor.component.scss']
})
export class RtSectionPayloadEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  viewOnly$: Observable<boolean>;
  isPublished$: Observable<boolean>;
  isOwned$: Observable<boolean>;
  isAdmin$: Observable<boolean>;
  detections$: Observable<IDetectionResource[]>;
  cvxs$: Observable<ICvxResource[]>;
  value: IReportSection;
  wSub: Subscription;
  options$: Observable<IFieldInputOptions>;
  labels$: Observable<Labelizer>;
  accordion: { [n: number]: boolean } = {};

  constructor(
    store: Store<any>,
    actions$: Actions,
    private dialog: MatDialog,
    private valueService: ValuesService,
    private reportTemplateService: ReportTemplateService,
    private messageService: MessageService,
  ) {
    super(
      RT_SECTION_PAYLOAD_EDITOR_METADATA,
      actions$,
      store,
    );


    this.detections$ = this.store.select(selectAllDetections);
    this.cvxs$ = this.store.select(selectAllCvx);
    this.viewOnly$ = this.store.select(selectRtIsViewOnly);
    this.isPublished$ = this.store.select(selectRtIsPublished);
    this.isOwned$ = this.store.select(selectRtIsOwned);
    this.isAdmin$ = this.store.select(selectIsAdmin);

    this.options$ = combineLatest([
      this.detections$,
      this.cvxs$,
      this.store.select(selectReportTemplateConfiguration),
      this.store.select(selectPatientTables),
      this.store.select(selectVaccinationTables),
    ]).pipe(
      map(([detections, cvxCodes, configuration, patientTables, vaccinationTables]) => {
        return this.valueService.getFieldOptions({
          detections: detections.filter((d) => configuration.detections.includes(d.id)),
          ageGroups: configuration.ageGroups,
          cvxs: cvxCodes,
          tables: {
            vaccinationTables,
            patientTables,
          }
        });
      }),
    );
    this.labels$ = this.options$.pipe(
      map((options) => this.valueService.getQueryValuesLabel(options)),
    );
    this.wSub = this.currentSynchronized$.pipe(
      map((section: IReportSection) => {
        this.value = {
          ...section,
        };
      }),
    ).subscribe();
  }

  toggleAccordion(n: number) {
    this.accordion[n] = !this.accordion[n];
  }

  openDialog(value: IDataViewQuery, i?: number) {
    this.options$.pipe(
      take(1),
      flatMap((options) => {
        return this.dialog.open(QueryDialogComponent, {
          disableClose: true,
          minWidth: '70vw',
          maxWidth: '93vw',
          maxHeight: '95vh',
          data: {
            options,
            query: value,
          }
        }).afterClosed().pipe(
          map((data) => {
            if (data) {
              if (i !== undefined) {
                this.payloadChange(data, i);
              } else {
                this.payloadCreate(data);
              }
            }
          }),
        );
      }),
    ).subscribe();
  }

  createPayload() {
    this.openDialog(this.reportTemplateService.getEmptyDataViewQuery());
  }

  editPayload(value: IDataViewQuery, i: number) {
    this.openDialog(value, i);
  }

  removePayload(i: number) {
    this.dialog.open(ConfirmDialogComponent, {
      data: {
        action: 'Delete Query',
        question: 'Are you sure you want to delete query ?',
      }
    }).afterClosed().pipe(
      map((answer) => {
        if (answer) {
          this.payloadRemove(i);
        }
      })
    ).subscribe();
  }

  payloadChange(value: IDataViewQuery, i: number) {
    const queries = [...this.value.data];
    queries.splice(i, 1, value);
    this.value = {
      ...this.value,
      data: queries
    };

    this.emitChange();
  }

  payloadCreate(value: IDataViewQuery) {
    const queries = [...this.value.data];
    queries.push(value);
    this.value = {
      ...this.value,
      data: queries
    };

    this.emitChange();
  }

  payloadRemove(i: number) {
    const queries = [...this.value.data];
    queries.splice(i, 1);
    this.value = {
      ...this.value,
      data: queries
    };

    this.emitChange();
  }

  emitChange() {
    this.editorChange({
      ...this.value
    }, this.value.header && this.value.header !== '');
  }

  valueOfAnalysis(str: string): AnalysisType {
    return Object.keys(AnalysisType).find((key) => {
      return AnalysisType[key] === str;
    }) as AnalysisType;
  }

  nameOf(type: AnalysisType) {
    return names[this.valueOfAnalysis(type)];
  }

  onEditorSave(action: EditorSave): Observable<Action> {
    return combineLatest([
      this.current$,
      this.editorDisplayNode(),
    ]).pipe(
      take(1),
      concatMap(([current, display]) => {
        const mergeReportTemplate = this.reportTemplateService.mergeSection(
          action.payload,
          display.path,
          { data: this.value.data },
        );

        return this.reportTemplateService.save(mergeReportTemplate.reportTemplate).pipe(
          flatMap((message) => {
            return [
              new LoadPayloadData(message.data),
              new InsertResourcesInCollection({
                key: 'sections',
                values: [{
                  type: EntityType.SECTION,
                  ...mergeReportTemplate.section,
                }],
              }),
              this.messageService.messageToAction(message),
            ];
          }),
          catchError((error) => {
            return throwError(this.messageService.actionFromError(error));
          })
        );
      }),
    );
  }

  editorDisplayNode(): Observable<IReportSectionDisplay> {
    return this.elementId$.pipe(
      switchMap((id) => {
        return this.store.select(selectSectionById, { id }).pipe(
          map((section) => {
            return this.reportTemplateService.getSectionDisplay(section);
          }),
        );
      }),
    );
  }

  ngOnDestroy(): void {
    if (this.wSub) {
      this.wSub.unsubscribe();
    }
  }

  onDeactivate(): void {
  }

  ngOnInit(): void {
  }

}
