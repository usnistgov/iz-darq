import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  DamAbstractEditorComponent,
  IEditorMetadata,
  EditorSave,
  ConfirmDialogComponent,
  MessageType,
  RxjsStoreHelperService,
  EditorUpdate,
  TurnOnLoader,
  TurnOffLoader,
  MessageService,
  IMessage
} from 'ngx-dam-framework';
import { Action, Store } from '@ngrx/store';
import { Actions } from '@ngrx/effects';
import { combineLatest, Observable, of, EMPTY, throwError, BehaviorSubject, Subscription } from 'rxjs';
import { concatMap, flatMap, map, take, takeUntil, filter, catchError, tap } from 'rxjs/operators';
import { IADFDescriptor } from '../../model/adf.model';
import * as moment from 'moment';
import { DataTableDialogComponent } from 'src/app/modules/shared/components/data-table-dialog/data-table-dialog.component';
import { QueryDialogComponent } from 'src/app/modules/shared/components/query-dialog/query-dialog.component';
import { selectAllDetections, selectAllCvx, selectPatientTables, selectVaccinationTables } from 'src/app/modules/shared/store/core.selectors';
import { AdfJobDialogComponent } from '../adf-job-dialog/adf-job-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ReportTemplateService } from 'src/app/modules/report-template/services/report-template.service';
import { AnalysisService } from 'src/app/modules/shared/services/analysis.service';
import { ValuesService } from 'src/app/modules/shared/services/values.service';
import { FileService } from '../../services/file.service';
import { selectUserFacilityById } from '../../store/core.selectors';
import { AdfEditDialogComponent } from '../adf-edit-dialog/adf-edit-dialog.component';

export const ADF_FILE_LIST_EDITOR_METADATA: IEditorMetadata = {
  id: 'ADF_FILE_LIST_EDITOR_METADATA',
  title: 'File List'
};


@Component({
  selector: 'app-files-list-editor',
  templateUrl: './files-list-editor.component.html',
  styleUrls: ['./files-list-editor.component.scss']
})
export class FilesListEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  files$: BehaviorSubject<IADFDescriptor[]>;
  filtered$: Observable<IADFDescriptor[]>;
  facilityId$: Observable<string>;
  tagFilter: string[] = [];
  workspaceSubscription: Subscription;

  constructor(
    store: Store<any>,
    actions$: Actions,
    private helper: RxjsStoreHelperService,
    private analysisService: AnalysisService,
    private rt: ReportTemplateService,
    private valueService: ValuesService,
    private router: Router,
    private dialog: MatDialog,
    private fileService: FileService,
    private message: MessageService,
  ) {
    super(
      ADF_FILE_LIST_EDITOR_METADATA,
      actions$,
      store,
    );
    this.files$ = new BehaviorSubject([]);
    this.workspaceSubscription = this.currentSynchronized$.pipe(
      takeUntil(this.active$.pipe(
        filter(value => value.editor.id !== ADF_FILE_LIST_EDITOR_METADATA.id),
      )),
      map((value) => {
        const files = [...value.files];
        files.sort((a, b) => b.uploadedOn - a.uploadedOn);
        this.files$.next(files);
      }),
    ).subscribe();
    this.filtered$ = this.files$.asObservable();
    this.facilityId$ = this.active$.pipe(
      takeUntil(this.active$.pipe(
        filter(value => value.editor.id !== ADF_FILE_LIST_EDITOR_METADATA.id),
      )),
      map((value) => {
        return value.display.id;
      }),
    );
  }

  updateFilter(tags: string[]) {
    if (tags.length === 0) {
      this.filtered$ = this.files$.asObservable();
    } else {
      const normalizedTags = tags.map((tag) => tag.toLowerCase());
      this.filtered$ = this.files$.asObservable().pipe(
        map((files) => {
          return this.filterListByTags(files, normalizedTags);
        })
      );
    }
  }

  filterListByTags(files: IADFDescriptor[], tags: string[]) {
    return files.filter((file) => {
      if (file.tags && file.tags.length > 0) {
        for (const tag of tags) {
          if (!file.tags.includes(tag.toLowerCase())) {
            return false;
          }
        }
        return true;
      }
      return false;
    });
  }

  edit(adf: IADFDescriptor) {
    this.dialog.open(AdfEditDialogComponent, {
      data: {
        adf,
      }
    }).afterClosed().pipe(
      flatMap((change) => {
        if (change) {
          return this.helper.getMessageAndHandle(
            this.store,
            () => {
              return this.fileService.updateFile(adf.id, change);
            },
            this.handleSuccessAndRefresh
          );
        } else {
          return of();
        }
      })
    ).subscribe();
  }

  onEditorSave(action: EditorSave): Observable<Action> {
    return of();
  }

  editorDisplayNode(): Observable<any> {
    return this.elementId$.pipe(
      flatMap((facilityId) => this.store.select(selectUserFacilityById, { id: facilityId })),
    );
  }

  query(fileId: string) {
    combineLatest([
      this.fileService.getFileMetadata(fileId),
      this.store.select(selectAllDetections),
      this.store.select(selectAllCvx),
      this.store.select(selectPatientTables),
      this.store.select(selectVaccinationTables),
    ]).pipe(
      map(([file, detections, cvxCodes, patientTables, vaccinationTables]) => {
        return {
          options: this.valueService.getFieldOptions({
            detections: detections.filter((d) => file.configuration.detections.includes(d.id)),
            ageGroups: file.configuration.ageGroups,
            cvxs: cvxCodes,
            reportingGroups: {},
            tables: {
              vaccinationTables,
              patientTables,
            }
          }, {}),
          configuration: file.configuration,
        };
      }),
    ).pipe(
      take(1),
      flatMap(({ options, configuration }) => {
        return this.dialog.open(QueryDialogComponent, {
          disableClose: true,
          minWidth: '70vw',
          maxWidth: '93vw',
          maxHeight: '95vh',
          data: {
            options,
            configuration,
            query: this.rt.getEmptyDataViewQuery(),
          }
        }).afterClosed().pipe(
          flatMap((data) => {
            if (data) {
              this.store.dispatch(new TurnOnLoader({ blockUI: true }));
              return this.analysisService.executeQuery(fileId, data).pipe(
                map((result) => {
                  this.store.dispatch(new TurnOffLoader(true));
                  this.dialog.open(DataTableDialogComponent, {
                    minWidth: '70vw',
                    maxWidth: '93vw',
                    maxHeight: '95vh',
                    data: {
                      labelizer: this.valueService.getQueryValuesLabel(options),
                      table: result,
                      configuration,
                    }
                  });
                  return result;
                }),
                catchError((er) => {
                  this.store.dispatch(new TurnOffLoader(true));
                  this.store.dispatch(this.message.actionFromError(er));
                  return throwError(er);
                })
              );
            }
            return of();
          }),
        );
      }),
    ).subscribe();
  }

  analyse(adf: IADFDescriptor) {
    this.fileService.templatesForFile(adf.id).pipe(
      flatMap((templates) => {
        return this.dialog.open(AdfJobDialogComponent, {
          data: {
            templates,
          }
        }).afterClosed().pipe(
          flatMap((value) => {
            if (value) {
              return this.helper.getMessageAndHandle(
                this.store,
                () => {
                  return this.analysisService.submitJob({
                    name: value.name,
                    templateId: value.templateId,
                    adfId: adf.id,
                  });
                },
                (message) => {
                  if (message.status === MessageType.SUCCESS) {
                    this.facilityId$.pipe(
                      take(1),
                      map((id) => {
                        this.router.navigate(['/', 'adf', 'dashboard', id, 'jobs']);
                      })
                    ).subscribe();
                  }
                  return EMPTY;
                }
              );
            }
            return EMPTY;
          }),
        );
      }),
    ).subscribe();
  }

  remove(meta: IADFDescriptor) {
    this.dialog.open(
      ConfirmDialogComponent,
      {
        data: {
          action: 'Delete ADF',
          question: 'Are you sure you want to delete file ' + meta.name + ' ? ',
        }
      }
    ).afterClosed().pipe(
      concatMap((answer) => {
        if (answer) {
          return this.helper.getMessageAndHandle(
            this.store,
            () => {
              return this.fileService.deleteFile(meta.id);
            },
            this.handleSuccessAndRefresh
          );
        }
      }),
    ).subscribe();
  }

  handleSuccessAndRefresh = (message: IMessage<any>): Observable<Action> => {
    if (message.status === MessageType.SUCCESS) {
      return this.facilityId$.pipe(
        take(1),
        flatMap((facilityId) => {
          return this.fileService.getListByFacility(facilityId).pipe(
            map((files) => {
              return new EditorUpdate({
                value: { files },
                updateDate: true,
              });
            }),
          );
        })
      );
    }
    return EMPTY;
  }

  age(date: Date) {
    return moment(date).fromNow();
  }

  onDeactivate(): void {
  }

  ngOnDestroy() {
    if (this.workspaceSubscription) {
      this.workspaceSubscription.unsubscribe();
    }
  }

  ngOnInit(): void {
  }

}
