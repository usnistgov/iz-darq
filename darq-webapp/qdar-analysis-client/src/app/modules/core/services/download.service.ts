import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface JarInfo {
  version: string;
  mqeVersion: string;
  buildAt: string;
  keyHash: string;
  description: string;
}

export interface FileDescriptor {
  id: string;
  name: string;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class DownloadService {

  readonly URL_PREFIX = 'public/download';

  constructor(private http: HttpClient) { }

  getFiles(): Observable<FileDescriptor[]> {
    return this.http.get<FileDescriptor[]>(this.URL_PREFIX + '/files');
  }

  getDownloadLink(id: string): string {
    return this.URL_PREFIX + '/file/' + id;
  }

  getCLIInfo(): Observable<JarInfo> {
    return this.http.get<JarInfo>(this.URL_PREFIX + '/cli/info');
  }

  getCLIDownloadLink(): string {
    return this.URL_PREFIX + '/cli';
  }
}
