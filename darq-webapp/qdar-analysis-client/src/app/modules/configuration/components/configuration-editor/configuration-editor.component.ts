import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  DamAbstractEditorComponent,
  EditorSave,
  IEditorMetadata,
  selectUsername,
  EditorUpdate,
  IWorkspaceCurrent,
  MessageService,
  ConfirmDialogComponent,
  InsertResourcesInCollection,
  selectIsAdmin,
  CleanWorkspace,
} from 'ngx-dam-framework';
import { Observable, of, Subscription, throwError } from 'rxjs';
import { Action, Store } from '@ngrx/store';
import { Actions } from '@ngrx/effects';
import { flatMap, map, tap, take, concatMap, catchError } from 'rxjs/operators';
import { selectConfigurationById } from '../../store/core.selectors';
import { IConfigurationDescriptor, IDigestConfiguration } from '../../model/configuration.model';
import { IRange } from '../../../shared/model/age-group.model';
import { IDetectionResource } from '../../../shared/model/public.model';
import { selectAllDetections } from '../../../shared/store/core.selectors';
import { ConfigurationService } from '../../services/configuration.service';
import { DamWidgetComponent, Message } from 'ngx-dam-framework';
import { MatDialog } from '@angular/material/dialog';

export const CONFIGURATION_EDITOR_MD: IEditorMetadata = {
  id: 'CONFIGURATION_EDITOR',
  title: 'Configuration',
};

@Component({
  selector: 'app-configuration-editor',
  templateUrl: './configuration-editor.component.html',
  styleUrls: ['./configuration-editor.component.scss']
})
export class ConfigurationEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  viewOnly: boolean;
  isLocked: boolean;
  isPublished: boolean;
  isOwned: boolean;
  isAdmin$: Observable<boolean>;
  value: IDigestConfiguration;
  detections: Observable<IDetectionResource[]>;
  wSub: Subscription;

  constructor(
    store: Store<any>,
    actions$: Actions,
    public dialog: MatDialog,
    public widget: DamWidgetComponent,
    private configurationService: ConfigurationService,
    private messageService: MessageService,
  ) {
    super(CONFIGURATION_EDITOR_MD, actions$, store);
    this.wSub = this.currentSynchronized$.pipe(
      map((configuration: IDigestConfiguration) => {
        this.isLocked = configuration.locked;
        this.isPublished = configuration.published;
        this.viewOnly = configuration.viewOnly;
        this.value = {
          ...configuration,
          payload: {
            ...configuration.payload,
            ageGroups: [
              ...configuration.payload.ageGroups,
            ],
          },
        };
        this.store.select(selectUsername).pipe(
          take(1),
          tap((username) => {
            this.isOwned = username === configuration.owner;
          })
        ).subscribe();
      }),
    ).subscribe();
    this.isAdmin$ = this.store.select(selectIsAdmin);
    this.detections = this.store.select(selectAllDetections);
  }

  emitChange() {
    this.editorChange({
      ...this.value
    }, this.value.name && this.value.name !== '');
  }

  executeAction(
    action: (id: string) => Observable<Message<IDigestConfiguration>>,
    confirm: { action: string, question: string }
  ) {
    this.dialog.open(ConfirmDialogComponent, {
      data: confirm,
    }).afterClosed().pipe(
      concatMap((answer) => {
        if (answer) {
          return this.elementId$.pipe(
            take(1),
            concatMap((id) => {
              return action(id).pipe(
                flatMap((message) => {
                  return [
                    new InsertResourcesInCollection({
                      key: 'configurations',
                      values: [this.configurationService.getDescriptor(message.data, this.isOwned)],
                    }),
                    this.messageService.messageToAction(message),
                    new EditorUpdate({ value: message.data, updateDate: true }),
                  ];
                }),
                map((a) => {
                  this.store.dispatch(a);
                }),
                catchError((error) => {
                  return throwError(this.messageService.actionFromError(error));
                })
              );
            }),
          );
        }
        return of();
      })
    ).subscribe();
  }

  lock() {
    this.executeAction(
      this.configurationService.lock,
      {
        action: 'Lock Configuration',
        question: 'Are you sure you want to lock configuration ' + this.value.name + '? (irreversible)'
      },
    );
  }

  publish() {
    this.executeAction(
      this.configurationService.publish,
      {
        action: 'Publish Configuration',
        question: 'Are you sure you want to publish configuration ' + this.value.name + ',<br> this will make it globally available for all users ?'
      },
    );
  }

  nameChange(value: string) {
    this.value = {
      ...this.value,
      name: value,
    };

    this.emitChange();
  }

  asOfChange(value) {
    this.value = {
      ...this.value,
      payload: {
        ...this.value.payload,
        asOf: value,
      }
    };

    this.emitChange();
  }

  ageGroupsChange(ageGroups: IRange[]) {
    this.value = {
      ...this.value,
      payload: {
        ...this.value.payload,
        ageGroups,
      }
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

  detectionsChange(detections: string[]) {
    this.value = {
      ...this.value,
      payload: {
        ...this.value.payload,
        detections,
      }
    };

    this.emitChange();
  }

  onEditorSave(action: EditorSave): Observable<Action> {
    return this.current$.pipe(
      take(1),
      concatMap((current: IWorkspaceCurrent) => {
        return this.configurationService.save(current.data).pipe(
          flatMap((message) => {
            return [
              new InsertResourcesInCollection({
                key: 'configurations',
                values: [this.configurationService.getDescriptor(message.data, this.isOwned)],
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

  editorDisplayNode(): Observable<IConfigurationDescriptor> {
    return this.elementId$.pipe(
      flatMap((id) => {
        return this.store.select(selectConfigurationById, { id });
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
