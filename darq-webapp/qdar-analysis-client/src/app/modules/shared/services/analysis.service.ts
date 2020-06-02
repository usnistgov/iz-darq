import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IDataViewQuery } from '../../report-template/model/report-template.model';
import { Observable } from 'rxjs';
import { IDataTable, IAnalysisJobRequest, IAnalysisJob } from '../../report/model/report.model';
import { Message } from 'ngx-dam-framework';

@Injectable({
  providedIn: 'root'
})
export class AnalysisService {

  readonly PUBLIC = 'api/analysis/';

  constructor(private http: HttpClient) { }

  executeQuery(target: string, query: IDataViewQuery): Observable<IDataTable> {
    return this.http.post<IDataTable>(this.PUBLIC + 'query/' + target, query);
  }

  submitJob(jobRequest: IAnalysisJobRequest): Observable<Message<IAnalysisJob>> {
    return this.http.post<Message<IAnalysisJob>>(this.PUBLIC + 'job/', jobRequest);
  }

  getJobs(): Observable<IAnalysisJob[]> {
    return this.http.get<IAnalysisJob[]>(this.PUBLIC + 'jobs');
  }

  getJobsByFacility(facility: string): Observable<IAnalysisJob[]> {
    return this.http.get<IAnalysisJob[]>(this.PUBLIC + 'jobs' + '/' + facility);
  }

  removeJob(id: string): Observable<Message<IAnalysisJob>> {
    return this.http.delete<Message<IAnalysisJob>>(this.PUBLIC + 'job/' + id);
  }

}
