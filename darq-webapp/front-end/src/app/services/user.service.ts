import {Injectable} from '@angular/core';
import {Http, Response} from "@angular/http";
import 'rxjs/add/operator/map';
import {Subject} from "rxjs";
import {Router} from "@angular/router";

export class User {

	constructor(public id : string, public username : string, public roles : string[]){

	}
}

export class LoginResponse {

	public user : User;
	public status : boolean;
	public message : string;

	constructor(user : User, status : boolean, message : string){
		this.user = user;
		this.status = status;
		this.message = message;
	}
}


@Injectable()
export class UserService {

	currentUser: User = null;
	authentication : Subject<LoginResponse>;

	constructor(private http: Http, private router : Router) {
		let ctrl = this;
		this.authentication = new Subject<LoginResponse>();
		this.authentication.subscribe(next => {
			ctrl.currentUser = next.user;
		});

		this.me().then(function (loginResponse) {
			ctrl.authentication.next(loginResponse);
		},
		function (err) {
			ctrl.authentication.next({
				status : false,
				message : "no user",
				user : null
			});
		});
	}

	cuser(){
		return this.currentUser;
	}

	loggedIn(): boolean {
		return this.currentUser != null;
	}

	clear(){
		this.authentication.next(null);
	}

	logout(){
		let ctrl = this;
		this.http.get("api/logout").toPromise().then(function (x) {
			ctrl.router.navigate(['home']);
			ctrl.authentication.next(new LoginResponse(null, false, "logout"));
		},
		function (y) {
			ctrl.router.navigate(['home']);
		});
	}

	auth$(){
		return this.authentication;
	}

	async me() : Promise<LoginResponse> {
		try {
			let response : LoginResponse = await this.http
				.get("api/me")
				.map((response: Response) => response.json())
				.map(({user , status, message}) => new LoginResponse(user, status, message))
				.toPromise();

			return response;
		}
		catch(e){
			throw e;
		}
	}

	async logIn(username: string, password: string) : Promise<LoginResponse> {
		try {
			let response : LoginResponse = await this.http
				.post("api/login", { username, password })
				.map((response: Response) => response.json())
				.map(({user , status, message}) => new LoginResponse(user, status, message))
				.toPromise();
			this.authentication.next(response);
			return response;
		}
		catch(e){
			console.log(e);
			return null;
		}

	}
}
