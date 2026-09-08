import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {SelectItem} from 'primeng/api/selectitem';

@Component({
  selector: 'app-create-rt-dialog',
  templateUrl: './create-rt-dialog.component.html',
  styleUrls: ['./create-rt-dialog.component.scss']
})
export class CreateRtDialogComponent implements OnInit {

  configurations: SelectItem[];
  configurationId: string;
  reportTemplateName: string;

  constructor(
    public dialogRef: MatDialogRef<CreateRtDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.configurations = data.configurations.map((conf) => {
      return {
        value: conf.id,
        label: conf,
      };
    });
  }

  valid() {
    return this.configurationId && this.reportTemplateName && this.reportTemplateName.length > 0;
  }

  create() {
    this.dialogRef.close({
      configurationId: this.configurationId,
      name: this.reportTemplateName,
    });
  }


  cancel() {
    this.dialogRef.close();
  }

  ngOnInit(): void {
  }

}
