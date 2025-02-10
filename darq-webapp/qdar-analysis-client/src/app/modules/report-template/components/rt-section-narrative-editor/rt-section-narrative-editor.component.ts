import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  DamAbstractEditorComponent,
  IEditorMetadata,
  MessageService,
  EditorSave,
  selectIsAdmin,
  LoadPayloadData,
  InsertResourcesInCollection,
} from '@usnistgov/ngx-dam-framework-legacy';
import { Store, Action } from '@ngrx/store';
import { Actions } from '@ngrx/effects';
import { ReportTemplateService } from '../../services/report-template.service';
import { Observable, Subscription, throwError, combineLatest } from 'rxjs';
import { IReportSectionDisplay } from '../../model/state.model';
import { selectSectionById, selectRtIsPublished, selectReportTemplate } from '../../store/core.selectors';
import { switchMap, map, take, concatMap, catchError, flatMap } from 'rxjs/operators';
import { IReportSection } from '../../model/report-template.model';
import { EntityType } from '../../../shared/model/entity.model';
import { Action as ResourceAction } from 'src/app/modules/core/model/action.enum';
import { ResourceType } from '../../../core/model/resouce-type.enum';
import { PermissionService } from '../../../core/services/permission.service';

export const RT_SECTION_NARRATIVE_EDITOR_METADATA: IEditorMetadata = {
  id: 'RT_SECTION_NARRATIVE_EDITOR_ID',
  title: 'Narrative'
};

@Component({
  selector: 'app-rt-section-narrative-editor',
  templateUrl: './rt-section-narrative-editor.component.html',
  styleUrls: ['./rt-section-narrative-editor.component.scss']
})
export class RtSectionNarrativeEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  viewOnly$: Observable<boolean>;
  isPublished$: Observable<boolean>;
  isAdmin$: Observable<boolean>;
  value: IReportSection;
  wSub: Subscription;

  constructor(
    store: Store<any>,
    actions$: Actions,
    private permissionService: PermissionService,
    private reportTemplateService: ReportTemplateService,
    private messageService: MessageService,
  ) {
    super(
      RT_SECTION_NARRATIVE_EDITOR_METADATA,
      actions$,
      store,
    );

    this.viewOnly$ = combineLatest([
      this.store.select(selectReportTemplate),
      this.permissionService.abilities$,
    ]).pipe(
      map(([rt, abilities]) => {
        return abilities.onResourceCant(ResourceAction.EDIT, ResourceType.REPORT_TEMPLATE, rt);
      })
    );
    this.isPublished$ = this.store.select(selectRtIsPublished);
    this.isAdmin$ = this.store.select(selectIsAdmin);

    this.wSub = this.currentSynchronized$.pipe(
      map((section: IReportSection) => {
        this.value = {
          ...section,
        };
      }),
    ).subscribe();
  }

  headerChange(value: string) {
    this.value = {
      ...this.value,
      header: value,
    };

    this.emitChange();
  }

  textChange(value) {
    this.value = {
      ...this.value,
      text: value,
    };

    this.emitChange();
  }

  emitChange() {
    this.editorChange({
      ...this.value
    }, this.value.header && this.value.header !== '');
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
          { header: current.data.header, text: current.data.text }
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
