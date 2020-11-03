import { Component, OnInit } from '@angular/core';
import { Action, Store } from '@ngrx/store';
import { selectUserFacilityById } from '../../store/core.selectors';
import { Observable, of } from 'rxjs';
import { IReportDescriptor } from '../../../report/model/report.model';
import {
  ConfirmDialogComponent,
  RxjsStoreHelperService,
  DamAbstractEditorComponent,
  EditorSave,
  EditorUpdate,
} from 'ngx-dam-framework';
import { map, concatMap, flatMap, filter, takeUntil, take } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { ReportService } from '../../../report/services/report.service';
import { Actions } from '@ngrx/effects';
import { IEditorMetadata } from 'ngx-dam-framework';

export const REPORT_LIST_EDITOR_METADATA: IEditorMetadata = {
  id: 'REPORT_LIST_EDITOR_METADATA',
  title: 'Reports List'
};

@Component({
  selector: 'app-report-list',
  templateUrl: './report-list-editor.component.html',
  styleUrls: ['./report-list-editor.component.scss']
})
export class ReportListEditorComponent extends DamAbstractEditorComponent implements OnInit {

  reports$: Observable<IReportDescriptor[]>;
  facilityId$: Observable<string>;

  constructor(
    store: Store<any>,
    actions$: Actions,
    private dialog: MatDialog,
    private helper: RxjsStoreHelperService,
    private reportService: ReportService,
  ) {
    super(
      REPORT_LIST_EDITOR_METADATA,
      actions$,
      store,
    );

    this.reports$ = this.currentSynchronized$.pipe(
      takeUntil(this.active$.pipe(
        filter(value => value.editor.id !== REPORT_LIST_EDITOR_METADATA.id),
      )),
      map((value) => [...value.reports]),
    );

    this.facilityId$ = this.active$.pipe(
      takeUntil(this.active$.pipe(
        filter(value => value.editor.id !== REPORT_LIST_EDITOR_METADATA.id),
      )),
      map((value) => {
        return value.display.id;
      }),
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
              return this.facilityId$.pipe(
                take(1),
                flatMap((facilityId) => {
                  return this.reportService.publishedForFacility(facilityId).pipe(
                    map((reports) => {
                      return new EditorUpdate({
                        value: { reports },
                        updateDate: true,
                      });
                    }),
                  );
                })
              );
            }
          );
        }
      }),
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

  onDeactivate(): void {
  }

  ngOnInit(): void {
  }

}
