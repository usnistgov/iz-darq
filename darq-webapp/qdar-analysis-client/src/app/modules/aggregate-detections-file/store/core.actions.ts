import { Action } from '@ngrx/store';
import { IADFDescriptor, IADFMetadata } from '../model/adf.model';
import { IFacilityDescriptor } from '../../facility/model/facility.model';

export enum CoreActionTypes {
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
  LoadADFiles
  | LoadADFilesSuccess
  | LoadADFilesFailure
  | LoadADFile
  | LoadADFileSuccess
  | LoadADFileFailure
  | LoadUserFacilities
  | LoadUserFacilitiesSuccess
  | LoadUserFacilitiesFailure;

