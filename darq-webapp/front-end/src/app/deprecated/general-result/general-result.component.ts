// import {Component, OnInit} from '@angular/core';
// import {ActivatedRoute} from "@angular/router";
// import {JobsService} from "../../services/jobs.service";
//
// @Component({
// 	selector: 'app-general-result',
// 	templateUrl: 'general-result.component.html',
// 	styleUrls: ['general-result.component.css']
// })
// export class GeneralResultComponent implements OnInit {
//
// 	result: JobResult;
//
// 	constructor(private route: ActivatedRoute,
// 				private  $jobs : JobsService) {
// 	}
//
// 	pieChart(){
// 		if(this.result)
// 		return [ { name : 'Containing Issues', value : this.result.totalRecords - this.result.noDetectionRecords }, { name : 'Not Containing Issues', value : this.result.noDetectionRecords } ];
// 		else {
// 			return [];
// 		}
// 	}
//
// 	async ngOnInit() {
// 		let ctrl = this;
// 		ctrl.result = null;
// 		let jobId: string;
// 		ctrl.route.parent.params.subscribe(params => {
// 			console.log(params);
// 			jobId = params['id'];
// 			this.$jobs.getResult(jobId).then(function (x) {
// 				ctrl.result = x;
// 			});
// 		});
// 	}
//
// }
