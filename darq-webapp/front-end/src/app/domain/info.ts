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
