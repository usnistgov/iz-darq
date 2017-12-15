import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {JobsService, JobResult} from "../services/jobs.service";
import {TreeNode} from "primeng/components/common/treenode";

@Component({
	selector: 'app-job-result',
	templateUrl: './job-result.component.html',
	styleUrls: ['./job-result.component.css']
})
export class JobResultComponent implements OnInit {

	result : JobResult;
	tree : TreeNode[];
	selected : any;
	tab : string = 'S';

	constructor(private route: ActivatedRoute,
				private  $jobs : JobsService) {
	}

	nodeSelect($event){
		console.log(this.selected);
	}

	async ngOnInit() {
		let ctrl = this;
		ctrl.tree = [];
		ctrl.result = null;
		let jobId : string;
		ctrl.route.params.subscribe(params => {
			jobId = params['id'];
			this.$jobs.getResult(jobId).then(function (x) {
				ctrl.result = x;
				console.log(x);
				ctrl.tree = ctrl.$jobs.fieldsTree(x);
				console.log(ctrl.tree);
			});
		});
	}

}
