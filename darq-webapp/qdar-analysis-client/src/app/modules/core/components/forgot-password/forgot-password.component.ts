import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { RxjsStoreHelperService, DAM_AUTH_USER_TRANSFORMER, UserTransformer, IDamUser } from '@usnistgov/ngx-dam-framework-legacy';
import { EMPTY } from 'rxjs';
import { UserService } from '../../services/user.service';


@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {

  form: FormGroup;

  constructor(
    private userService: UserService,
    private store: Store<any>,
    private helper: RxjsStoreHelperService,
  ) {
    this.form = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
    });
  }

  submit() {
    const data = this.form.getRawValue();
    this.helper.getMessageAndHandle<any>(
      this.store,
      () => {
        return this.userService.passwordResetLinkRequest(data.email);
      },
      (message) => {
        return EMPTY;
      }
    ).subscribe();
  }

  ngOnInit(): void {
  }

}
