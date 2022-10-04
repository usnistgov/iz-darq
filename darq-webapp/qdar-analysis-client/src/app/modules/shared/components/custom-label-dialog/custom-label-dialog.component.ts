import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataTableComponent } from '../data-table/data-table.component';
import { SelectItem } from 'primeng/api/selectitem';

@Component({
  selector: 'app-custom-label-dialog',
  templateUrl: './custom-label-dialog.component.html',
  styleUrls: ['./custom-label-dialog.component.scss']
})
export class CustomLabelDialogComponent implements OnInit {
  type: string;
  target: string;
  custom: string;
  default: string;
  options: SelectItem[];
  editMode: boolean;
  constructor(
    public dialogRef: MatDialogRef<DataTableComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.type = data.type;
    this.options = data.options;
    this.custom = data.custom;
    this.target = data.target;
    this.default = data.default;
    this.editMode = data.editMode;
  }

  selected(e: string) {
    this.custom = this.getLabel(e);
  }

  getLabel(v: string): string {
    return this.options.find((x) => x.value === v).label;
  }

  done() {
    this.dialogRef.close({
      target: this.target,
      custom: this.custom,
      valid: true,
      default: this.editMode ? this.default : this.getLabel(this.target),
    });
  }

  ngOnInit(): void {
  }

}
