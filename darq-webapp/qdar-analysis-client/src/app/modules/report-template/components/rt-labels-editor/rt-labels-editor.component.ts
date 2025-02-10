import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import {
  DamAbstractEditorComponent,
  IEditorMetadata,
  EditorSave,
  selectPayloadData,
  IWorkspaceCurrent,
  LoadPayloadData,
  MessageService,
  Message,
} from '@usnistgov/ngx-dam-framework-legacy';
import { Store, Action } from '@ngrx/store';
import { Actions } from '@ngrx/effects';
import { Observable, Subscription, throwError, combineLatest, of } from 'rxjs';
import { map, take, concatMap, flatMap, catchError, withLatestFrom, tap } from 'rxjs/operators';
import { IReportTemplate } from '../../model/report-template.model';
import { IConfigurationDescriptor } from 'src/app/modules/configuration/model/configuration.model';
import { ReportTemplateService } from '../../services/report-template.service';
import { Action as ResourceAction } from 'src/app/modules/core/model/action.enum';
import { ResourceType } from '../../../core/model/resouce-type.enum';
import { PermissionService } from '../../../core/services/permission.service';
import { MatDialog } from '@angular/material/dialog';
import { CustomLabelDialogComponent } from '../../../shared/components/custom-label-dialog/custom-label-dialog.component';
import { selectAllDetections, selectDetectionById } from '../../../shared/store/core.selectors';
import { MessageType } from '@usnistgov/ngx-dam-framework-legacy';

export const RT_LABEL_EDITOR_METADATA: IEditorMetadata = {
  id: 'RT_LABEL_EDITOR_ID',
  title: 'Report Labels'
};

export interface IDetectionLabel {
  valid: boolean;
  target: string;
  default: string;
  custom: string;
}

@Component({
  selector: 'app-rt-labels-editor',
  templateUrl: './rt-labels-editor.component.html',
  styleUrls: ['./rt-labels-editor.component.scss']
})
export class RtLabelsEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  viewOnly$: Observable<boolean>;
  isPublished: boolean;
  value: IReportTemplate;
  wSub: Subscription;

  detectionText = '';
  detectionLabelTab = true;
  detectionLabels: IDetectionLabel[] = [];
  @ViewChild('detectionTextArea') detectionTextElm: ElementRef;

  constructor(
    store: Store<any>,
    actions$: Actions,
    private dialog: MatDialog,
    private permissionService: PermissionService,
    private reportTemplateService: ReportTemplateService,
    private messageService: MessageService,
  ) {
    super(
      RT_LABEL_EDITOR_METADATA,
      actions$,
      store,
    );

    this.wSub = this.currentSynchronized$.pipe(
      flatMap((template: IReportTemplate) => {
        this.isPublished = template.published;
        this.viewOnly$ = this.permissionService.abilities$.pipe(
          map((abilities) => {
            return abilities.onResourceCant(ResourceAction.EDIT, ResourceType.REPORT_TEMPLATE, template);
          })
        );

        this.value = {
          ...template,
        };

        return this.updateDetectionLabels(this.value.customDetectionLabels);
      }),
    ).subscribe();
  }

  updateDetectionLabels(customLabels: Record<string, string>, emitChange: boolean = false): Observable<IDetectionLabel[]> {
    return this.makeDetectionLabelsArray(customLabels).pipe(
      map((list) => {
        this.detectionLabels = [
          ...list,
        ];
        this.makeDetectionText();
        if (emitChange) {
          this.emitChange(this.detectionLabels);
        }
        return this.detectionLabels;
      })
    );
  }

  makeDetectionLabelsArray(customLabels: Record<string, string>): Observable<IDetectionLabel[]> {
    if (customLabels) {
      return combineLatest(Object.keys(customLabels).map((target) => {
        return this.store.select(selectDetectionById, { id: target })
          .pipe(
            take(1),
            map((resource) => (resource ? {
              valid: true,
              target,
              custom: customLabels[target],
              default: resource.description,
            } : {
              valid: false,
              target,
              custom: customLabels[target],
              default: '',
            })),
          );
      }));
    }
    return of([]);
  }

  openCustomDetectionDialog(elm?: any) {
    this.store.select(selectAllDetections).pipe(
      map((detections) => {
        return detections
          // Keep detections from configuration
          .filter((d) => {
            return this.value.configuration.detections.find((e) => {
              return e === d.id;
            });
          })
          // Keep detections that are not already defined
          .filter((d) => {
            return !this.detectionLabels.find((e) => {
              return e.target === d.id;
            });
          })
          .map((d) => ({
            value: d.id,
            label: `${d.description}`,
          }));
      }),
      flatMap((options) => {
        return this.dialog.open(CustomLabelDialogComponent, {
          data: {
            type: 'Detection',
            options,
            editMode: !!elm,
            ...elm,
          },
        }).afterClosed();
      }),
      flatMap((detection) => {
        if (detection) {
          const idx = this.detectionLabels.findIndex((e) => e.target === detection.target);
          if (idx !== -1) {
            this.detectionLabels.splice(idx, 1, detection);
          } else {
            this.detectionLabels.push(detection);
          }
          this.makeDetectionText();
          this.emitChange(this.detectionLabels);
        }
        return of();
      }),
    ).subscribe();
  }

  remove(i: number) {
    this.detectionLabels.splice(i, 1);
    this.emitChange(this.detectionLabels);
  }

  makeDetectionText() {
    this.detectionText = this.detectionLabels.reduce((acc, v) => {
      return acc + `\n${v.target}, ${v.custom}`;
    }, '');
  }

  copy() {
    navigator.clipboard.writeText(this.detectionText);
  }

  paste() {
    navigator.clipboard.readText()
      .then(text => {
        const labels = this.parse(text);
        const existing = this.getLabelMap(this.detectionLabels);
        for (const key of Object.keys(labels)) {
          existing[key] = labels[key];
        }

        this.updateDetectionLabels(existing, true).subscribe();
      })
      .catch(err => {
        this.store.dispatch(this.messageService.messageToAction(new Message(MessageType.FAILED, err, undefined)));
      });
  }

  parse(txt: string): Record<string, string> {
    const values = {};
    if (txt) {
      const lines = txt.split('\n');
      for (const line of lines) {
        const fields = line.split(',');
        if (fields.length >= 2 && fields[0].startsWith('MQE')) {
          values[fields[0]] = fields[1].trim();
        }
      }
    }
    return values;
  }

  clear() {
    this.detectionLabels = [];
    this.makeDetectionText();
    this.emitChange(this.detectionLabels);
  }

  getLabelMap(list: IDetectionLabel[]): Record<string, string> {
    return list.reduce((acc, label) => {
      acc[label.target] = label.custom;
      return acc;
    }, {});
  }

  emitChange(list: IDetectionLabel[]) {
    const customDetectionLabels = this.getLabelMap(list);
    this.editorChange({
      ...this.value,
      customDetectionLabels,
    }, true);
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
