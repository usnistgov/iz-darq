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
