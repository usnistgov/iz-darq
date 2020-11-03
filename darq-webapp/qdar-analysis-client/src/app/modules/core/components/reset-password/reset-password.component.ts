import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { RxjsStoreHelperService, DAM_AUTH_USER_TRANSFORMER, UserTransformer, IDamUser, MessageType, UpdateAuthStatus } from 'ngx-dam-framework';
import { Observable, of } from 'rxjs';
import { map, take, flatMap } from 'rxjs/operators';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  form: FormGroup;
  token$: Observable<string>;

  constructor(
    private userService: UserService,
    private store: Store<any>,
    private router: Router,
    private activeRoute: ActivatedRoute,
    private helper: RxjsStoreHelperService,
    @Inject(DAM_AUTH_USER_TRANSFORMER) private userTransformer: UserTransformer<any, IDamUser>,
  ) {
    this.form = new FormGroup({
      username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(10), Validators.pattern('[a-zA-Z0-9_]+')]),
      password: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(20), Validators.pattern('[a-zA-Z0-9@*#!\'&]+')]),
      confirmPassword: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(20), , Validators.pattern('[a-zA-Z0-9@*#!\'&]+')]),
    });
    this.token$ = this.activeRoute.queryParams.pipe(
      map((qp) => qp.token)
    );
  }

  submit() {
    const data = this.form.getRawValue();
    this.helper.getMessageAndHandle<any>(
      this.store,
      () => {
        return this.token$.pipe(
          take(1),
          flatMap((token) => {
            return this.userService.passwordReset({
              username: data.username,
              password: data.password,
              token,
            });
          })
        );
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

  ngOnInit(): void {
  }

}
