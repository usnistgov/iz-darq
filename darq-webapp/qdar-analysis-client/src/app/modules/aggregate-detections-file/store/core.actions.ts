import { Action } from '@ngrx/store';
import { IADFDescriptor, IADFMetadata } from '../model/adf.model';
import { IFacilityDescriptor } from '../../facility/model/facility.model';
import { IEditorMetadata, OpenEditorBase } from 'ngx-dam-framework';

export enum CoreActionTypes {
  LoadADFDashboard = '[ADF] Load ADF Dashboard',
  LoadADFDashboardSuccess = '[ADF] Load ADF Dashboard Success',
  LoadADFDashboardFailure = '[ADF] Load ADF Dashboard Failure',


  OpenADFListEditor = '[ADF] Open ADF List Editor',
  OpenAnalysisJobEditor = '[ADF] Open Analysis Job Editor',
  OpenReportsEditor = '[ADF] Open Reports Editor',


  LoadADFiles = '[ADF] Load ADF Files List',
  LoadADFilesSuccess = '[ADF] Load ADF Files List Success',
  LoadADFilesFailure = '[ADF] Load ADF Files List Failure',
  LoadADFile = '[ADF] Load ADF File',
  LoadADFileSuccess = '[ADF] Load ADF File Success',
  LoadADFileFailure = '[ADF] Load ADF File Failure',

  LoadUserFacilities = '[ADF] Load User Facilities',
  LoadUserFacilitiesSuccess = '[ADF] Load User Facilities Success',
  LoadUserFacilitiesFailure = '[ADF] Load User Facilities Failure',
}

export class LoadADFDashboard implements Action {
  readonly type = CoreActionTypes.LoadADFDashboard;
  constructor() { }
}

export class LoadADFDashboardSuccess implements Action {
  readonly type = CoreActionTypes.LoadADFDashboardSuccess;
  constructor() { }
}

export class LoadADFDashboardFailure implements Action {
  readonly type = CoreActionTypes.LoadADFDashboardFailure;
  constructor() { }
}

export class OpenADFListEditor implements OpenEditorBase {
  readonly type = CoreActionTypes.OpenADFListEditor;
  constructor(readonly payload: { id: string; editor: IEditorMetadata; }) { }
}

export class OpenAnalysisJobEditor implements OpenEditorBase {
  readonly type = CoreActionTypes.OpenAnalysisJobEditor;
  constructor(readonly payload: { id: string; editor: IEditorMetadata; }) { }
}

export class OpenReportsEditor implements OpenEditorBase {
  readonly type = CoreActionTypes.OpenReportsEditor;
  constructor(readonly payload: { id: string; editor: IEditorMetadata; }) { }
}

export class LoadADFiles implements Action {
  readonly type = CoreActionTypes.LoadADFiles;
  constructor(public facility: string) { }
}

export class LoadADFilesSuccess implements Action {
  readonly type = CoreActionTypes.LoadADFilesSuccess;
  constructor(public payload: IADFDescriptor[]) { }
}

export class LoadADFilesFailure implements Action {
  readonly type = CoreActionTypes.LoadADFilesFailure;
  constructor(public payload: any) { }
}

export class LoadADFile implements Action {
  readonly type = CoreActionTypes.LoadADFile;
  constructor(public id: string) { }
}

export class LoadADFileSuccess implements Action {
  readonly type = CoreActionTypes.LoadADFileSuccess;
  constructor(public payload: IADFMetadata) { }
}

export class LoadADFileFailure implements Action {
  readonly type = CoreActionTypes.LoadADFileFailure;
  constructor(public payload: any) { }
}

export class LoadUserFacilities implements Action {
  readonly type = CoreActionTypes.LoadUserFacilities;
  constructor() { }
}

export class LoadUserFacilitiesSuccess implements Action {
  readonly type = CoreActionTypes.LoadUserFacilitiesSuccess;
  constructor(public payload: IFacilityDescriptor[]) { }
}

export class LoadUserFacilitiesFailure implements Action {
  readonly type = CoreActionTypes.LoadUserFacilitiesFailure;
  constructor(public payload: any) { }
}

export type CoreActions =
  LoadADFDashboard
  | LoadADFDashboardSuccess
  | LoadADFDashboardFailure
  | OpenADFListEditor
  | OpenAnalysisJobEditor
  | OpenReportsEditor
  | LoadADFiles
  | LoadADFilesSuccess
  | LoadADFilesFailure
  | LoadADFile
  | LoadADFileSuccess
  | LoadADFileFailure
  | LoadUserFacilities
  | LoadUserFacilitiesSuccess
  | LoadUserFacilitiesFailure;

