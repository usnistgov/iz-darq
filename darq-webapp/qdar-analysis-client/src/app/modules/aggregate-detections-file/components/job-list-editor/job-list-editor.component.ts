import { Component, OnInit, OnDestroy } from '@angular/core';
import { Observable, interval, BehaviorSubject, Subscription, of, EMPTY } from 'rxjs';
import { IAnalysisJob } from 'src/app/modules/report/model/report.model';
import { Action, Store } from '@ngrx/store';
import { selectCurrentFacility, selectUserFacilityById } from '../../store/core.selectors';
import { ReportTemplateService } from '../../../report-template/services/report-template.service';
import { AnalysisService } from '../../../shared/services/analysis.service';
import { switchMap, flatMap, map, concatMap, take, takeUntil, filter } from 'rxjs/operators';
import {
  ConfirmDialogComponent,
  RxjsStoreHelperService,
  MessageType,
  DamAbstractEditorComponent,
  EditorSave,
  EditorUpdate
} from '@usnistgov/ngx-dam-framework-legacy';
import { MatDialog } from '@angular/material/dialog';
import { Actions } from '@ngrx/effects';
import { IEditorMetadata } from '@usnistgov/ngx-dam-framework-legacy';

export const ANALYSIS_JOB_LIST_EDITOR_METADATA: IEditorMetadata = {
  id: 'ANALYSIS_JOB_LIST_EDITOR_METADATA',
  title: 'Analysis Job List'
};

@Component({
  selector: 'app-job-list',
  templateUrl: './job-list-editor.component.html',
  styleUrls: ['./job-list-editor.component.scss']
})
export class JobListEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  jobs$: Observable<IAnalysisJob[]>;
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
    private analysisService: AnalysisService,
    private helper: RxjsStoreHelperService,
    private dialog: MatDialog,
    public rtService: ReportTemplateService,
  ) {
    super(
      ANALYSIS_JOB_LIST_EDITOR_METADATA,
      actions$,
      store,
    );

    this.jobs$ = this.currentSynchronized$.pipe(
      takeUntil(this.active$.pipe(
        filter(value => value.editor.id !== ANALYSIS_JOB_LIST_EDITOR_METADATA.id),
      )),
      map((value) => [...value.jobs]),
    );

    this.facilityId$ = this.active$.pipe(
      takeUntil(this.active$.pipe(
        filter(value => value.editor.id !== ANALYSIS_JOB_LIST_EDITOR_METADATA.id),
      )),
      map((value) => {
        return value.display.id;
      }),
    );
  }

  changeRate(n: number) {
    this.rate.next(n);
  }

  get rateValue() {
    return this.rate.getValue();
  }

  remove(job: IAnalysisJob) {
    this.dialog.open(
      ConfirmDialogComponent,
      {
        data: {
          action: 'Delete Analysis',
          question: 'Are you sure you want to delete analysis ' + job.name + ' (the report is going to be deleted) ? ',
        }
      }
    ).afterClosed().pipe(
      concatMap((answer) => {
        if (answer) {
          return this.helper.getMessageAndHandle(
            this.store,
            () => {
              return this.analysisService.removeJob(job.id);
            },
            (message) => {
              if (message.status === MessageType.SUCCESS) {
                return this.facilityId$.pipe(
                  take(1),
                  flatMap((facilityId) => {
                    return this.analysisService.getJobsByFacility(facilityId).pipe(
                      map((jobs) => {
                        return new EditorUpdate({
                          value: { jobs },
                          updateDate: true,
                        });
                      }),
                    );
                  })
                );
              }
              return EMPTY;
            }
          );
        }
      }),
    ).subscribe();
  }


  ngOnInit(): void {
    this.referesh = this.rate.asObservable().pipe(
      switchMap((rate) => {
        return interval(rate).pipe(
          flatMap(() => {
            return this.store.select(selectCurrentFacility).pipe(
              take(1),
              flatMap((facility) => {
                return (facility.id === 'local' ? this.analysisService.getJobs() : this.analysisService.getJobsByFacility(facility.id))
                  .pipe(
                    map((jobs) => {
                      this.store.dispatch(new EditorUpdate({ value: { jobs }, updateDate: true }));
                    }),
                  );
              }),
            );
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
