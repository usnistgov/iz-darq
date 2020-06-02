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
      lastUpdated: undefined,
      locked: false,
      owned: true,
      published: false,
      payload: {
        ageGroups: [],
        detections: [],
      },
      description: '',
      type: EntityType.CONFIGURATION,
      viewOnly: undefined,
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

  getDescriptor(configuration: IDigestConfiguration, owned: boolean): IConfigurationDescriptor {
    return {
      id: configuration.id,
      type: EntityType.CONFIGURATION,
      name: configuration.name,
      owner: configuration.owner,
      lastUpdated: configuration.lastUpdated,
      viewOnly: configuration.viewOnly,
      locked: configuration.locked,
      published: configuration.published,
      owned,
    };
  }

}
