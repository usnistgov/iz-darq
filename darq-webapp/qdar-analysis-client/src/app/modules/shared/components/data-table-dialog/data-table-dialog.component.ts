import { Store } from '@ngrx/store';
import { IMessage, MessageService, UserMessage } from 'ngx-dam-framework';
import { IQuerySaveDetails, QuerySaveDialogComponent } from './../query-save-dialog/query-save-dialog.component';
import { IConfigurationPayload } from './../../../configuration/model/configuration.model';
import { IQuerySaveRequest, QueryService } from './../../services/query.service';
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataTableComponent } from '../data-table/data-table.component';
import { IDataTable } from '../../../report/model/report.model';
import { Labelizer } from '../../services/values.service';
import { Field } from '../../../report-template/model/analysis.values';
import { of, throwError } from 'rxjs';
import { catchError, flatMap, map } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-data-table-dialog',
  templateUrl: './data-table-dialog.component.html',
  styleUrls: ['./data-table-dialog.component.scss']
})
export class DataTableDialogComponent implements OnInit {

  fields = Field;
  table: IDataTable;
  labelizer: Labelizer;
  configuration: IConfigurationPayload;
  messages: UserMessage[] = [];

  constructor(
    public dialogRef: MatDialogRef<DataTableComponent>,
    private dialog: MatDialog,
    private queryService: QueryService,
    private messageService: MessageService,
    private store: Store<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.table = data.table;
    this.labelizer = data.labelizer;
    this.configuration = data.configuration;
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

  saveQuery() {
    this.dialog.open(QuerySaveDialogComponent).afterClosed().pipe(
      flatMap((result: IQuerySaveDetails) => {
        if (result) {
          const query: IQuerySaveRequest = {
            id: null,
            name: result.name,
            query: this.table.query,
            configuration: this.configuration,
          };
          return this.queryService.saveQuery(query).pipe(
            map((m) => {
              this.displayHTTPApiMessage(m);
            }),
            catchError((e) => {
              this.displayHTTPApiError(e);
              return throwError(e);
            })
          );
        }
        return of();
      })
    ).subscribe();
  }

  ngOnInit(): void {
  }

}
