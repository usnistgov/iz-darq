import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IEmailTemplate } from '../model/email-template.model';
import { IWebContent } from '../model/web-content.model';
import { IToolConfiguration, IToolConfigurationKeyValue } from '../model/tool-config.model';
import { Message } from '@usnistgov/ngx-dam-framework-legacy';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  readonly URL_PREFIX = 'api/admin/';
  readonly EMAIL_URL = this.URL_PREFIX + 'email/';
  readonly WEBCNT_URL = this.URL_PREFIX + 'web-content/';
  readonly CONF_URL = this.URL_PREFIX + 'tool-configuration/';


  constructor(private http: HttpClient) { }

  getEmailTemplates(): Observable<IEmailTemplate[]> {
    return this.http.get<IEmailTemplate[]>(this.EMAIL_URL + 'templates');
  }

  setEmailTemplates(templates: IEmailTemplate[]): Observable<IEmailTemplate[]> {
    return this.http.post<IEmailTemplate[]>(this.EMAIL_URL + 'templates', templates);
  }

  getPreview(body: string, params: Record<string, string>): Observable<IEmailTemplate[]> {
    return this.http.post<IEmailTemplate[]>(this.EMAIL_URL + 'freemarker', { body, params });
  }

  getWebContent(): Observable<IWebContent> {
    return this.http.get<IWebContent>(this.WEBCNT_URL);
  }

  setWebContent(content: IWebContent): Observable<IWebContent> {
    return this.http.post<IWebContent>(this.WEBCNT_URL, content);
  }

  getToolConfiguration(): Observable<IToolConfiguration> {
    return this.http.get<IToolConfiguration>(this.CONF_URL);
  }

  setToolConfKeys(keys: IToolConfigurationKeyValue[]): Observable<Message<Message<string>[]>> {
    return this.http.post<Message<Message<string>[]>>(this.CONF_URL, keys);
  }

  checkStatus(): Observable<Message<string>[]> {
    return this.http.get<Message<string>[]>(this.CONF_URL + '/status');
  }

}
