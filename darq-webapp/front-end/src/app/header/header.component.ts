import {Component, OnInit} from '@angular/core';
import {ServerInfo, ServerInfoService} from "../services/server-info.service";
import {UserService} from "../services/user.service";

@Component({
	selector: 'app-header',
	templateUrl: './header.component.html',
	styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

	serverInfo : ServerInfo = null;

	constructor(private sInfoService: ServerInfoService, private userService : UserService) {
		this.serverInfo = new ServerInfo("","",new Date());
	}

	async ngOnInit() {
		this.serverInfo = await this.sInfoService.get();
	}

	sInfo() {
		return this.serverInfo;
	}

	logged(){
		return this.userService.loggedIn();
	}

	user(){
		return this.userService.cuser();
	}

	logout(){
		return this.userService.logout();
	}

}
