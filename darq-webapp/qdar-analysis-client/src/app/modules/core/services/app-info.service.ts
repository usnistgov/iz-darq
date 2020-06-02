import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface IServerInfo {
  version: string;
  date: string;
}

@Injectable({
  providedIn: 'root'
})
export class ServerInfoService {

  readonly URL_PREFIX = 'public/';

  constructor(private http: HttpClient) { }

  getServerInfo(): Observable<IServerInfo> {
    return this.http.get<IServerInfo>(this.URL_PREFIX + 'serverInfo');
  }

}
