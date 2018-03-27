import {Component, OnInit} from '@angular/core';
import {ServerInfo} from "../../../domain/info";
import {ServerInfoService} from "../../../services/server-info.service";

@Component({
	selector: 'app-footer',
	templateUrl: 'footer.component.html',
	styleUrls: ['footer.component.css']
})
export class FooterComponent implements OnInit {

	serverInfo: ServerInfo = new ServerInfo("","",new Date());

	constructor(private sInfoService: ServerInfoService) {
	}

	async ngOnInit() {
		this.serverInfo = await this.sInfoService.get();
	}

	sInfo() {
		return this.serverInfo;
	}

}
