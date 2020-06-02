import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IUserResource, IUserAccountRegister } from '../model/user.model';
import { Message } from 'ngx-dam-framework';


@Injectable({
  providedIn: 'root'
})
export class UserService {

  readonly URL_PREFIX = 'api/account/';
  readonly USER_PREFIX = 'api/user/';

  constructor(private http: HttpClient) { }

  getList(): Observable<IUserResource[]> {
    return this.http.get<IUserResource[]>(this.URL_PREFIX);
  }

  register(account: IUserAccountRegister): Observable<Message<IUserResource>> {
    return this.http.post<Message<IUserResource>>(this.USER_PREFIX + 'register', account);
  }

}
