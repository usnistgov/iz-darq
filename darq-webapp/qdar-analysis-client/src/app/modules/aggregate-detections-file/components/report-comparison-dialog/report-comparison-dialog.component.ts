import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ReportService } from '../../../report/services/report.service';

@Component({
  selector: 'app-report-comparison-dialog',
  templateUrl: './report-comparison-dialog.component.html',
  styleUrls: ['./report-comparison-dialog.component.scss']
})
export class ReportComparisonDialogComponent implements OnInit {

  baseReport: any;
  reports: any[];
  compatibleReports: any[];
  selectedReportId: string;

  constructor(
    public dialogRef: MatDialogRef<ReportComparisonDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private reportService: ReportService
  ) {
    this.baseReport = data.baseReport;
    this.reports = data.reports || [];
    console.log(this.reports)
    this.compatibleReports = this.reports.filter(r =>
      r.id !== this.baseReport.id &&
      r.template && this.baseReport.template &&
      r.template.id === this.baseReport.template.id
    );
  }

  ngOnInit(): void {
  }

  submit() {
    if (this.selectedReportId) {
      this.reportService.compareByIds(this.baseReport.id, this.selectedReportId).subscribe(
        () => {
          this.dialogRef.close(true);
        },
        (error) => {
          console.error('Comparison failed', error);
          this.dialogRef.close(true); // Close anyway or handle error
        }
      );
    }
  }

}
