import {Injectable} from '@angular/core';
import {Subject} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {TreeNode} from "primeng/components/common/treenode";

export class OpRes {
	status : boolean;
	message : string;
	op : string;
}

export class JobResult {
	totalRecords : number;
	fields : SubjectField[];
}

export class SubjectField
{
	fieldOfInterest : string;
	statistics : {
		statisticId : string;
		count : number;
		total : number;
	}[];
	detections : any[];
}


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

@Injectable()
export class JobsService {

	jobs : Subject<Job[]>;
	stop : boolean;
	rate : number;

	constructor(private http: HttpClient) {
		this.rate = 20;
	}

	startFlow$(){
		this.stop = false;
		this.jobs = new Subject<Job[]>();
		this.fetchJobs();
		this.loop();
		return this.jobs;
	}

	fieldsTree(rs : JobResult) : TreeNode[] {
		let tree : TreeNode[] = [];
		for(let sub of rs.fields){
			this.addToTree(tree, sub, "", sub.fieldOfInterest);
		}
		return tree[0].children;
	}

	addToTree(tree : TreeNode[], sub : SubjectField, path_first : string, path_next : string){
		if(path_next === ""){
			let node : TreeNode = {
				label : path_first,
				selectable : true,
				data : sub,
				children : []
			};
			tree.push(node);
			return;
		}
		else {
			let z = path_next.split(".");
			let n_path_first = z[0];
			let n_path_next = z.length > 1 ? path_next.substr(path_next.indexOf(".")+1) : "";

			for(let tnode of tree){
				if(tnode.label === path_first){
					this.addToTree(tnode.children, sub, n_path_first, n_path_next);
					return;
				}
			}
			let node : TreeNode = {
				label : path_first,
				selectable : false,
				children : []
			};
			tree.push(node);
			this.addToTree(node.children, sub, n_path_first, n_path_next);
		}
	}

	loop(){
		let ctrl = this;
		setTimeout(()=>{
			ctrl.fetchJobs();
			if(!ctrl.stop){
				ctrl.loop();
			}
		},ctrl.rate * 1000);
	}

	refreshRate(obs : Subject<number>){
		obs.subscribe(x => {
			this.rate = x;
		});
	}

	async deleteJob(j : Job){
		let r = await this.http.delete<OpRes>("api/job/"+j.id).toPromise();
		if(r.status){
			//TODO OK
		}
		else {
			//TODO Failure
		}
	}

	async stopJob(j : Job){
		let r = await this.http.get<OpRes>("api/job/"+j.id+"/stop").toPromise();
		if(r.status){
			//TODO OK
		}
		else {
			//TODO Failure
		}
	}

	async getResult(id : string){
		let r = await this.http.get<JobResult>("api/job/"+id+"/result").toPromise();
		return r;
	}

	async fetchJobs() {
		try {
			let jobs = await this.http.get<Job[]>("api/jobs").toPromise();
			if(this.jobs){
				let result : Job[] = [];
				for(let job of jobs){
					result.push(new Job(job));
				}
				this.jobs.next(result);
			}
		}
		catch(e){
			this.halt();
		}
	}

	halt(){
		this.stop = true;
	}

	myJobs$() {

	}

}
