import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {IADFDescriptor} from '../../model/adf.model';
import {IAdfComparisonCandidate} from '../../model/adf-comparison.model';
import {AdfComparisonService} from '../../services/adf-comparison.service';

export interface IAdfComparisonDialogData {
  baseFile: IADFDescriptor;
  files: IADFDescriptor[];
}

@Component({
  selector: 'app-adf-comparison-dialog',
  templateUrl: './adf-comparison-dialog.component.html',
  styleUrls: ['./adf-comparison-dialog.component.scss']
})
export class AdfComparisonDialogComponent implements OnInit {

  baseFile: IADFDescriptor;
  candidates: IAdfComparisonCandidate[];
  selectedFileId: string;

  constructor(
    public dialogRef: MatDialogRef<AdfComparisonDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IAdfComparisonDialogData,
    private comparisonService: AdfComparisonService,
  ) {
    this.baseFile = data.baseFile;
    this.candidates = this.comparisonService.candidatesFor(this.baseFile, data.files);
  }

  get selectedCandidate(): IAdfComparisonCandidate {
    return this.candidates.find((candidate) => candidate.file.id === this.selectedFileId);
  }

  /**
   * Warn when the selected ADF comes from a different tool version: the
   * comparison still runs, but the statistics may not line up.
   */
  get selectionIsIncompatible(): boolean {
    const selected = this.selectedCandidate;
    return !!selected && !selected.compatible;
  }

  compare() {
    if (this.selectedFileId) {
      this.dialogRef.close(this.selectedFileId);
    }
  }

  ngOnInit(): void {
  }

}
