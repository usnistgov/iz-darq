import { ExternalQueryVariableScope } from './../../../shared/model/query-variable.model';
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-variables-csv-import-dialog',
  templateUrl: './variables-csv-import-dialog.component.html',
  styleUrls: ['./variables-csv-import-dialog.component.scss']
})
export class VariablesCsvImportDialogComponent implements OnInit {

  options = [{
    label: 'GLOBAL Variables',
    value: ExternalQueryVariableScope.GLOBAL,
  }, {
    label: 'IIS Variables',
    value: ExternalQueryVariableScope.IIS,
  }];
  file: File;
  scope: ExternalQueryVariableScope;

  constructor(
    public dialogRef: MatDialogRef<VariablesCsvImportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {

  }

  setFile(file: File) {
    this.file = file;
  }

  import() {
    this.dialogRef.close({ file: this.file, scope: this.scope });
  }

  ngOnInit(): void {
  }

}
