import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, EMPTY, of } from 'rxjs';
import { IUser } from 'src/app/modules/core/model/user.model';
import { selectUsers } from '../../../shared/store/core.selectors';
import {
  RxjsStoreHelperService,
  MessageType,
  ConfirmDialogComponent,
  InsertResourcesInCollection,
  DeleteResourcesFromCollection,
} from 'ngx-dam-framework';
import { UserService } from '../../../core/services/user.service';
import { MatDialog } from '@angular/material/dialog';
import { concatMap } from 'rxjs/operators';
import { UserProfileDialogComponent } from '../user-profile-dialog/user-profile-dialog.component';
import { UserRoleDialogComponent } from '../user-role-dialog/user-role-dialog.component';
import { selectCurrentUserId } from '../../../core/store/core.selectors';

@Component({
  selector: 'app-users-management-editor',
  templateUrl: './users-management-editor.component.html',
  styleUrls: ['./users-management-editor.component.scss']
})
export class UsersManagementEditorComponent implements OnInit {

  users$: Observable<IUser[]>;
  userId$: Observable<string>;

  constructor(
    private store: Store<any>,
    private userService: UserService,
    private dialog: MatDialog,
    private helper: RxjsStoreHelperService) {
    this.users$ = store.select(selectUsers);
    this.userId$ = store.select(selectCurrentUserId);
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
              if (message.status === MessageType.SUCCESS) {
                return of(new InsertResourcesInCollection({
                  key: 'users',
                  values: [message.data],
                }));
              }
              return EMPTY;
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
          question: 'Are you sure you want to delete user account for ' + user.screenName + ' ? All user private resources are going to be deleted (Configurations, Report Template, ADF, Analysis Jobs, Reports) ',
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
              if (message.status === MessageType.SUCCESS) {
                return of(new InsertResourcesInCollection({
                  key: 'users',
                  values: [message.data],
                }));
              }
              return EMPTY;
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
              if (message.status === MessageType.SUCCESS) {
                return of(new InsertResourcesInCollection({
                  key: 'users',
                  values: [message.data],
                }));
              }
              return EMPTY;
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
              if (message.status === MessageType.SUCCESS) {
                return of(new InsertResourcesInCollection({
                  key: 'users',
                  values: [message.data],
                }));
              }
              return EMPTY;
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
