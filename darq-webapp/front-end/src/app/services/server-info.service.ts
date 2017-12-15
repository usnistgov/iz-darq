import {Injectable} from '@angular/core';
import {Http, Response} from "@angular/http";

export class ServerInfo {
	version : string;
	qualifier : string;
	date : Date;

	constructor(v : string, q : string, d : Date){
		this.version = v;
		this.qualifier = q;
		this.date = d;
	}
}

@Injectable()
export class ServerInfoService {

	serverInfo : ServerInfo = null;

	constructor(private http: Http) {
		this.fetch();
	}

	private async fetch(){
		let serverInf : ServerInfo = await this.http
			.get("public/serverInfo")
			.map((response: Response) => response.json())
			.map(({version, qualifier, date}) => new ServerInfo(version, qualifier, date))
			.toPromise();

		this.serverInfo = serverInf;
		return serverInf;
	}

	async get() : Promise<ServerInfo> {
		if(this.serverInfo){
			return this.serverInfo;
		}
		else {
			return this.fetch();
		}
	}




}
