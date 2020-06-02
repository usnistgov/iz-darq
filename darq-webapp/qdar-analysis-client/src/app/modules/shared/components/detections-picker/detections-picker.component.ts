import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IDetectionResource } from '../../model/public.model';

@Component({
  selector: 'app-detections-picker',
  templateUrl: './detections-picker.component.html',
  styleUrls: ['./detections-picker.component.scss']
})
export class DetectionsPickerComponent implements OnInit {

  detections: IDetectionResource[];
  pickList: IDetectionResource[];
  picked: IDetectionResource[];
  selection: string[];

  constructor(
    public dialogRef: MatDialogRef<DetectionsPickerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.detections = data.detections;
    this.selection = data.selection;

    this.pickList = this.detections.filter((elm) => {
      return !this.selection.find((s) => s === elm.id);
    });
  }

  dismiss() {
    this.dialogRef.close();
  }

  select() {
    this.dialogRef.close(this.picked.map((d) => d.id));
  }

  ngOnInit(): void {
  }

}
