import { Action } from '@ngrx/store';

export enum CoreActionTypes {
  LoadCores = '[Core] Load Cores',


}

export class LoadCores implements Action {
  readonly type = CoreActionTypes.LoadCores;
}


export type CoreActions = LoadCores;
