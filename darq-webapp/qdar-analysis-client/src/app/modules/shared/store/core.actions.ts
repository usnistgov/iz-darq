import { Action } from '@ngrx/store';
import { EntityType } from '../model/entity.model';

export enum CoreActionTypes {
  GoToEntity = '[Main] Go To Entity',
}

export class GoToEntity implements Action {
  readonly type = CoreActionTypes.GoToEntity;
  constructor(readonly payload: {
    type: EntityType,
    id?: string,
  }) { }
}


export type CoreActions = GoToEntity;
