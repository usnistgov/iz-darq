import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IConfigurationDescriptor, IDigestConfiguration } from '../model/configuration.model';
import { Message } from 'ngx-dam-framework';
import { EntityType } from '../../shared/model/entity.model';

@Injectable({
  providedIn: 'root'
})
export class ConfigurationService {

  readonly URL_PREFIX = 'api/configuration/';

  constructor(private http: HttpClient) { }

  getEmptyConfiguration(): IDigestConfiguration {
    return {
      id: undefined,
      name: '',
      owner: undefined,
      ownerDisplayName: undefined,
      lastUpdated: undefined,
      locked: false,
      published: false,
      public: false,
      payload: {
        ageGroups: [],
        detections: [],
      },
      description: '',
      type: EntityType.CONFIGURATION,
    };
  }

  getConfigurations(): Observable<IConfigurationDescriptor[]> {
    return this.http.get<IConfigurationDescriptor[]>(this.URL_PREFIX);
  }

  getById(id: string): Observable<IDigestConfiguration> {
    return this.http.get<IDigestConfiguration>(this.URL_PREFIX + id);
  }

  save(configuration: IDigestConfiguration): Observable<Message<IDigestConfiguration>> {
    return this.http.post<Message<IDigestConfiguration>>(this.URL_PREFIX, configuration);
  }

  delete(id: string): Observable<Message<IDigestConfiguration>> {
    return this.http.delete<Message<IDigestConfiguration>>(this.URL_PREFIX + id);
  }

  lock = (id: string): Observable<Message<IDigestConfiguration>> => {
    return this.http.post<Message<IDigestConfiguration>>(this.URL_PREFIX + id + '/lock/true', {});
  }

  unlock = (id: string): Observable<Message<IDigestConfiguration>> => {
    return this.http.post<Message<IDigestConfiguration>>(this.URL_PREFIX + id + '/lock/false', {});
  }

  publish = (id: string): Observable<Message<IDigestConfiguration>> => {
    return this.http.post<Message<IDigestConfiguration>>(this.URL_PREFIX + id + '/publish', {});
  }

  clone(id: string): Observable<Message<IDigestConfiguration>> {
    return this.http.post<Message<IDigestConfiguration>>(this.URL_PREFIX + id + '/clone', {});
  }

  getDescriptorById(id: string): Observable<IConfigurationDescriptor> {
    return this.http.get<IDigestConfiguration>(this.URL_PREFIX + id + '/descriptor');
  }

}
