import { Component, Input, OnInit, Output, EventEmitter, OnDestroy } from '@angular/core';
import { IUser } from '../../../core/model/user.model';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';

export interface IProfileUpdate {
  id: string;
  email?: string;
  fullName: string;
  organization: string;
  password?: string;
}

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit, OnDestroy {

  @Input()
  set user(u: IUser) {
    this.profileForm.patchValue({
      id: u.id,
      email: u.email,
      fullName: u.name,
      organization: u.organization
    });
    this.passwordForm.patchValue({
      password: '',
      confirmPassword: '',
    });
    this.account = u;
  }
  account: IUser;
  profileForm: FormGroup;
  passwordForm: FormGroup;
  password = false;
  @Input()
  admin: boolean;
  @Output()
  profileUpdate: EventEmitter<{ updates: IProfileUpdate, valid: boolean }>;

  profilesubs: Subscription;
  passwordsubs: Subscription;

  constructor() {
    this.profileUpdate = new EventEmitter();
    this.profileForm = new FormGroup({
      id: new FormControl('', [Validators.required]),
      fullName: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      organization: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]),
    });
    this.passwordForm = new FormGroup({
      password: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(20),
        Validators.pattern('[a-zA-Z0-9@*#!\'&]+'),
      ]),
      confirmPassword: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(20),
        Validators.pattern('[a-zA-Z0-9@*#!\'&]+'),
      ]),
    });

    this.profilesubs = this.profileForm.valueChanges.subscribe((change) => {
      this.profileUpdate.emit({ updates: { ...change, ...this.passwordForm.getRawValue() }, valid: this.isValid() });
    });
    this.passwordsubs = this.passwordForm.valueChanges.subscribe((change) => {
      this.profileUpdate.emit({ updates: { ...change, ...this.profileForm.getRawValue() }, valid: this.isValid() });
    });
  }

  checkPassword() {
    this.passwordForm.patchValue({
      password: '',
      confirmPassword: '',
    });
  }

  isValid() {
    return this.profileForm.valid && (!this.password || this.passwordForm.valid);
  }

  ngOnDestroy() {
    if (this.profilesubs) {
      this.profilesubs.unsubscribe();
    }
    if (this.passwordsubs) {
      this.passwordsubs.unsubscribe();
    }
  }

  ngOnInit(): void {
  }

}
