import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from 'ngx-dam-framework';
import { IFacility, IFacilityDescriptor } from '../model/facility.model';
import { EntityType } from '../../shared/model/entity.model';

export interface IAddMember {
  username: string;
}

@Injectable({
  providedIn: 'root'
})
export class FacilityService {

  readonly URL_PREFIX = 'api/facility/';

  constructor(private http: HttpClient) { }

  getDescriptor(facility: IFacility): IFacilityDescriptor {
    return {
      id: facility.id,
      name: facility.name,
      size: facility.members.length,
      type: EntityType.FACILITY,
    };
  }

  getList(): Observable<IFacilityDescriptor[]> {
    return this.http.get<IFacilityDescriptor[]>(this.URL_PREFIX);
  }

  getById(id: string): Observable<IFacility> {
    return this.http.get<IFacility>(this.URL_PREFIX + id);
  }

  create(descriptor: IFacilityDescriptor): Observable<Message<IFacilityDescriptor>> {
    return this.http.post<Message<IFacilityDescriptor>>(this.URL_PREFIX + 'create', descriptor);
  }

  save(descriptor: IFacility): Observable<Message<IFacility>> {
    return this.http.post<Message<IFacility>>(this.URL_PREFIX, descriptor);
  }

  addMember(id: string, member: IAddMember): Observable<Message<IFacility>> {
    return this.http.post<Message<IFacility>>(this.URL_PREFIX + id + '/add-member', member);
  }

  removeMember(id: string, member: IAddMember): Observable<Message<IFacility>> {
    return this.http.post<Message<IFacility>>(this.URL_PREFIX + id + '/remove-member', member);
  }


}
