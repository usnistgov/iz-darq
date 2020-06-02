import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  DamAbstractEditorComponent,
  IEditorMetadata,
  EditorSave,
  selectPayloadData,
  selectUsername,
  IWorkspaceCurrent,
  LoadPayloadData,
  MessageService,
} from 'ngx-dam-framework';
import { Store, Action } from '@ngrx/store';
import { Actions } from '@ngrx/effects';
import { Observable, Subscription, throwError } from 'rxjs';
import { map, take, tap, concatMap, flatMap, catchError, withLatestFrom } from 'rxjs/operators';
import { IReportTemplate } from '../../model/report-template.model';
import { IConfigurationDescriptor } from 'src/app/modules/configuration/model/configuration.model';
import { ReportTemplateService } from '../../services/report-template.service';

export const RT_METADATA_EDITOR_METADATA: IEditorMetadata = {
  id: 'RT_METADATA_EDITOR_ID',
  title: 'Metadata'
};

@Component({
  selector: 'app-rt-metadata-editor',
  templateUrl: './rt-metadata-editor.component.html',
  styleUrls: ['./rt-metadata-editor.component.scss']
})
export class RtMetadataEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  viewOnly: boolean;
  isPublished: boolean;
  isOwned: boolean;
  isAdmin$: Observable<boolean>;
  value: IReportTemplate;
  wSub: Subscription;
  compatibilities$: Observable<IConfigurationDescriptor[]>;

  constructor(
    store: Store<any>,
    actions$: Actions,
    private reportTemplateService: ReportTemplateService,
    private messageService: MessageService,
  ) {
    super(
      RT_METADATA_EDITOR_METADATA,
      actions$,
      store,
    );
    this.compatibilities$ = this.active$.pipe(
      map((a) => {
        return a.display.compatibilities;
      })
    );

    this.wSub = this.currentSynchronized$.pipe(
      map((template: IReportTemplate) => {
        this.isPublished = template.published;
        this.viewOnly = template.viewOnly;
        this.value = {
          ...template,
        };
        this.store.select(selectUsername).pipe(
          take(1),
          tap((username) => {
            this.isOwned = username === template.owner;
          })
        ).subscribe();
      }),
    ).subscribe();
  }

  nameChange(value: string) {
    this.value = {
      ...this.value,
      name: value,
    };

    this.emitChange();
  }

  descriptionChange(value) {
    this.value = {
      ...this.value,
      description: value,
    };

    this.emitChange();
  }

  emitChange() {
    this.editorChange({
      ...this.value
    }, this.value.name && this.value.name !== '');
  }

  onEditorSave(action: EditorSave): Observable<Action> {
    return this.current$.pipe(
      take(1),
      concatMap((current: IWorkspaceCurrent) => {
        return this.reportTemplateService.save(
          this.reportTemplateService.mergeMetadata(action.payload, current.data),
        ).pipe(
          flatMap((message) => {
            return [
              new LoadPayloadData(message.data),
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

  editorDisplayNode(): Observable<any> {
    return this.store.select(selectPayloadData).pipe(
      withLatestFrom(this.active$),
      map(([current, active]) => {
        return {
          ...active.display,
          name: current.name,
        };
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
