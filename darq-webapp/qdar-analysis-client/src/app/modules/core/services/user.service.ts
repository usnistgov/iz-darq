import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IUserAccountRegister, IUser, ICreateCredentials } from '../model/user.model';
import { Message, UserTransformer, IDamUser } from '@usnistgov/ngx-dam-framework-legacy';
import { IProfileUpdate } from '../../shared/components/user-profile/user-profile.component';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  readonly URL_PREFIX = 'api/account/';
  readonly USER_PREFIX = 'api/user/';

  constructor(private http: HttpClient) { }

  getListOfRegularUsers(): Observable<IUser[]> {
    return this.http.get<IUser[]>(this.URL_PREFIX + 'users');
  }

  getListOfAllUsers(): Observable<IUser[]> {
    return this.http.get<IUser[]>(this.URL_PREFIX + 'all');
  }

  register(account: IUserAccountRegister): Observable<Message<IUser>> {
    return this.http.post<Message<IUser>>(this.USER_PREFIX + 'register', account);
  }

  verifyEmail(account: any): Observable<Message<IUser>> {
    return this.http.post<Message<IUser>>(this.USER_PREFIX + 'verify-email', account);
  }

  lock(id: string): Observable<Message<IUser>> {
    return this.http.post<Message<IUser>>(this.URL_PREFIX + 'lock', { id, lock: true });
  }

  delete(id: string): Observable<Message<IUser>> {
    return this.http.delete<Message<IUser>>(this.URL_PREFIX + 'delete/' + id);
  }

  approve(id: string): Observable<Message<IUser>> {
    return this.http.post<Message<IUser>>(this.URL_PREFIX + 'approve/' + id, {});
  }

  unlock(id: string): Observable<Message<IUser>> {
    return this.http.post<Message<IUser>>(this.URL_PREFIX + 'lock', { id, lock: false });
  }

  update(update: IProfileUpdate): Observable<Message<IUser>> {
    return this.http.post<Message<IUser>>(this.URL_PREFIX + 'update', update);
  }

  updateProfile(update: IProfileUpdate): Observable<Message<IUser>> {
    return this.http.post<Message<IUser>>('api/profile/update', update);
  }

  passwordResetLinkRequest(email: string): Observable<Message<IUser>> {
    return this.http.post<Message<IUser>>(this.USER_PREFIX + 'reset-password-request', { email });
  }

  passwordReset(value: any): Observable<Message<IUser>> {
    return this.http.post<Message<IUser>>(this.USER_PREFIX + 'reset-password', value);
  }

  setUserRole(user: IUser, role: string): Observable<Message<IUser>> {
    return this.http.post<Message<IUser>>(this.URL_PREFIX + 'role', { id: user.id, role });
  }

}

export const USER_TRANSFORM: UserTransformer<IUser, IDamUser> = (user) => {
  return {
    username: user.username,
    roles: user.roles,
    administrator: user.administrator,
    payload: user,
  };
};
