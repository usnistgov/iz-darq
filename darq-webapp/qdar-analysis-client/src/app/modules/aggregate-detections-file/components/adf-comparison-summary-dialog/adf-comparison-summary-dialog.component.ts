import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {IADFMetadata} from '../../model/adf.model';

export interface IAdfComparisonSummaryDialogData {
  baseMeta: IADFMetadata;
  newMeta: IADFMetadata;
}

@Component({
  selector: 'app-adf-comparison-summary-dialog',
  templateUrl: './adf-comparison-summary-dialog.component.html',
  styleUrls: ['./adf-comparison-summary-dialog.component.scss']
})
export class AdfComparisonSummaryDialogComponent implements OnInit {

  baseMeta: IADFMetadata;
  newMeta: IADFMetadata;

  constructor(
    public dialogRef: MatDialogRef<AdfComparisonSummaryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IAdfComparisonSummaryDialogData,
  ) {
    this.baseMeta = data.baseMeta;
    this.newMeta = data.newMeta;
  }

  ngOnInit(): void {
  }

}
