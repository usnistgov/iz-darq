import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectReports } from '../../store/core.selectors';
import { Observable, combineLatest } from 'rxjs';
import { IReportDescriptor } from '../../../report/model/report.model';
import { selectIsAdmin, selectUsername, ConfirmDialogComponent, RxjsStoreHelperService, MessageType, DeleteResourcesFromCollection } from 'ngx-dam-framework';
import { map, concatMap } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { ReportService } from '../../../report/services/report.service';

@Component({
  selector: 'app-report-list',
  templateUrl: './report-list.component.html',
  styleUrls: ['./report-list.component.scss']
})
export class ReportListComponent implements OnInit {

  reports$: Observable<IReportDescriptor[]>;
  isAdmin$: Observable<boolean>;
  username$: Observable<string>;
  userInfo$: Observable<{ isAdmin: boolean, username: string }>;

  constructor(
    private store: Store<any>,
    private dialog: MatDialog,
    private helper: RxjsStoreHelperService,
    private reportService: ReportService,
  ) {
    this.reports$ = this.store.select(selectReports);
    this.isAdmin$ = store.select(selectIsAdmin);
    this.username$ = store.select(selectUsername);
    this.userInfo$ = combineLatest([
      this.isAdmin$,
      this.username$,
    ]).pipe(
      map(([isAdmin, username]) => {
        return {
          isAdmin,
          username,
        };
      })
    );
  }

  remove(report: IReportDescriptor) {
    this.dialog.open(
      ConfirmDialogComponent,
      {
        data: {
          action: 'Delete Report',
          question: 'Are you sure you want to delete report ' + report.name + ' (this report is published) ? ',
        }
      }
    ).afterClosed().pipe(
      concatMap((answer) => {
        if (answer) {
          return this.helper.getMessageAndHandle(
            this.store,
            () => {
              return this.reportService.delete(report.id);
            },
            (message) => {
              return [
                ...message.status === MessageType.SUCCESS ? [new DeleteResourcesFromCollection({
                  key: 'reports',
                  values: [report.id],
                })] : [],
              ];
            }
          );
        }
      }),
    ).subscribe();
  }

  ngOnInit(): void {
  }

}
