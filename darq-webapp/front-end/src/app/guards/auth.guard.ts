import {Injectable} from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {UserService} from "../services/user.service";

@Injectable()
export class AuthGuard implements CanActivate {

	constructor(private router: Router, private $user : UserService){}

	canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
		let ctrl = this;
		return new Promise<boolean>((resolve, reject)=> {
			ctrl.$user.me().then(function (x) {
				if(x && x.status) resolve(true);
				else {
					ctrl.router.navigate(['home']);
					resolve(false);
				}
			},
			function (err) {
				ctrl.router.navigate(['home']);
				resolve(false);
			});
		});
	}
}
