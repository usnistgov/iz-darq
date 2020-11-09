import { Component, Inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { RxjsStoreHelperService, DAM_AUTH_USER_TRANSFORMER, UserTransformer, IDamUser, MessageType, UpdateAuthStatus } from 'ngx-dam-framework';
import { of, Observable } from 'rxjs';
import { ICurrentUser } from '../../model/user.model';
import { UserService } from '../../services/user.service';
import { selectCurrentUser } from '../../store/core.selectors';
import { IProfileUpdate } from '../../../shared/components/user-profile/user-profile.component';

@Component({
  selector: 'app-update-profile',
  templateUrl: './update-profile.component.html',
  styleUrls: ['./update-profile.component.scss']
})
export class UpdateProfileComponent implements OnInit {

  user$: Observable<ICurrentUser>;
  profileUpdate: IProfileUpdate = undefined;
  valid: boolean;

  constructor(
    private userService: UserService,
    private store: Store<any>,
    private router: Router,
    private helper: RxjsStoreHelperService,
    @Inject(DAM_AUTH_USER_TRANSFORMER) private userTransformer: UserTransformer<any, IDamUser>,
  ) {
    this.user$ = this.store.select(selectCurrentUser);
  }

  update(event) {
    this.profileUpdate = event.updates;
    this.valid = event.valid;
  }

  submit() {
    if (this.profileUpdate) {
      this.helper.getMessageAndHandle<any>(
        this.store,
        () => {
          return this.userService.updateProfile(this.profileUpdate);
        },
        (message) => {
          if (message.status === MessageType.SUCCESS) {
            this.router.navigate(['/', 'home']);
          }
          return of(
            new UpdateAuthStatus({
              statusChecked: true,
              isLoggedIn: true,
              userInfo: this.userTransformer(message.data),
            })
          );
        }
      ).subscribe();
    }
  }

  ngOnInit(): void {
  }

}
