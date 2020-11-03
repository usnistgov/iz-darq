import { Component, OnInit, Inject } from '@angular/core';
import { IUser } from '../../../core/model/user.model';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-user-role-dialog',
  templateUrl: './user-role-dialog.component.html',
  styleUrls: ['./user-role-dialog.component.scss']
})
export class UserRoleDialogComponent implements OnInit {

  user: IUser;
  role: string;

  roles = [{
    value: 'BASIC',
    label: 'BASIC',
  }, {
    value: 'ADVANCED',
    label: 'ADVANCED',
  }, {
    value: 'MODERATOR',
    label: 'MODERATOR',
  }, {
    value: 'ADMIN',
    label: 'ADMIN',
  }];

  constructor(
    public dialogRef: MatDialogRef<UserRoleDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.user = data.user;
    this.role = this.user.roles && this.user.roles.length > 0 ? this.user.roles[0] : undefined;
  }


  ngOnInit(): void {
  }

}
