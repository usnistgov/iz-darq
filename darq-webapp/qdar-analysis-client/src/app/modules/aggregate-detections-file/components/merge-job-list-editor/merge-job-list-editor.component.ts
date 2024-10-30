import { Component, OnInit, OnDestroy } from '@angular/core';
import { Observable, interval, BehaviorSubject, Subscription, of, EMPTY } from 'rxjs';
import { Action, Store } from '@ngrx/store';
import { selectCurrentFacility, selectUserFacilities, selectUserFacilityById } from '../../store/core.selectors';
import { switchMap, flatMap, map, concatMap, take, takeUntil, filter } from 'rxjs/operators';
import {
  ConfirmDialogComponent,
  RxjsStoreHelperService,
  MessageType,
  DamAbstractEditorComponent,
  EditorSave,
  EditorUpdate
} from 'ngx-dam-framework';
import { MatDialog } from '@angular/material/dialog';
import { Actions } from '@ngrx/effects';
import { IEditorMetadata } from 'ngx-dam-framework';
import { IADFMergeJobDescriptor } from '../../model/adf.model';
import { FileService } from '../../services/file.service';

export const MERGE_JOB_LIST_EDITOR_METADATA: IEditorMetadata = {
  id: 'MERGE_JOB_LIST_EDITOR_METADATA',
  title: 'ADF Merge Job List'
};

@Component({
  selector: 'app-merge-job-list',
  templateUrl: './merge-job-list-editor.component.html',
  styleUrls: ['./merge-job-list-editor.component.scss']
})
export class MergeJobListEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  facilityMap$: Observable<Record<string, string>>;
  jobs$: Observable<IADFMergeJobDescriptor[]>;
  refreshRateOptions = [
    {
      label: '2s',
      value: 2000,
    },
    {
      label: '5s',
      value: 5000,
    },
    {
      label: '10s',
      value: 10000,
    },
    {
      label: '20s',
      value: 20000,
    },
    {
      label: '1m',
      value: 60000,
    },
    {
      label: '10m',
      value: 600000,
    },
  ];
  rate = new BehaviorSubject(2000);
  referesh: Subscription;
  facilityId$: Observable<string>;

  constructor(
    store: Store<any>,
    actions$: Actions,
    private adfService: FileService,
    private helper: RxjsStoreHelperService,
    private dialog: MatDialog,
  ) {
    super(
      MERGE_JOB_LIST_EDITOR_METADATA,
      actions$,
      store,
    );

    this.jobs$ = this.currentSynchronized$.pipe(
      takeUntil(this.active$.pipe(
        filter(value => value.editor.id !== MERGE_JOB_LIST_EDITOR_METADATA.id),
      )),
      map((value) => [...value.jobs]),
    );

    this.facilityId$ = this.active$.pipe(
      takeUntil(this.active$.pipe(
        filter(value => value.editor.id !== MERGE_JOB_LIST_EDITOR_METADATA.id),
      )),
      map((value) => {
        return value.display.id;
      }),
    );
    this.facilityMap$ = this.store.select(selectUserFacilities).pipe(
      map((facilities) => {
        const facilityMap = {};
        facilities.forEach((f) => {
          facilityMap[f.id] = f.name;
        });
        return facilityMap;
      }),
    );
  }

  changeRate(n: number) {
    this.rate.next(n);
  }

  get rateValue() {
    return this.rate.getValue();
  }

  remove(job: IADFMergeJobDescriptor) {
    this.dialog.open(
      ConfirmDialogComponent,
      {
        data: {
          action: 'Delete Merge Job',
          question: 'Are you sure you want to delete merge job "' + job.name + '" (the resulting merged ADF will not be deleted) ? ',
        }
      }
    ).afterClosed().pipe(
      concatMap((answer) => {
        if (answer) {
          return this.helper.getMessageAndHandle(
            this.store,
            () => {
              return this.adfService.removeJob(job.id);
            },
            (message) => {
              if (message.status === MessageType.SUCCESS) {
                this.refresh().pipe(take(1)).subscribe();
              }
              return EMPTY;
            }
          );
        }
      }),
    ).subscribe();
  }

  refresh() {
    return this.store.select(selectCurrentFacility).pipe(
      take(1),
      flatMap((facility) => {
        return (facility.id === 'local' ? this.adfService.getJobs() : this.adfService.getJobsByFacility(facility.id))
          .pipe(
            map((jobs) => {
              this.store.dispatch(new EditorUpdate({ value: { jobs }, updateDate: true }));
            }),
          );
      }),
    );
  }

  ngOnInit(): void {
    this.referesh = this.rate.asObservable().pipe(
      switchMap((rate) => {
        return interval(rate).pipe(
          flatMap(() => {
            return this.refresh();
          })
        );
      })
    ).subscribe();
  }

  ngOnDestroy() {
    if (this.referesh) {
      this.referesh.unsubscribe();
    }
  }

  onEditorSave(action: EditorSave): Observable<Action> {
    return of();
  }

  editorDisplayNode(): Observable<any> {
    return this.elementId$.pipe(
      flatMap((facilityId) => this.store.select(selectUserFacilityById, { id: facilityId })),
    );
  }

  onDeactivate(): void {
  }

}
