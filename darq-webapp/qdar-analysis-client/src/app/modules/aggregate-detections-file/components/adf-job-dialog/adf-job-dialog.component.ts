import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SelectItem } from 'primeng/api/selectitem';

@Component({
  selector: 'app-adf-job-dialog',
  templateUrl: './adf-job-dialog.component.html',
  styleUrls: ['./adf-job-dialog.component.scss']
})
export class AdfJobDialogComponent implements OnInit {

  templates: SelectItem[];
  templateId: string;
  jobName: string;

  constructor(
    public dialogRef: MatDialogRef<AdfJobDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.templates = data.templates.map((conf) => {
      return {
        value: conf.id,
        label: conf,
      };
    });
  }

  valid() {
    return this.templateId && this.jobName && this.jobName.length > 0;
  }

  create() {
    this.dialogRef.close({
      templateId: this.templateId,
      name: this.jobName,
    });
  }


  cancel() {
    this.dialogRef.close();
  }

  ngOnInit(): void {
  }

}
