import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ReportService } from '../../../report/services/report.service';
import {
  ComparisonDashboardComponent,
  IComparisonDashboardData
} from '../../../report/components/comparison-dashboard/comparison-dashboard.component';

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
  loading = false;
  error: string;

  constructor(
    public dialogRef: MatDialogRef<ReportComparisonDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialog: MatDialog,
    private reportService: ReportService
  ) {
    this.baseReport = data.baseReport;
    this.reports = data.reports || [];
    this.compatibleReports = this.reports.filter(r =>
      r.id !== this.baseReport.id &&
      r.template && this.baseReport.template &&
      r.template.id === this.baseReport.template.id
    );
  }

  ngOnInit(): void {
  }

  get selectedReport() {
    return this.compatibleReports.find((report) => report.id === this.selectedReportId);
  }

  viewComparison() {
    if (!this.selectedReportId) {
      return;
    }
    this.loading = true;
    this.error = undefined;
    this.reportService.compareByIds(this.baseReport.id, this.selectedReportId).subscribe({
      next: (comparison) => {
        this.loading = false;
        const dialogData: IComparisonDashboardData = {
          comparison,
          baseReport: this.baseReport,
          newReport: this.selectedReport,
        };
        this.dialog.open(ComparisonDashboardComponent, {
          data: dialogData,
          maxWidth: '95vw',
          panelClass: 'comparison-dashboard-panel',
        });
      },
      error: () => {
        this.loading = false;
        this.error = 'Could not compute the comparison for the selected reports.';
      }
    });
  }

  submit() {
    if (this.selectedReportId) {
      this.reportService.exportComparisonCsv(this.baseReport.id, this.selectedReportId).subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'differential_report.csv';
          a.click();
          window.URL.revokeObjectURL(url);
        },
        error: () => {
          this.error = 'Failed to download the comparison CSV.';
        }
      });
    }
  }

}
