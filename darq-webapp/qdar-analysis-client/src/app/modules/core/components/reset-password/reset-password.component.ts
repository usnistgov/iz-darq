import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { MessageService, RxjsStoreHelperService } from 'ngx-dam-framework';
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
    private activeRoute: ActivatedRoute,
    private helper: RxjsStoreHelperService,
    private message: MessageService,
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
      (_message) => {
        return of();
      }
    ).subscribe();
  }

  ngOnInit(): void {
  }

}
