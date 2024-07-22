import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, combineLatest, of } from 'rxjs';
import { Message } from 'ngx-dam-framework';
import { IADFDescriptor, IADFMetadata } from '../model/adf.model';
import { IReportTemplateDescriptor } from '../../report-template/model/report-template.model';
import { IUserFacilityDescriptor } from '../../facility/model/facility.model';
import { EntityType } from '../../shared/model/entity.model';
import { PermissionService } from '../../core/services/permission.service';
import { take, map, flatMap } from 'rxjs/operators';
import { Action } from '../../core/model/action.enum';
import { ResourceType } from '../../core/model/resouce-type.enum';
import { Scope } from '../../core/model/scope.enum';

export const PRIVATE_FACILITY_ID = 'private';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  readonly URL_PREFIX = 'api/adf/';

  constructor(
    private http: HttpClient,
    private permission: PermissionService,
  ) { }


  getPrivateFacility(): Observable<IUserFacilityDescriptor> {
    return this.http.get<number>('api/adf/private').pipe(
      map((files) => {
        return {
          id: PRIVATE_FACILITY_ID,
          type: EntityType.FACILITY,
          name: 'Private',
          size: undefined,
          reports: undefined,
          files,
          _private: true,
        };
      })
    );
  }

  getFacilitiesForUser(): Observable<IUserFacilityDescriptor[]> {
    return combineLatest([
      this.http.get<IUserFacilityDescriptor[]>(this.URL_PREFIX + '/facilities'),
      this.permission.abilities$.pipe(
        flatMap((abilities) => {
          return abilities.onScopeCan(Action.UPLOAD, ResourceType.ADF, { scope: Scope.GLOBAL }) ?
            this.getPrivateFacility() :
            of(undefined as IUserFacilityDescriptor);
        }),
      ),
    ]).pipe(
      take(1),
      map(([facilities, priv]) => {
        return [
          ...facilities,
          ...priv ? [priv] : [],
        ];
      }),
    );
  }

  getList(): Observable<IADFDescriptor[]> {
    return this.http.get<IADFDescriptor[]>(this.URL_PREFIX);
  }

  getAll(): Observable<IADFDescriptor[]> {
    return this.http.get<IADFDescriptor[]>(this.URL_PREFIX + 'files');
  }

  mergeFiles(request: { name: string, facilityId: string, ids: string[] }): Observable<Message<IADFDescriptor>> {
    const transformed = {
      ...request,
      facilityId: request.facilityId === PRIVATE_FACILITY_ID ? null : request.facilityId,
    };
    return this.http.post<Message<IADFDescriptor>>(this.URL_PREFIX + 'merge', transformed);
  }

  getListByFacility(id: string): Observable<IADFDescriptor[]> {
    if (id === PRIVATE_FACILITY_ID) {
      return this.getList();
    } else {
      return this.http.get<IADFDescriptor[]>(this.URL_PREFIX + 'facility/' + id);
    }
  }

  updateFile(id: string, fileChange: { name: string, tags: string[] }) {
    return this.http.post<Message<IADFDescriptor>>(this.URL_PREFIX + id, fileChange);
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
