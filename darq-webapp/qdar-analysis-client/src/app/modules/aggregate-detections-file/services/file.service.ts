import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from 'ngx-dam-framework';
import { IADFDescriptor, IADFMetadata } from '../model/adf.model';
import { IReportTemplateDescriptor } from '../../report-template/model/report-template.model';
import { IFacilityDescriptor } from '../../facility/model/facility.model';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  readonly URL_PREFIX = 'api/adf/';

  constructor(private http: HttpClient) { }

  getFacilitiesForUser(): Observable<IFacilityDescriptor[]> {
    return this.http.get<IFacilityDescriptor[]>(this.URL_PREFIX + '/facilities');
  }

  getList(): Observable<IADFDescriptor[]> {
    return this.http.get<IADFDescriptor[]>(this.URL_PREFIX);
  }

  getListByFacility(id: string): Observable<IADFDescriptor[]> {
    return this.http.get<IADFDescriptor[]>(this.URL_PREFIX + 'facility/' + id);
  }

  deleteFile(id: string): Observable<Message<IADFMetadata>> {
    return this.http.delete<Message<IADFMetadata>>(this.URL_PREFIX + '/' + id);
  }

  templatesForFile(id: string): Observable<IReportTemplateDescriptor[]> {
    return this.http.get<IReportTemplateDescriptor[]>('api/template/for/' + id);
  }

  getFileMetadata(id: string): Observable<IADFMetadata> {
    return this.http.get<IADFMetadata>(this.URL_PREFIX + '/' + id);
  }

  upload(form: FormData): Observable<Message<any>> {
    return this.http.post<Message<any>>(this.URL_PREFIX + 'upload', form);
  }

}
