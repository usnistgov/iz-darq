
import { CoreActions, CoreActionTypes } from './core.actions';

export const coreFeatureKey = 'core';

export interface State {

}

export const initialState: State = {

};

export function reducer(state = initialState, action: CoreActions): State {
  switch (action.type) {

    case CoreActionTypes.LoadCores:
      return state;

    case CoreActionTypes.LoadCoresSuccess:
      return state;

    case CoreActionTypes.LoadCoresFailure:
      return state;

    default:
      return state;
  }
}
