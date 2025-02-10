import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  DamAbstractEditorComponent,
  EditorSave,
  IEditorMetadata,
  EditorUpdate,
  IWorkspaceCurrent,
  MessageService,
  ConfirmDialogComponent,
  InsertResourcesInCollection,
} from '@usnistgov/ngx-dam-framework-legacy';
import { Observable, of, Subscription, throwError } from 'rxjs';
import { Store, Action } from '@ngrx/store';
import { Actions } from '@ngrx/effects';
import { flatMap, map, take, concatMap, catchError, tap } from 'rxjs/operators';
import { selectConfigurationById } from '../../store/core.selectors';
import { IConfigurationDescriptor, IDigestConfiguration } from '../../model/configuration.model';
import { IRange } from '../../../shared/model/age-group.model';
import { IDetectionResource } from '../../../shared/model/public.model';
import { selectAllDetections } from '../../../shared/store/core.selectors';
import { ConfigurationService } from '../../services/configuration.service';
import { DamWidgetComponent, Message } from '@usnistgov/ngx-dam-framework-legacy';
import { MatDialog } from '@angular/material/dialog';
import { Action as ResourceAction } from 'src/app/modules/core/model/action.enum';
import { ResourceType } from '../../../core/model/resouce-type.enum';
import { PermissionService } from '../../../core/services/permission.service';
import { ComplexDetectionDialogComponent } from '../complex-detection-dialog/complex-detection-dialog.component';

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

  viewOnly$: Observable<boolean>;
  isLocked: boolean;
  isPublished: boolean;
  value: IDigestConfiguration;
  detections: Observable<IDetectionResource[]>;
  wSub: Subscription;
  configurationTextAreaCollapsed = true;
  yamlEditorOptions = {
    theme: 'monokai',
    mode: 'yaml',
    lineNumbers: true,
    foldGutter: true,
    styleActiveLine: true,
    autoCloseTags: true,
    gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter', 'CodeMirror-lint-markers'],
    matchBrackets: true,
    extraKeys: { 'Alt-F': 'findPersistent' },
    lineWrapping: true,
    placeholder:
      ``,
  };
  constructor(
    store: Store<any>,
    actions$: Actions,
    private permissionService: PermissionService,
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

        this.viewOnly$ = this.permissionService.abilities$.pipe(
          map((abilities) => {
            return abilities.onResourceCant(ResourceAction.EDIT, ResourceType.CONFIGURATION, configuration) || configuration.locked;
          })
        );

        this.value = {
          ...configuration,
          payload: {
            ...configuration.payload,
            ageGroups: [
              ...configuration.payload.ageGroups,
            ],
          },
        };
      }),
    ).subscribe();
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

                  return this.configurationService.getDescriptorById(message.data.id).pipe(
                    flatMap((descriptor) => {
                      return [
                        new InsertResourcesInCollection({
                          key: 'configurations',
                          values: [descriptor],
                        }),
                        this.messageService.messageToAction(message),
                        new EditorUpdate({ value: message.data, updateDate: true }),
                      ];
                    })
                  );


                }),
                map((a) => {
                  this.store.dispatch(a);
                }),
                catchError((error) => {
                  this.store.dispatch(this.messageService.actionFromError(error));
                  return of(error);
                })
              );
            }),
          );
        }
        return of();
      })
    ).subscribe();
  }

  openComplexDetectionDialog(index: number = -1) {
    this.detections.pipe(
      take(1),
      flatMap((detections) => {
        const excludeCode = index !== -1 ? this.value.payload.complexDetections[index].code : undefined;
        return this.dialog.open(ComplexDetectionDialogComponent, {
          minWidth: '80vw',
          data: {
            detections,
            existingCodes: (this.value.payload.complexDetections || [])
              .map((detection) => detection.code)
              .filter((detection) => !excludeCode || excludeCode !== detection),
            detection: index !== -1 ? this.value.payload.complexDetections[index] : null,
          }
        }).afterClosed().pipe(
          tap((result) => {
            if (result) {
              const complexDetections = [
                ...(this.value.payload.complexDetections || [])
              ];
              if (index !== -1) {
                complexDetections.splice(index, 1, result);
              } else {
                complexDetections.push(result);
              }
              this.value = {
                ...this.value,
                payload: {
                  ...this.value.payload,
                  complexDetections,
                }
              };
              this.emitChange();
            }
          })
        );
      })
    ).subscribe();
  }

  deleteComplexDetection(index: number) {
    const detections = [
      ...(this.value.payload.complexDetections || [])
    ];
    detections.splice(index, 1);
    this.value = {
      ...this.value,
      payload: {
        ...this.value.payload,
        complexDetections: detections,
      }
    };
    this.emitChange();
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

  mismoConfigChange(value: string) {
    this.value = {
      ...this.value,
      payload: {
        ...this.value.payload,
        mismoPatientMatchingConfiguration: value,
      },
    };

    this.emitChange();
  }

  pmChange(value: boolean) {
    this.value = {
      ...this.value,
      payload: {
        ...this.value.payload,
        activatePatientMatching: value,
      }
    };

    if (!value) {
      this.mismoConfigChange('');
    }

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
            return this.configurationService.getDescriptorById(message.data.id).pipe(
              flatMap((descriptor) => {
                return [
                  new InsertResourcesInCollection({
                    key: 'configurations',
                    values: [descriptor],
                  }),
                  this.messageService.messageToAction(message),
                ];
              })
            );
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
