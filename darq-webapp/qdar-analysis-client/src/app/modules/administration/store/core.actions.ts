import { Action } from '@ngrx/store';
import { IUser } from '../../core/model/user.model';
import { OpenEditorBase, IEditorMetadata } from '@usnistgov/ngx-dam-framework-legacy';

export enum CoreActionTypes {
  LoadAdministrationWidget = '[Administration] Load Administration Widget',
  LoadAdministrationWidgetSuccess = '[Administration] Load Administration Widget Success',
  LoadAdministrationWidgetFailure = '[Administration] Load Administration Widget Failure',

  OpenWebContentEditor = '[Administration] Open Web Content Editor',
  OpenEmailTemplateEditor = '[Administration] Open Email Template Editor',
  OpenConfigurationEditor = '[Administration] Open Configuration Editor',
  OpenExternalVariablesEditor = '[Administration] Open External Variables Editor',

  LoadUsers = '[Administration User Management] Load Users',
  LoadUsersSuccess = '[Administration User Management] Load Users Success',
  LoadUsersFailure = '[Administration User Management] Load Users Failure',
}

export class LoadAdministrationWidget implements Action {
  readonly type = CoreActionTypes.LoadAdministrationWidget;
}
export class LoadAdministrationWidgetSuccess implements Action {
  readonly type = CoreActionTypes.LoadAdministrationWidgetSuccess;
}
export class LoadAdministrationWidgetFailure implements Action {
  readonly type = CoreActionTypes.LoadAdministrationWidgetFailure;
}
export class LoadUsers implements Action {
  readonly type = CoreActionTypes.LoadUsers;
}
export class LoadUsersSuccess implements Action {
  readonly type = CoreActionTypes.LoadUsersSuccess;
  constructor(public payload: IUser[]) { }
}
export class LoadUsersFailure implements Action {
  readonly type = CoreActionTypes.LoadUsersFailure;
  constructor(public payload: any) { }
}

export class OpenWebContentEditor extends OpenEditorBase {
  readonly type = CoreActionTypes.OpenWebContentEditor;
  constructor(readonly payload: {
    id: string;
    editor: IEditorMetadata;
  }) {
    super();
  }
}
export class OpenEmailTemplateEditor extends OpenEditorBase {
  readonly type = CoreActionTypes.OpenEmailTemplateEditor;
  constructor(readonly payload: {
    id: string;
    editor: IEditorMetadata;
  }) {
    super();
  }
}
export class OpenExternalVariablesEditor extends OpenEditorBase {
  readonly type = CoreActionTypes.OpenExternalVariablesEditor;
  constructor(readonly payload: {
    id: string;
    editor: IEditorMetadata;
  }) {
    super();
  }
}
export class OpenConfigurationEditor extends OpenEditorBase {
  readonly type = CoreActionTypes.OpenConfigurationEditor;
  constructor(readonly payload: {
    id: string;
    editor: IEditorMetadata;
  }) {
    super();
  }
}


export type CoreActions =
  LoadAdministrationWidget
  | OpenWebContentEditor
  | OpenEmailTemplateEditor
  | OpenExternalVariablesEditor
  | LoadAdministrationWidgetSuccess
  | LoadAdministrationWidgetFailure
  | LoadUsers
  | LoadUsersSuccess
  | LoadUsersFailure;
