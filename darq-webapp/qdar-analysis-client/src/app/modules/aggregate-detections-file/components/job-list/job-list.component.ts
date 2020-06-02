import { Component, OnInit, OnDestroy } from '@angular/core';
import { Observable, interval, BehaviorSubject, Subscription } from 'rxjs';
import { IAnalysisJob } from 'src/app/modules/report/model/report.model';
import { Store } from '@ngrx/store';
import { selectJobs, selectCurrentFacility } from '../../store/core.selectors';
import { ReportTemplateService } from '../../../report-template/services/report-template.service';
import { AnalysisService } from '../../../shared/services/analysis.service';
import { switchMap, tap, flatMap, map, concatMap, take } from 'rxjs/operators';
import { tick } from '@angular/core/testing';
import { InsertResourcesInCollection, ConfirmDialogComponent, RxjsStoreHelperService, MessageType, DeleteResourcesFromCollection } from 'ngx-dam-framework';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-job-list',
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.scss']
})
export class JobListComponent implements OnInit, OnDestroy {

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

  constructor(
    private store: Store<any>,
    private analysisService: AnalysisService,
    private helper: RxjsStoreHelperService,
    private dialog: MatDialog,
    public rtService: ReportTemplateService,
  ) {
    this.jobs$ = this.store.select(selectJobs);
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
              return [
                ...message.status === MessageType.SUCCESS ? [new DeleteResourcesFromCollection({
                  key: 'jobs',
                  values: [job.id],
                })] : [],
              ];
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
                return (facility === 'local' ? this.analysisService.getJobs() : this.analysisService.getJobsByFacility(facility)).pipe(
                  map((jobs) => {
                    this.store.dispatch(new InsertResourcesInCollection({
                      key: 'jobs',
                      values: jobs,
                    }));
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

}
