// import { Component, OnInit } from '@angular/core';
// import {ActivatedRoute} from "@angular/router";
// import * as _ from "lodash";
// import {HttpClient} from "@angular/common/http";
// import {JobsService} from "../../services/jobs.service";
//
// @Component({
//   selector: 'app-qda-result',
//   templateUrl: 'qda-result.component.html',
//   styleUrls: ['qda-result.component.css']
// })
// export class QdaResultComponent implements OnInit {
//
// 	result : JobResult;
// 	group : any;
// 	chart : any[];
// 	typeSearch : string;
// 	issues : Issue[];
//
// 	constructor(private route: ActivatedRoute,
// 				private  $jobs : JobsService,
// 				private http: HttpClient) {}
//
// 	total(){
// 		this.chart = [];
// 		for(let x in this.group){
// 			this.chart.push({ name : x + " - " + this.get(x), value : this.group[x].length, a : 'b'});
// 		}
// 		console.log(this.chart);
// 	}
//
// 	totalDT(){
// 		return this.result.detections.length;
// 	}
//
//
// 	async ngOnInit() {
// 		let ctrl = this;
// 		this.chart = [];
// 		ctrl.result = null;
// 		let jobId : string;
// 		ctrl.route.parent.params.subscribe(params => {
// 			jobId = params['id'];
// 			this.$jobs.getResult(jobId).then(function (x) {
// 				ctrl.result = x;
// 				ctrl.group = _.groupBy(ctrl.result.detections, x => {
// 					return x.type;
// 				});
// 				ctrl.total();
// 				console.log(ctrl.group);
// 			});
// 		});
// 		ctrl.issues = await ctrl.getDetections();
//
// 	}
//
// 	async getDetections(){
// 		let r = await this.http.get<Issue[]>("api/detections").toPromise();
// 		return r;
// 	}
//
// 	get(y : string){
// 		let z = this.issues.filter(x => {
// 			return x.code === y;
// 		});
// 		return z && z.length > 0 ? z[0].message : '';
// 	}
//
// }
//
// export class Issue {
// 	code : string;
// 	message : string;
// }
