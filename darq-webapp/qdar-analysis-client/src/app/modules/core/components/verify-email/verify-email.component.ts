import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { RxjsStoreHelperService, MessageService } from 'ngx-dam-framework';
import { of, Observable } from 'rxjs';
import { UserService } from '../../services/user.service';
import { map, flatMap, take } from 'rxjs/operators';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.scss']
})
export class VerifyEmailComponent implements OnInit {

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
            return this.userService.verifyEmail({
              username: data.username,
              password: data.password,
              token,
            });
          })
        );
      },
      (message) => {
        return of();
      }
    ).subscribe();
  }

  ngOnInit(): void {
  }

}
