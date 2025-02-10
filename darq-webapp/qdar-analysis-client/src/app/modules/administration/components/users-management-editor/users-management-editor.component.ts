import { Component, OnInit } from '@angular/core';
import { Action, Store } from '@ngrx/store';
import { Observable, EMPTY, of, BehaviorSubject, combineLatest } from 'rxjs';
import { IUser } from 'src/app/modules/core/model/user.model';
import { selectUsers } from '../../../shared/store/core.selectors';
import {
  RxjsStoreHelperService,
  MessageType,
  ConfirmDialogComponent,
  InsertResourcesInCollection,
  DeleteResourcesFromCollection,
} from '@usnistgov/ngx-dam-framework-legacy';
import { UserService } from '../../../core/services/user.service';
import { MatDialog } from '@angular/material/dialog';
import { concatMap, map } from 'rxjs/operators';
import { UserProfileDialogComponent } from '../user-profile-dialog/user-profile-dialog.component';
import { UserRoleDialogComponent } from '../user-role-dialog/user-role-dialog.component';
import { selectCurrentUserId } from '../../../core/store/core.selectors';
import { FormGroup, FormControl } from '@angular/forms';
import { Message } from '@usnistgov/ngx-dam-framework-legacy';

export interface IUserFilter {
  name: string;
  username: string;
  email: string;
  org: string;
  source: string;
  status: string;
  role: string;
}

@Component({
  selector: 'app-users-management-editor',
  templateUrl: './users-management-editor.component.html',
  styleUrls: ['./users-management-editor.component.scss']
})
export class UsersManagementEditorComponent implements OnInit {

  users$: Observable<IUser[]>;
  userId$: Observable<string>;
  filter$: BehaviorSubject<IUserFilter>;

  sourceOptions = [{
    value: 'AART',
    label: 'AART',
  }, {
    value: 'qDAR',
    label: 'qDAR'
  }];
  statusOptions = [{
    value: 'PENDING',
    label: 'PENDING',
  }, {
    value: 'LOCKED',
    label: 'LOCKED'
  }, {
    value: 'ACTIVE',
    label: 'ACTIVE'
  }];
  roleOptions = [{
    value: 'BASIC',
    label: 'BASIC',
  }, {
    value: 'ADVANCED',
    label: 'ADVANCED',
  }, {
    value: 'MODERATOR',
    label: 'MODERATOR',
  }, {
    value: 'ADMIN',
    label: 'ADMIN',
  }];
  filterFormGroup: FormGroup;

  constructor(
    private store: Store<any>,
    private userService: UserService,
    private dialog: MatDialog,
    private helper: RxjsStoreHelperService) {
    this.filterFormGroup = new FormGroup({
      name: new FormControl('', []),
      username: new FormControl('', []),
      email: new FormControl('', []),
      org: new FormControl('', []),
      source: new FormControl('', []),
      status: new FormControl('', []),
      role: new FormControl('', []),
    });
    this.filter$ = new BehaviorSubject({
      name: '',
      username: '',
      email: '',
      org: '',
      source: '',
      status: '',
      role: '',
    });
    this.filterFormGroup.valueChanges.subscribe(
      (value) => {
        this.filter$.next(value as IUserFilter);
      }
    );
    this.users$ = combineLatest([
      store.select(selectUsers),
      this.filter$,
    ]).pipe(
      map(([users, filter]) => {
        return this.filterList(users, filter);
      }),
    );

    this.userId$ = store.select(selectCurrentUserId);
  }

  // tslint:disable-next-line: cognitive-complexity
  filterList(users: IUser[], filter: IUserFilter) {
    return users.filter((user) => {
      return (!filter.name || user.name && user.name.includes(filter.name)) &&
        (!filter.source || (filter.source === 'qDAR' && !user.source) || (filter.source === user.source)) &&
        (!filter.username || user.username && user.username.includes(filter.username)) &&
        (!filter.email || user.email && user.email.includes(filter.email)) &&
        (!filter.status || user.status === filter.status) &&
        (!filter.role || filter.role === user.roles[0]) &&
        (!filter.org || user.organization && filter.org.includes(user.organization));
    });
  }

  clear() {
    this.filterFormGroup.patchValue({
      name: '',
      username: '',
      email: '',
      org: '',
      source: '',
      status: '',
      role: '',
    });
  }

  setLock(user: IUser, lock: boolean) {
    this.dialog.open(
      ConfirmDialogComponent,
      {
        data: {
          action: 'Lock Account',
          question: 'Are you sure you want to lock user account for ' + user.screenName + ' ? ',
        }
      }
    ).afterClosed().pipe(
      concatMap((answer) => {
        if (answer) {
          return this.helper.getMessageAndHandle(
            this.store,
            () => {
              return lock ? this.userService.lock(user.id) : this.userService.unlock(user.id);
            },
            (message) => {
              return this.insertIfSuccess(message);
            }
          );
        }
      }),
    ).subscribe();
  }

  delete(user: IUser) {
    this.dialog.open(
      ConfirmDialogComponent,
      {
        data: {
          action: 'Delete Account',
          question: 'Are you sure you want to delete user account for ' + user.screenName + ' ? All user private resources are going to be deleted (Configurations, Report Template, ADF, Jobs, Reports) ',
        }
      }
    ).afterClosed().pipe(
      concatMap((answer) => {
        if (answer) {
          return this.helper.getMessageAndHandle(
            this.store,
            () => {
              return this.userService.delete(user.id);
            },
            (message) => {
              if (message.status === MessageType.SUCCESS) {
                return of(new DeleteResourcesFromCollection({
                  key: 'users',
                  values: [user.id],
                }));
              }
              return EMPTY;
            }
          );
        }
      }),
    ).subscribe();
  }

  insertIfSuccess(message: Message<IUser>): Observable<Action> {
    if (message.status === MessageType.SUCCESS) {
      return of(new InsertResourcesInCollection({
        key: 'users',
        values: [message.data],
      }));
    }
    return EMPTY;
  }

  approve(user: IUser) {
    this.dialog.open(
      ConfirmDialogComponent,
      {
        data: {
          action: 'Approve Account',
          question: 'Are you sure you want to approve user account for ' + user.screenName + ' ? ',
        }
      }
    ).afterClosed().pipe(
      concatMap((answer) => {
        if (answer) {
          return this.helper.getMessageAndHandle(
            this.store,
            () => {
              return this.userService.approve(user.id);
            },
            (message) => {
              return this.insertIfSuccess(message);
            }
          );
        }
      }),
    ).subscribe();
  }

  edit(user: IUser) {
    this.dialog.open(
      UserProfileDialogComponent,
      {
        data: {
          user,
        }
      }
    ).afterClosed().pipe(
      concatMap((update) => {
        if (update) {
          return this.helper.getMessageAndHandle(
            this.store,
            () => {
              return this.userService.update(update);
            },
            (message) => {
              return this.insertIfSuccess(message);
            }
          );
        }
      }),
    ).subscribe();
  }

  editRole(user: IUser) {
    this.dialog.open(
      UserRoleDialogComponent,
      {
        data: {
          user,
        }
      }
    ).afterClosed().pipe(
      concatMap((role) => {
        if (role) {
          return this.helper.getMessageAndHandle(
            this.store,
            () => {
              return this.userService.setUserRole(user, role);
            },
            (message) => {
              return this.insertIfSuccess(message);
            }
          );
        }
      }),
    ).subscribe();
  }

  ngOnInit(): void {
    (document.getElementsByClassName('container-content').item(0) as HTMLElement).scrollTop = 0;
  }

}
