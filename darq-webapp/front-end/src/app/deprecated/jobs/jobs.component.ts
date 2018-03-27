import {Component, OnInit} from '@angular/core';
// import {Subject} from "rxjs";
// import {Job} from "../../../domain/job";
// import {JobsService} from "../../../services/jobs.service";


@Component({
	selector: 'app-jobs',
	templateUrl: 'jobs.component.html',
	styleUrls: ['jobs.component.css']
})
export class JobsComponent implements OnInit {

	// rates : number[];
	// rate$ : Subject<number> = new Subject<number>();
	// current : number;
	// jobs : Job[];
	// active : number;
    //
	// constructor(private $jobs : JobsService) {}
    //
	// setRefreshRate(i : number){
	// 	this.rate$.next(i);
	// 	this.current = i;
	// }
    //
	// toggle(i){
	// 	if(this.toggled(i)){
	// 		this.active = -1;
	// 	}
	// 	else
	// 		this.active = i;
	// }
    //
	// toggled(i){
	// 	return i === this.active;
	// }
    //
	ngOnInit() {}
	// 	this.rates = [
	// 		-1, 5, 10, 15
	// 	];
	// 	this.current = 5;
	// 	this.jobs = [];
	// 	this.$jobs.refreshRate(this.rate$);
	// 	this.rate$.next(this.current);
	// 	this.$jobs.startFlow$().subscribe(x => {
	// 		this.updateList(x);
	// 	});
	// }
    //
	// find(x : Job){
	// 	return this.jobs.find(z => {
	// 		return z.id === x.id;
	// 	});
	// }
    //
	// updateList(x : Job[]){
	// 	while(x.length > 0){
	// 		let job = x.pop();
	// 		let e_job = this.find(job);
	// 		if(e_job){
	// 			Object.assign(e_job, job);
	// 		}
	// 		else {
	// 			this.jobs.push(job)
	// 		}
	// 	}
	// }

}
