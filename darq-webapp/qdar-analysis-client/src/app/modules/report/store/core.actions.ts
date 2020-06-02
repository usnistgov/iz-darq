import { Action } from '@ngrx/store';
import { IReport } from '../model/report.model';
import { OpenEditorBase, IEditorMetadata } from 'ngx-dam-framework';

export enum CoreActionTypes {
  LoadReport = '[Report] Load Report',
  LoadReportSuccess = '[Report] Load Report Success',
  LoadReportFailure = '[Report] Load Report Failure',

  OpenReportEditor = '[Report] Open Report Editor',
}

export class LoadReport implements Action {
  readonly type = CoreActionTypes.LoadReport;
  constructor(public id: string) { }
}

export class LoadReportSuccess implements Action {
  readonly type = CoreActionTypes.LoadReportSuccess;
  constructor(public payload: IReport) { }
}

export class LoadReportFailure implements Action {
  readonly type = CoreActionTypes.LoadReportFailure;
  constructor(public payload: any) { }
}

export class OpenReportEditor extends OpenEditorBase {
  readonly type = CoreActionTypes.OpenReportEditor;
  constructor(public payload: {
    id: string;
    editor: IEditorMetadata;
  }) {
    super();
  }
}

export type CoreActions = LoadReport | LoadReportSuccess | LoadReportFailure | OpenReportEditor;

