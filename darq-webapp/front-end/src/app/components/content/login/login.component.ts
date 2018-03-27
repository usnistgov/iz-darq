import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {UserService} from "../../../services/user.service";

@Component({
	selector: 'app-login',
	templateUrl: 'login.component.html',
	styleUrls: ['login.component.css']
})
export class LoginComponent implements OnInit {

	username : string;
	password : string;
	error : {
		on : boolean,
		message : string
	} = {
		on : false,
		message : ""
	};

	constructor(private $user : UserService, private router : Router) {
	}

	login(){
		let ctrl = this;
		this.$user.logIn(this.username, this.password).then(function (x) {
			if(!x){
				ctrl.error.on = true;
				ctrl.error.message = "Unable to authenticate user, please try in a few moments";
				return;
			}

			if(x.status){
				ctrl.router.navigate(["data"]).then(function (x) {
					ctrl.error.on = false;
					ctrl.error.message = "";
				});
			}
			else {
				ctrl.error.on = !x.status;
				ctrl.error.message = x.message;
			}
		});
	}

	ngOnInit() {
		this.username = "";
		this.password = "";
	}

}
