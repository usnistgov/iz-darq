import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { IUserAccountRegister } from '../../model/user.model';
import { UserService } from '../../services/user.service';
import { RxjsStoreHelperService } from 'ngx-dam-framework';
import { Store } from '@ngrx/store';
import { of, Observable } from 'rxjs';
import { WebContentService } from '../../services/web-content.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {

  form: FormGroup;
  termsAndConditions: string;
  init = false;

  constructor(
    private userService: UserService,
    private store: Store<any>,
    private helper: RxjsStoreHelperService,
    private webContentService: WebContentService,
  ) {
    this.form = new FormGroup({
      fullName: new FormControl('', [Validators.required]),
      username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(10), Validators.pattern('[a-zA-Z0-9_]+')]),
      password: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(20), Validators.pattern('[a-zA-Z0-9@*#!\'&]+')]),
      confirmPassword: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(20), , Validators.pattern('[a-zA-Z0-9@*#!\'&]+')]),
      email: new FormControl('', [Validators.required, Validators.email]),
      organization: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]),
      signedConfidentialityAgreement: new FormControl(false, [Validators.required]),
    });
  }

  submit() {
    const registration: IUserAccountRegister = this.form.getRawValue();
    this.helper.getMessageAndHandle<any>(
      this.store,
      () => {
        return this.userService.register(registration);
      },
      (message) => {
        return of();
      }
    ).subscribe();
  }

  ngOnInit(): void {
    this.webContentService.getRegisterTermsAndConditions().pipe(
      map((value) => {
        this.termsAndConditions = value;
        this.init = true;
      }),
    ).subscribe();
  }

}
