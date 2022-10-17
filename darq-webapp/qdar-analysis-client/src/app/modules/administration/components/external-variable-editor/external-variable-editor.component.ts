import { selectFacilities } from './../../../facility/store/core.selectors';
import { ExternalVariableDialogComponent } from './../external-variable-dialog/external-variable-dialog.component';
import { ExternalVariableService } from './../../services/external-variables.service';
import { IExternalQueryVariable } from './../../../shared/model/query-variable.model';
import { DamAbstractEditorComponent, EditorSave, IEditorMetadata, MessageService, EditorUpdate, ConfirmDialogComponent, IMessage } from 'ngx-dam-framework';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';
import { BehaviorSubject, Observable, combineLatest, of, throwError, Subscription } from 'rxjs';
import { map, take, flatMap, catchError, tap } from 'rxjs/operators';
import { IFacilityDescriptor } from 'src/app/modules/facility/model/facility.model';

export const EXTERNAL_VARIABLE_EDITOR_METADATA: IEditorMetadata = {
  id: 'EXTERNAL_VARIABLE_EDITOR_METADATA',
  title: 'External Variables'
};


@Component({
  selector: 'app-external-variable-editor',
  templateUrl: './external-variable-editor.component.html',
  styleUrls: ['./external-variable-editor.component.scss']
})
export class ExternalVariableEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  values$: BehaviorSubject<IExternalQueryVariable[]>;
  filterTxt$: BehaviorSubject<string>;
  filteredValues$: Observable<IExternalQueryVariable[]>;
  facilities$: Observable<IFacilityDescriptor[]>;
  facilityValueCollapse: Record<string, boolean> = {};
  facilityNameMap: Record<string, string> = {};
  subs: Subscription;
  txt: string;

  constructor(
    actions$: Actions,
    store: Store<any>,
    private dialog: MatDialog,
    private externalVariableService: ExternalVariableService,
    private message: MessageService,
  ) {
    super(
      EXTERNAL_VARIABLE_EDITOR_METADATA,
      actions$,
      store,
    );
    this.values$ = new BehaviorSubject([]);
    this.filterTxt$ = new BehaviorSubject('');
    this.facilities$ = this.store.select(selectFacilities);
    this.facilities$.pipe(
      take(1),
      map((facilities) => {
        this.facilityNameMap = facilities.reduce((acc, v) => {
          return {
            ...acc,
            [v.id]: v.name,
          };
        }, {});
      })
    ).subscribe();
    this.subs = this.currentSynchronized$.pipe(
      map((content) => {
        this.values$.next(content.variables || []);
      })
    ).subscribe();
    this.filteredValues$ = combineLatest([
      this.values$,
      this.filterTxt$
    ]).pipe(
      map(([values, txt]) => {
        return txt ? values.filter((v) => {
          const id = v.id.toLowerCase().includes(txt.toLowerCase());
          const name = v.name.toLowerCase().includes(txt.toLowerCase());
          const description = v.description.toLowerCase().includes(txt.toLowerCase());
          const tag = (v.tags || []).map((t) => txt.toLowerCase().includes(t.toLowerCase())).includes(true);
          return id || name || description || tag;
        }) : values;
      })
    );
  }

  filterTxtChange(v: string) {
    this.filterTxt$.next(v);
  }

  toggleShowIISValues(id: string) {
    this.facilityValueCollapse[id] = !this.facilityValueCollapse[id];
  }

  openAddVariableDialog() {
    this.facilities$.pipe(
      take(1),
      flatMap((facilities) => {
        return this.dialog.open(ExternalVariableDialogComponent, {
          data: {
            edit: false,
            facilities,
          }
        }).afterClosed().pipe(
          flatMap((value: IExternalQueryVariable) => {
            if (value) {
              return this.externalVariableService.createVariable(value).pipe(
                flatMap((message) => this.notifyAndGetVariableList(message)),
                catchError((e) => {
                  this.store.dispatch(this.message.actionFromError(e));
                  return throwError(e);
                })
              );
            } else {
              return of();
            }
          }));
      })
    ).subscribe();
  }

  notifyAndGetVariableList(message: IMessage<any>): Observable<IExternalQueryVariable[]> {
    this.store.dispatch(this.message.messageToAction(message));
    return this.externalVariableService.getVariables().pipe(
      tap((variables) => {
        this.store.dispatch(new EditorUpdate({ value: { variables }, updateDate: true }));
      }),
    );
  }

  deleteVariable(variable: IExternalQueryVariable) {
    this.dialog.open(ConfirmDialogComponent, {
      data: {
        action: 'Delete Variable',
        question: 'Are you sure you want to delete variable ' + variable.name + ' ?'
      }
    }).afterClosed().pipe(
      flatMap((answer) => {
        if (answer) {
          return this.externalVariableService.deleteVariable(variable.id).pipe(
            flatMap((message) => this.notifyAndGetVariableList(message)),
            catchError((e) => {
              this.store.dispatch(this.message.actionFromError(e));
              return throwError(e);
            }),
          );
        } else {
          return of();
        }
      })
    ).subscribe();
  }

  editVariable(variable: IExternalQueryVariable) {
    this.facilities$.pipe(
      take(1),
      flatMap((facilities) => {
        return this.dialog.open(ExternalVariableDialogComponent, {
          data: {
            edit: true,
            value: variable,
            facilities,
          }
        }).afterClosed().pipe(
          flatMap((value: IExternalQueryVariable) => {
            if (value) {
              return this.externalVariableService.saveVariable(value).pipe(
                flatMap((message) => this.notifyAndGetVariableList(message)),
                catchError((e) => {
                  this.store.dispatch(this.message.actionFromError(e));
                  return throwError(e);
                })
              );
            } else {
              return of();
            }
          }));
      })
    ).subscribe();
  }

  onEditorSave(action: EditorSave): Observable<Action> {
    return of();
  }

  editorDisplayNode(): Observable<any> {
    return of();
  }
  ngOnDestroy() {
    if (this.subs) {
      this.subs.unsubscribe()
    }
  }
  onDeactivate(): void {
  }
  ngOnInit(): void {
  }

}
