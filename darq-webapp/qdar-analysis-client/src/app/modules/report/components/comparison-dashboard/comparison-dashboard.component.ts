import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Store} from '@ngrx/store';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {
  selectAllCvx,
  selectAllDetections,
  selectPatientTables,
  selectVaccinationTables,
} from '../../../shared/store/core.selectors';
import {Labelizer, ValuesService} from '../../../shared/services/values.service';
import {selectReportingGroups} from '../../store/core.selectors';
import {IReportDescriptor} from '../../model/report.model';
import {IComparisonSummary, IReportComparisonResult} from '../../model/comparison.model';
import {ReportComparisonService} from '../../services/report-comparison.service';
import {ReportService} from '../../services/report.service';

export interface IComparisonDashboardData {
  comparison: IReportComparisonResult;
  baseReport: IReportDescriptor;
  newReport: IReportDescriptor;
}

@Component({
  selector: 'app-comparison-dashboard',
  templateUrl: './comparison-dashboard.component.html',
  styleUrls: ['./comparison-dashboard.component.scss']
})
export class ComparisonDashboardComponent implements OnInit {

  comparison: IReportComparisonResult;
  baseReport: IReportDescriptor;
  newReport: IReportDescriptor;
  labelizer$: Observable<Labelizer>;
  summary$: Observable<IComparisonSummary>;
  changesOnly = false;

  constructor(
    public dialogRef: MatDialogRef<ComparisonDashboardComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IComparisonDashboardData,
    private store: Store<any>,
    private valueService: ValuesService,
    private comparisonService: ReportComparisonService,
    private reportService: ReportService,
  ) {
    this.comparison = data.comparison;
    this.baseReport = data.baseReport;
    this.newReport = data.newReport;

    // Built from the base report's configuration, the same way the analysis report
    // display resolves detection / CVX / age group codes into readable labels.
    this.labelizer$ = combineLatest([
      this.store.select(selectAllDetections),
      this.store.select(selectAllCvx),
      this.store.select(selectPatientTables),
      this.store.select(selectVaccinationTables),
      this.store.select(selectReportingGroups),
    ]).pipe(
      map(([detections, cvxCodes, patientTables, vaccinationTables, reportingGroups]) => {
        const options = this.valueService.getFieldOptionsUsingConfiguration({
          detections,
          reportingGroups: reportingGroups || {},
          cvxs: cvxCodes,
          tables: {
            vaccinationTables,
            patientTables,
          }
        }, this.baseReport.configuration, this.baseReport.customDetectionLabels);
        return this.valueService.getQueryValuesLabel(options);
      }),
    );

    this.summary$ = this.labelizer$.pipe(
      map((labelizer) => this.comparisonService.deepSummarize(this.comparison.sectionDifferentials, labelizer)),
    );
  }

  get baseLabel(): string {
    return this.baseReport && this.baseReport.name ? this.baseReport.name : 'Base';
  }

  get newLabel(): string {
    return this.newReport && this.newReport.name ? this.newReport.name : 'New';
  }

  get hasSections(): boolean {
    return !!this.comparison
      && !!this.comparison.sectionDifferentials
      && this.comparison.sectionDifferentials.length > 0;
  }

  toggleChangesOnly() {
    this.changesOnly = !this.changesOnly;
  }

  exportCsv() {
    this.reportService.exportComparisonCsv(
      this.comparison.baseReportId,
      this.comparison.newReportId,
      this.baseLabel,
      this.newLabel,
    ).subscribe((blob: Blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'differential_report.csv';
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }

  ngOnInit(): void {
  }

}
