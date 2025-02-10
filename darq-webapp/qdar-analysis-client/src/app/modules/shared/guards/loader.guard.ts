import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { TurnOnLoader } from '@usnistgov/ngx-dam-framework-legacy';

@Injectable({
  providedIn: 'root'
})
export class LoaderGuard implements CanActivate {

  constructor(private store: Store<any>) { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
    this.store.dispatch(new TurnOnLoader({ blockUI: true }));
    return true;
  }

}
