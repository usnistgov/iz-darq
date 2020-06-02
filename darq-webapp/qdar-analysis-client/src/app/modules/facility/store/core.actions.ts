import { Action } from '@ngrx/store';
import { IFacilityDescriptor, IFacility } from '../model/facility.model';
import { OpenEditorBase, IEditorMetadata } from 'ngx-dam-framework';

export enum CoreActionTypes {
  LoadFacilities = '[Facility] Load Facilities',
  LoadFacilitiesSuccess = '[Facility] Load Facilities Success',
  LoadFacilitiesFailure = '[Facility] Load Facilities Failure',

  LoadFacility = '[Facility] Load Facility',
  LoadFacilitySuccess = '[Facility] Load Facility Success',
  LoadFacilityFailure = '[Facility] Load Facility Failure',

  OpenFacilityEditor = '[Facility Editor] Open Facility Editor',
}

export class LoadFacilities implements Action {
  readonly type = CoreActionTypes.LoadFacilities;
}

export class LoadFacilitiesSuccess implements Action {
  readonly type = CoreActionTypes.LoadFacilitiesSuccess;
  constructor(public payload: IFacilityDescriptor[]) { }
}

export class LoadFacilitiesFailure implements Action {
  readonly type = CoreActionTypes.LoadFacilitiesFailure;
  constructor(public payload: any) { }
}

export class LoadFacility implements Action {
  readonly type = CoreActionTypes.LoadFacility;
  constructor(public id: string) { }
}

export class LoadFacilitySuccess implements Action {
  readonly type = CoreActionTypes.LoadFacilitySuccess;
  constructor(public payload: IFacility) { }
}

export class LoadFacilityFailure implements Action {
  readonly type = CoreActionTypes.LoadFacilityFailure;
  constructor(public payload: any) { }
}

export class OpenFacilityEditor extends OpenEditorBase {
  readonly type = CoreActionTypes.OpenFacilityEditor;
  constructor(public payload: {
    id: string;
    editor: IEditorMetadata;
  }) {
    super();
  }
}

export type CoreActions =
  LoadFacilities
  | LoadFacilitiesSuccess
  | LoadFacilitiesFailure
  | LoadFacility
  | LoadFacilitySuccess
  | LoadFacilityFailure
  | OpenFacilityEditor;

