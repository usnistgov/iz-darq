import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  DamAbstractEditorComponent,
  IEditorMetadata,
  MessageService,
  EditorSave,
  LoadPayloadData,
  InsertResourcesInCollection
} from 'ngx-dam-framework';
import { Store, Action } from '@ngrx/store';
import { Actions } from '@ngrx/effects';
import { ReportTemplateService } from '../../services/report-template.service';
import { Observable, Subscription, throwError, combineLatest } from 'rxjs';
import { IReportSectionDisplay } from '../../model/state.model';
import {
  selectSectionById,
  selectRtIsPublished,
  selectReportTemplateConfiguration,
  selectReportTemplate,
} from '../../store/core.selectors';
import { switchMap, map, take, concatMap, catchError, flatMap, distinctUntilChanged } from 'rxjs/operators';
import { IReportSection, IDataViewQuery, QueryType } from '../../model/report-template.model';
import { EntityType } from '../../../shared/model/entity.model';
import { IDetectionResource, ICvxResource } from 'src/app/modules/shared/model/public.model';
import { selectAllDetections, selectAllCvx, selectPatientTables, selectVaccinationTables } from '../../../shared/store/core.selectors';
import { MatDialog } from '@angular/material/dialog';
import { QueryDialogComponent } from '../../../shared/components/query-dialog/query-dialog.component';
import { AnalysisType, names } from '../../model/analysis.values';
import { IFieldInputOptions } from 'src/app/modules/shared/components/field-input/field-input.component';
import { ValuesService, Labelizer } from '../../../shared/services/values.service';
import { ConfirmDialogComponent } from 'ngx-dam-framework';
import { ResourceType } from '../../../core/model/resouce-type.enum';
import { Action as ResourceAction } from 'src/app/modules/core/model/action.enum';
import { PermissionService } from '../../../core/services/permission.service';

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
    private permissionService: PermissionService,
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
    this.viewOnly$ = combineLatest([
      this.store.select(selectReportTemplate),
      this.permissionService.abilities$,
    ]).pipe(
      map(([rt, abilities]) => {
        return abilities.onResourceCant(ResourceAction.EDIT, ResourceType.REPORT_TEMPLATE, rt);
      })
    );

    this.isPublished$ = this.store.select(selectRtIsPublished);

    this.options$ = combineLatest([
      this.store.select(selectReportTemplate).pipe(
        map((rt) => rt.customDetectionLabels),
        distinctUntilChanged(),
      ),
      this.detections$,
      this.cvxs$,
      this.store.select(selectReportTemplateConfiguration),
      this.store.select(selectPatientTables),
      this.store.select(selectVaccinationTables),
    ]).pipe(
      map(([labels, detections, cvxCodes, configuration, patientTables, vaccinationTables]) => {
        return this.valueService.getFieldOptions({
          detections: detections.filter((d) => configuration.detections.includes(d.id)),
          ageGroups: configuration.ageGroups,
          cvxs: cvxCodes,
          reportingGroups: {},
          tables: {
            vaccinationTables,
            patientTables,
          }
        }, labels);
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

  openDialog(value: QueryType, i?: number) {
    combineLatest([
      this.options$,
      this.store.select(selectReportTemplateConfiguration),
    ]).pipe(
      take(1),
      flatMap(([options, configuration]) => {
        return this.dialog.open(QueryDialogComponent, {
          disableClose: true,
          minWidth: '70vw',
          maxWidth: '93vw',
          maxHeight: '95vh',
          data: {
            options,
            configuration,
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
