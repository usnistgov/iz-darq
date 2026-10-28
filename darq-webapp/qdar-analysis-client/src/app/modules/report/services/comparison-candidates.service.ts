import {Injectable} from '@angular/core';
import {combineLatest, Observable, of} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {IAnalysisJob, IReportDescriptor} from '../model/report.model';
import {IReportTemplate} from '../../report-template/model/report-template.model';
import {AnalysisService} from '../../shared/services/analysis.service';
import {ReportService} from './report.service';

export enum CandidateSource {
  REPORT = 'Published Report',
  JOB = 'Analysis Job',
}

/**
 * A report that can take part in a comparison, whether it was reached through the
 * published reports list or through the analysis job that produced it.
 */
export interface IComparisonCandidate {
  id: string;
  name: string;
  template: IReportTemplate;
  publishDate: Date;
  source: CandidateSource;
}

@Injectable({
  providedIn: 'root'
})
export class ComparisonCandidatesService {

  constructor(
    private reportService: ReportService,
    private analysisService: AnalysisService,
  ) { }

  fromReport(report: IReportDescriptor): IComparisonCandidate {
    return {
      id: report.id,
      name: report.name,
      template: (report as any).template,
      publishDate: (report as any).publishDate,
      source: CandidateSource.REPORT,
    };
  }

  fromJob(job: IAnalysisJob): IComparisonCandidate {
    return {
      id: job.reportId,
      name: job.name,
      template: job.template,
      publishDate: job.endTime,
      source: CandidateSource.JOB,
    };
  }

  /**
   * Every report of the facility that can be compared, drawn from both the published
   * reports and the completed analysis jobs. A job whose report is already published
   * is dropped in favour of the published entry, so the same report is never offered
   * twice.
   */
  candidatesForFacility(facilityId: string): Observable<IComparisonCandidate[]> {
    const reports$ = (facilityId === 'private' || facilityId === 'local')
      ? this.reportService.published()
      : this.reportService.publishedForFacility(facilityId);

    return combineLatest([
      reports$.pipe(catchError(() => of([] as IReportDescriptor[]))),
      this.analysisService.getJobsByFacility(facilityId).pipe(catchError(() => of([] as IAnalysisJob[]))),
    ]).pipe(
      map(([reports, jobs]) => this.merge(
        (reports || []).map((report) => this.fromReport(report)),
        (jobs || []).filter((job) => !!job.reportId).map((job) => this.fromJob(job)),
      )),
    );
  }

  /**
   * Merges candidate lists, keeping the first entry seen for a given report id and
   * dropping anything without a resolvable template (it could never be compatible).
   */
  merge(...lists: IComparisonCandidate[][]): IComparisonCandidate[] {
    const byId = new Map<string, IComparisonCandidate>();
    lists.forEach((list) => (list || []).forEach((candidate) => {
      if (candidate.id && candidate.template && !byId.has(candidate.id)) {
        byId.set(candidate.id, candidate);
      }
    }));
    return Array.from(byId.values());
  }

  /**
   * Two reports are comparable when they were produced from the same report template
   * and are not the same report.
   */
  comparable(base: IComparisonCandidate, candidate: IComparisonCandidate): boolean {
    return !!base && !!candidate &&
      candidate.id !== base.id &&
      !!candidate.template && !!base.template &&
      candidate.template.id === base.template.id;
  }

  compatible(base: IComparisonCandidate, candidates: IComparisonCandidate[]): IComparisonCandidate[] {
    return (candidates || [])
      .filter((candidate) => this.comparable(base, candidate))
      .sort((a, b) => new Date(b.publishDate).getTime() - new Date(a.publishDate).getTime());
  }

}
