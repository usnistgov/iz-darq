import { Injectable, OnDestroy } from '@angular/core';
import { ICurrentUser } from '../model/user.model';
import { Scope } from '../model/scope.enum';
import { Store } from '@ngrx/store';
import { selectCurrentUser } from '../store/core.selectors';
import { map } from 'rxjs/operators';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { Action } from '../model/action.enum';
import { ResourceType } from '../model/resouce-type.enum';
import { AccessToken } from '../model/token.enum';

export interface IPermissionEvaluator {
  onScopeCan(action: Action, type: ResourceType, qscope: { scope: Scope, facilityId?: string }, token?: AccessToken): boolean;
  onResourceCan(action: Action, type: ResourceType, value: any): boolean;
  onScopeCant(action: Action, type: ResourceType, qscope: { scope: Scope, facilityId?: string }, token?: AccessToken): boolean;
  onResourceCant(action: Action, type: ResourceType, value: any): boolean;
}

export class SimplePermissionEvaluator implements IPermissionEvaluator {
  private readonly permissions;
  constructor(private user: ICurrentUser) {
    this.permissions = user.permissions;
  }

  getResourceScope(resource: any): string {
    if (resource && resource.facilityId) {
      return `${Scope.FACILITY}_${resource.facilityId}`;
    } else {
      return Scope.GLOBAL;
    }
  }

  getResourceToken(resource: any): string {
    if (resource && resource.public) {
      return AccessToken.PUBLIC;
    } else if (resource && resource.owner && resource.owner !== '') {
      return `${AccessToken.OWNER}_${resource.owner}`;
    } else {
      return AccessToken.ANY;
    }
  }

  onResourceCan(action: Action, type: ResourceType, value: any): boolean {
    const scopeToken = this.getResourceScope(value);
    const token = this.getResourceToken(value);
    const perms = this.permissions?.authorize?.[scopeToken]?.[type]?.[token];
    const anyToken = this.permissions?.authorize?.[scopeToken]?.[type]?.[AccessToken.ANY];
    return [
      ...perms ? perms : [],
      ...anyToken ? anyToken : [],
    ].includes(action);
  }

  onScopeCan(action: Action, type: ResourceType, qscope: { scope: Scope, facilityId?: string }, token?: AccessToken): boolean {
    const scopeToken = this.getScopeToken(qscope);
    const anyToken = this.permissions?.authorize?.[scopeToken]?.[type]?.[AccessToken.ANY];
    const byToken = token ? this.permissions?.authorize?.[scopeToken]?.[type]?.[this.getAccessToken(token)] : [];
    return [
      ...anyToken ? anyToken : [],
      ...byToken ? byToken : [],
    ].includes(action);
  }

  onResourceCant(action: Action, type: ResourceType, value: any): boolean {
    return !this.onResourceCan(action, type, value);
  }

  onScopeCant(action: Action, type: ResourceType, qscope: { scope: Scope, facilityId?: string }, token?: AccessToken): boolean {
    return !this.onScopeCan(action, type, qscope, token);
  }

  getScopeToken({ scope, facilityId }: { scope: Scope, facilityId?: string }) {
    return scope === Scope.FACILITY && facilityId ? `${Scope.FACILITY}_${facilityId}` : Scope.GLOBAL;
  }

  getAccessToken(token: AccessToken) {
    return token === AccessToken.OWNER ? `${AccessToken.OWNER}_${this.user.id}` : token;
  }

}

export const FORBID: IPermissionEvaluator = {
  onScopeCan: () => false,
  onResourceCan: () => false,
  onScopeCant: () => true,
  onResourceCant: () => true,
};

@Injectable({
  providedIn: 'root'
})
export class PermissionService implements OnDestroy {

  private permissionEvaluator$: BehaviorSubject<IPermissionEvaluator>;
  readonly abilities$: Observable<IPermissionEvaluator>;
  subs: Subscription;

  constructor(
    private store: Store<any>,
  ) {
    this.permissionEvaluator$ = new BehaviorSubject(FORBID);
    this.abilities$ = this.permissionEvaluator$.asObservable();

    // Listen to user changes
    this.subs = this.store.select(selectCurrentUser).pipe(
      map((user) => {
        this.permissionEvaluator$.next(user ? this.makePermissionEvaluator(user) : FORBID);
      })
    ).subscribe();
  }

  ngOnDestroy(): void {
    if (this.subs) {
      this.subs.unsubscribe();
    }
  }

  syncAbilities(): IPermissionEvaluator {
    return this.permissionEvaluator$.getValue();
  }

  private makePermissionEvaluator(user: ICurrentUser): IPermissionEvaluator {
    return new SimplePermissionEvaluator(user);
  }

}
