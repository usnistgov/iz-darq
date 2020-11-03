import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { map, concatMap, flatMap, catchError } from 'rxjs/operators';
import { CoreActionTypes, CoreActions, LoadAdministrationWidgetSuccess, LoadUsersSuccess, LoadUsersFailure, OpenWebContentEditor, OpenEmailTemplateEditor, OpenConfigurationEditor } from './core.actions';
import {
  DamWidgetEffect,
  LoadResourcesInRepository,
  MessageService,
  SetValue,
  OpenEditor,
  OpenEditorFailure,
  DamActionTypes,
  GlobalSave,
  EditorSave,
} from 'ngx-dam-framework';
import { ADMIN_WIDGET } from '../components/admin-widget/admin-widget.component';
import { IUser } from '../../core/model/user.model';
import { handleError } from '../../shared/services/helper.functions';
import { UserService } from '../../core/services/user.service';
import { AdminTabs } from '../components/admin-sidebar/admin-sidebar.component';
import { AdminService } from '../services/admin.service';
import { of, combineLatest } from 'rxjs';



@Injectable()
export class CoreEffects extends DamWidgetEffect {

  @Effect()
  save$ = this.actions$.pipe(
    ofType(DamActionTypes.GlobalSave),
    map((action: GlobalSave) => {
      return new EditorSave({});
    })
  );

  @Effect()
  loadAdminPanel$ = this.actions$.pipe(
    ofType(CoreActionTypes.LoadAdministrationWidget),
    map(() => new LoadAdministrationWidgetSuccess())
  );

  @Effect()
  loadStores$ = this.actions$.pipe(
    ofType(CoreActionTypes.LoadUsers),
    concatMap(() =>
      this.userService.getListOfAllUsers().pipe(
        flatMap((users) => {
          return [
            new SetValue({
              adminActiveTab: AdminTabs.USERS,
            }),
            new LoadResourcesInRepository<IUser>({
              collections: [{
                key: 'users',
                values: users,
              }]
            }),
            new LoadUsersSuccess(users),
          ];
        }),
        catchError(handleError(this.messageService, LoadUsersFailure)),
      )
    )
  );

  @Effect()
  openWebContent$ = this.actions$.pipe(
    ofType(CoreActionTypes.OpenWebContentEditor),
    concatMap((action: OpenWebContentEditor) => {
      return this.adminServce.getWebContent().pipe(
        flatMap((webcontent) => {
          return [
            new SetValue({
              adminActiveTab: AdminTabs.NARRATIVES,
            }),
            new OpenEditor({
              id: action.payload.editor.id,
              editor: action.payload.editor,
              initial: {
                ...webcontent,
              },
              display: {},
            })
          ];
        }),
        catchError((error) => {
          return of(
            this.messageService.actionFromError(error),
            new OpenEditorFailure({ id: action.payload.editor.id }),
          );
        })
      );
    })
  );

  @Effect()
  openEmailTemplate$ = this.actions$.pipe(
    ofType(CoreActionTypes.OpenEmailTemplateEditor),
    concatMap((action: OpenEmailTemplateEditor) => {
      return this.adminServce.getEmailTemplates().pipe(
        flatMap((templates) => {
          return [
            new SetValue({
              adminActiveTab: AdminTabs.EMAILS,
            }),
            new OpenEditor({
              id: action.payload.editor.id,
              editor: action.payload.editor,
              initial: {
                templates,
              },
              display: {},
            })
          ];
        }),
        catchError((error) => {
          return of(
            this.messageService.actionFromError(error),
            new OpenEditorFailure({ id: action.payload.editor.id }),
          );
        })
      );
    })
  );

  @Effect()
  openConfiguration$ = this.actions$.pipe(
    ofType(CoreActionTypes.OpenConfigurationEditor),
    concatMap((action: OpenConfigurationEditor) => {
      return combineLatest([
        this.adminServce.getToolConfiguration(),
        this.adminServce.checkStatus()
      ]).pipe(
        flatMap(([configuration, status]) => {
          return [
            new SetValue({
              adminActiveTab: AdminTabs.CONFIG,
            }),
            new OpenEditor({
              id: action.payload.editor.id,
              editor: action.payload.editor,
              initial: {
                ...configuration,
              },
              display: {
                status: [...status],
              },
            })
          ];
        }),
        catchError((error) => {
          return of(
            this.messageService.actionFromError(error),
            new OpenEditorFailure({ id: action.payload.editor.id }),
          );
        })
      );
    })
  );


  constructor(
    actions$: Actions<CoreActions>,
    private adminServce: AdminService,
    private userService: UserService,
    private messageService: MessageService,
  ) {
    super(ADMIN_WIDGET, actions$);
  }


}
