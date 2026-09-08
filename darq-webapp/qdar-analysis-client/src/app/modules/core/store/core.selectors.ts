import {createSelector} from '@ngrx/store';
import {selectIsLoggedIn, selectUserInfo} from '@usnistgov/ngx-dam-framework-legacy';
import {ICurrentUser} from '../model/user.model';

export const selectCurrentUser = createSelector(
  selectIsLoggedIn,
  selectUserInfo,
  (logged: boolean, user: any): ICurrentUser => {
    return logged ? user.payload as ICurrentUser : undefined;
  }
);

export const selectCurrentUserId = createSelector(
  selectCurrentUser,
  (user: ICurrentUser): string => {
    return user ? user.id : undefined;
  }
);

