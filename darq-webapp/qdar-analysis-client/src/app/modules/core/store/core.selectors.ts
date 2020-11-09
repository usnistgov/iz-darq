import { createSelector } from '@ngrx/store';
import { selectUserInfo, selectIsLoggedIn } from 'ngx-dam-framework';
import { ICurrentUser } from '../model/user.model';

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

