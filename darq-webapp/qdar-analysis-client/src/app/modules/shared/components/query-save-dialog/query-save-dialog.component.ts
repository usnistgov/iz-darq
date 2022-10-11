import { IQuery } from './../../model/query.model';
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface IQuerySaveDetails {
  replace: boolean;
  name: string;
}

@Component({
  selector: 'app-query-save-dialog',
  templateUrl: './query-save-dialog.component.html',
  styleUrls: ['./query-save-dialog.component.scss']
})
export class QuerySaveDialogComponent implements OnInit {

  query: IQuery;
  replace = false;
  existing = false;
  name: string;
  valid: boolean;

  constructor(
    public dialogRef: MatDialogRef<QuerySaveDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.query = data?.query;
    this.existing = !!this.query;
    this.name = '';
    this.valid = false;
  }

  changeReplace(value: boolean) {
    if (value) {
      this.valid = true;
    } else {
      this.valid = this.name && this.name !== '';
    }
  }

  changeName(value: string) {
    this.valid = value && value !== '';
  }

  ngOnInit(): void {
  }

}
