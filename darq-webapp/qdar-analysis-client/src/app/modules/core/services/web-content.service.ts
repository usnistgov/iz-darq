import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface IHomePage {
  title: string;
  sections: {
    title: string;
    content: string;
  }[];
}

@Injectable({
  providedIn: 'root'
})
export class WebContentService {

  readonly URL_PREFIX = 'public/';

  constructor(private http: HttpClient) { }

  getHomePage(): Observable<IHomePage> {
    return this.http.get<IHomePage>(this.URL_PREFIX + 'home');
  }

  getRegisterTermsAndConditions(): Observable<string> {
    return this.http.get<string>(this.URL_PREFIX + 'registerTermsAndConditions');
  }

  getUploadTermsAndConditions(): Observable<string> {
    return this.http.get<string>(this.URL_PREFIX + 'uploadTermsAndConditions');
  }

}
