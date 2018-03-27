import {Injectable} from '@angular/core';
import {Subject} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {Job} from "../domain/job";

export class OpRes {
	status : string;
	message : string;
	op : string;
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

}
