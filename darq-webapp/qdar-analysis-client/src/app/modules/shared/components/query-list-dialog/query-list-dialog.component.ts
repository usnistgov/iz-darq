import { IConfigurationPayload } from './../../../configuration/model/configuration.model';
import { ConfirmDialogComponent, IMessage, MessageService, UserMessage } from 'ngx-dam-framework';
import { map, flatMap, catchError } from 'rxjs/operators';
import { Labelizer } from './../../services/values.service';
import { IQueryDescriptor } from './../../model/query.model';
import { Observable, of, BehaviorSubject, combineLatest, throwError } from 'rxjs';
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { QueryService } from '../../services/query.service';
import { IQuery } from '../../model/query.model';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-query-list-dialog',
  templateUrl: './query-list-dialog.component.html',
  styleUrls: ['./query-list-dialog.component.scss']
})
export class QueryListDialogComponent implements OnInit {

  queries$: BehaviorSubject<IQueryDescriptor[]>;
  filtered$: Observable<IQueryDescriptor[]>;
  filterText$: BehaviorSubject<string>;
  query: IQuery;
  labelizer: Labelizer;
  filterText: string;
  configuration: IConfigurationPayload;
  current: IQuery;
  messages: UserMessage[] = [];

  constructor(
    public dialogRef: MatDialogRef<QueryListDialogComponent>,
    private dialog: MatDialog,
    private messageService: MessageService,
    private queryService: QueryService,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.labelizer = data.labelizer;
    this.current = data.current;
    this.configuration = data.configuration;

    this.queries$ = new BehaviorSubject([]);
    this.filterText$ = new BehaviorSubject('');
    this.queryService.getQueriesForConfiguration(this.configuration).pipe(
      map((ls) => this.queries$.next(ls))
    ).subscribe();
    this.filtered$ = combineLatest([
      this.queries$,
      this.filterText$
    ]).pipe(
      map(([ls, txt]) => {
        return txt ? ls.filter((elm) => elm.name.toLowerCase().includes(txt.toLowerCase())) : ls;
      })
    );
  }

  filter(txt: string) {
    this.filterText$.next(txt);
  }

  deleteQuery(item: IQuery) {
    this.dialog.open(ConfirmDialogComponent, {
      data: {
        action: 'Delete Query : ' + item.name,
        question: 'Are you sure you want to delete this query ?',
      }
    }).afterClosed().pipe(
      flatMap((answer) => {
        if (answer) {
          return this.queryService.deleteQuery(item.id).pipe(
            map((message) => {
              this.displayHTTPApiMessage(message);
              this.query = null;
              this.queryService.getQueries().pipe(
                map((ls) => this.queries$.next(ls))
              ).subscribe();
            }),
            catchError((e) => {
              this.displayHTTPApiError(e);
              return throwError(e);
            })
          );
        } else {
          return of();
        }
      })
    ).subscribe();
  }

  displayHTTPApiMessage(message: IMessage<any>) {
    const m = this.messageService.mergeUserMessage(UserMessage.fromMessage(
      message, {
      closable: true,
    }));
    this.messages.push(m);
  }

  displayHTTPApiError(response: HttpErrorResponse) {
    const m = this.messageService.fromError(response, null, {
      closable: true,
    });
    this.messages.push(m);
  }

  closeNotification(id: string) {
    const deleteInList = (list: any[]) => {
      const idx = list.findIndex((e) => e.id === id);
      if (idx !== -1) {
        list.splice(idx, 1);
      }
    };
    deleteInList(this.messages);
  }

  selectQuery(item: IQueryDescriptor) {
    this.query = null;
    this.queryService.getQuery(item.id).pipe(
      map((q) => {
        this.query = q;
      }),
    ).subscribe();
  }

  ngOnInit(): void {
  }

}
