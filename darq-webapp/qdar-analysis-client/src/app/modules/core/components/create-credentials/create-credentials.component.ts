import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { DAM_AUTH_USER_TRANSFORMER, MessageType, RxjsStoreHelperService, UpdateAuthStatus, UserTransformer, IDamUser } from 'ngx-dam-framework';
import { Subscription, of } from 'rxjs';
import { ICurrentUser, ICreateCredentials } from '../../model/user.model';
import { UserService } from '../../services/user.service';
import { selectCurrentUser } from '../../store/core.selectors';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-credentials',
  templateUrl: './create-credentials.component.html',
  styleUrls: ['./create-credentials.component.scss']
})
export class CreateCredentialsComponent implements OnInit, OnDestroy {

  user: ICurrentUser;
  form: FormGroup;
  sub: Subscription;
  constructor(
    private userService: UserService,
    private store: Store<any>,
    private router: Router,
    private helper: RxjsStoreHelperService,
    @Inject(DAM_AUTH_USER_TRANSFORMER) private userTransformer: UserTransformer<any, IDamUser>,
  ) {
    this.sub = this.store.select(selectCurrentUser).pipe(
      tap((user) => {
        this.user = user;
        this.form = new FormGroup({
          fullName: new FormControl(user.name, [Validators.required]),
          username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(10), Validators.pattern('[a-zA-Z0-9_]+')]),
          password: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(20), Validators.pattern('[a-zA-Z0-9@*#!\'&]+')]),
          confirmPassword: new FormControl('',
            [Validators.required, Validators.minLength(8), Validators.maxLength(20), , Validators.pattern('[a-zA-Z0-9@*#!\'&]+')]
          ),
          email: new FormControl(user.email, [Validators.required, Validators.email]),
          organization: new FormControl(user.organization, [Validators.required, Validators.minLength(2), Validators.maxLength(100)]),
          signedConfidentialityAgreement: new FormControl(false, [Validators.required]),
        });
      })
    ).subscribe();
  }

  submit() {
    const data = this.form.getRawValue();
    const creds: ICreateCredentials = {
      id: this.user.id,
      username: data.username,
      password: data.password,
    };

    this.helper.getMessageAndHandle<any>(
      this.store,
      () => {
        return this.userService.createCredentials(creds);
      },
      (message) => {
        if (message.status === MessageType.SUCCESS) {
          this.router.navigate(['/', 'home']);
        }
        return of(
          new UpdateAuthStatus({
            isLoggedIn: true,
            userInfo: this.userTransformer(message.data),
          })
        );
      }
    ).subscribe();
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  ngOnInit(): void {
  }

}
