import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {ReportService} from '../../../report/services/report.service';
import {
  CandidateSource,
  ComparisonCandidatesService,
  IComparisonCandidate,
} from '../../../report/services/comparison-candidates.service';
import {
  ComparisonDashboardComponent,
  IComparisonDashboardData
} from '../../../report/components/comparison-dashboard/comparison-dashboard.component';

export interface IReportComparisonDialogData {
  /** The report the comparison starts from. */
  baseReport: IComparisonCandidate;
  /** Candidates already known by the caller (the rows of the list it was opened from). */
  reports?: IComparisonCandidate[];
  /** Facility whose reports and jobs are searched for further candidates. */
  facilityId?: string;
}

@Component({
  selector: 'app-report-comparison-dialog',
  templateUrl: './report-comparison-dialog.component.html',
  styleUrls: ['./report-comparison-dialog.component.scss']
})
export class ReportComparisonDialogComponent implements OnInit {

  sources = CandidateSource;
  baseReport: IComparisonCandidate;
  compatibleReports: IComparisonCandidate[] = [];
  selectedReportId: string;
  loading = false;
  loadingCandidates = false;
  error: string;

  constructor(
    public dialogRef: MatDialogRef<ReportComparisonDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IReportComparisonDialogData,
    private dialog: MatDialog,
    private reportService: ReportService,
    private candidatesService: ComparisonCandidatesService,
  ) {
    this.baseReport = data.baseReport;
    // Show what the caller already has straight away, then widen to the other list.
    this.compatibleReports = this.candidatesService.compatible(this.baseReport, data.reports);
  }

  ngOnInit(): void {
    if (this.data.facilityId) {
      this.loadCandidates(this.data.facilityId);
    }
  }

  /**
   * Pulls candidates from both the published reports and the analysis jobs, so a
   * report can be compared against one reached from the other list.
   */
  private loadCandidates(facilityId: string) {
    this.loadingCandidates = true;
    this.candidatesService.candidatesForFacility(facilityId).subscribe({
      next: (candidates) => {
        this.loadingCandidates = false;
        this.compatibleReports = this.candidatesService.compatible(
          this.baseReport,
          this.candidatesService.merge(this.data.reports, candidates),
        );
      },
      error: () => {
        this.loadingCandidates = false;
        // The caller's own rows are already listed, so this is not fatal.
        this.error = 'Could not load every comparable report, showing the ones already available.';
      }
    });
  }

  get selectedReport(): IComparisonCandidate {
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
          baseReport: this.baseReport as any,
          newReport: this.selectedReport as any,
        };
        this.dialog.open(ComparisonDashboardComponent, {
          data: dialogData,
          maxWidth: '98vw',
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
          a.download = 'differential_analysis_report.csv';
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
