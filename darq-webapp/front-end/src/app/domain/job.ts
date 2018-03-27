export class Job {
	id : string;
	name : string;
	user : string;
	configuration : {
		id : string,
		name : string
	};
	files : {
		patients : boolean,
		vaccines : boolean
	};
	dateStarted : Date;
	dateEnded : Date;
	dateCreated : Date;
	stats : {
		patients : number;
		done : number;
		eta : number;
	};
	progress : number;
	status : string;
	resultId : string;

	constructor(j : Job){
		this.id = j.id;
		this.name = j.name;
		this.configuration = j.configuration;
		this.files = j.files;
		this.dateEnded = j.dateEnded;
		this.dateStarted = j.dateStarted;
		this.dateCreated = j.dateCreated;
		this.progress = j.progress;
		this.status = j.status;
		this.stats = j.stats;
		this.resultId = j.resultId;
	}

	running(){
		return this.status === "STARTED";
	}

	queued(){
		return this.status === "QUEUED";
	}

	finished(){
		return this.status === "FINISHED";
	}

	stopped(){
		return this.status === "STOPED";
	}

	failed(){
		return this.status === "FAILED";
	}
}
