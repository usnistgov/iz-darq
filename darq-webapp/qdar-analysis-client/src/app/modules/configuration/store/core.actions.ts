import { Action } from '@ngrx/store';
import { IConfigurationDescriptor } from '../model/configuration.model';
import { OpenEditorBase, IEditorMetadata } from 'ngx-dam-framework';

export enum CoreActionTypes {
  RouteLoadConfigurationPage = '[qDAR Configuration] Route Load Configuration Page',
  RouteLoadConfigurationPageSuccess = '[qDAR Configuration] Route Load Configuration Page Success',
  RouteLoadConfigurationPageFailure = '[qDAR Configuration] Route Load Configuration Page Failure',
  OpenConfigurationEditor = '[qDAR Configuration] Open Configuration Editor',
}

export class RouteLoadConfigurationPage implements Action {
  readonly type = CoreActionTypes.RouteLoadConfigurationPage;
}

export class RouteLoadConfigurationPageSuccess implements Action {
  readonly type = CoreActionTypes.RouteLoadConfigurationPageSuccess;
  constructor(readonly payload: IConfigurationDescriptor[]) { }
}

export class RouteLoadConfigurationPageFailure implements Action {
  readonly type = CoreActionTypes.RouteLoadConfigurationPageFailure;
  constructor(readonly payload: any) { }
}

export class OpenConfigurationEditor implements OpenEditorBase {
  readonly type = CoreActionTypes.OpenConfigurationEditor;
  constructor(readonly payload: {
    id: string;
    editor: IEditorMetadata;
  }) { }
}


export type CoreActions =
  RouteLoadConfigurationPage
  | RouteLoadConfigurationPageSuccess
  | RouteLoadConfigurationPageFailure
  | OpenConfigurationEditor;
