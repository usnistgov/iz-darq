import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IADFDescriptor } from '../../model/adf.model';
import { SelectItem } from 'primeng/api/selectitem';
import { BehaviorSubject } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-adf-edit-dialog',
  templateUrl: './adf-edit-dialog.component.html',
  styleUrls: ['./adf-edit-dialog.component.scss']
})
export class AdfEditDialogComponent implements OnInit {
  adf: IADFDescriptor;
  formGroup: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<AdfEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.adf = data.adf;
    this.formGroup = new FormGroup({
      name: new FormControl(this.adf.name, Validators.required),
      tags: new FormControl(this.adf.tags || []),
    });
  }

  done() {
    this.dialogRef.close(this.formGroup.getRawValue());
  }

  ngOnInit(): void {
  }

}
