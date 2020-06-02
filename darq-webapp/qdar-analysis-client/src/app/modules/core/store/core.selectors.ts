import { createEntityAdapter, Dictionary } from '@ngrx/entity';
import { createSelector } from '@ngrx/store';
import { selectFromCollection, User } from 'ngx-dam-framework';
import { IUserResource } from '../model/user.model';

const UsersAdapter = createEntityAdapter<IUserResource>();
const UsersSelectors = UsersAdapter.getSelectors();
export const selectUsersRepo = selectFromCollection('users');
export const selectUsersEntities = createSelector(
  selectUsersRepo,
  UsersSelectors.selectEntities,
);
export const selectUsers = createSelector(
  selectUsersRepo,
  UsersSelectors.selectAll,
);
export const selectUserById = createSelector(
  selectUsersEntities,
  (dict: Dictionary<IUserResource>, props: any): User => {
    if (dict[props.id]) {
      return dict[props.id];
    } else {
      return undefined;
    }
  }
);
