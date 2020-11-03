import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { IUser } from '../../../core/model/user.model';
import { IProfileUpdate } from '../../../shared/components/user-profile/user-profile.component';

@Component({
  selector: 'app-user-profile-dialog',
  templateUrl: './user-profile-dialog.component.html',
  styleUrls: ['./user-profile-dialog.component.scss']
})
export class UserProfileDialogComponent implements OnInit {

  user: IUser;
  profileUpdate: IProfileUpdate;
  valid: boolean;

  constructor(
    public dialogRef: MatDialogRef<UserProfileDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.user = data.user;
  }

  update(u: any) {
    this.profileUpdate = u.updates;
    this.valid = u.valid;
  }

  ngOnInit(): void {
  }

}
