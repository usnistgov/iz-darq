// import { Component, OnInit } from '@angular/core';
// import {TreeNode} from "primeng/components/common/treenode";
// import {ActivatedRoute} from "@angular/router";
// import {JobsService} from "../../services/jobs.service";
//
// @Component({
//   selector: 'app-completion-result',
//   templateUrl: 'completion-result.component.html',
//   styleUrls: ['completion-result.component.css']
// })
// export class CompletionResultComponent implements OnInit {
//
// 	result : JobResult;
// 	tree : TreeNode[];
// 	selected : any[];
// 	tab : string = 'S';
// 	chart : any[];
//
// 	constructor(private route: ActivatedRoute,
// 				private  $jobs : JobsService) {
// 	}
//
// 	nodeSelect($event){
// 		console.log(this.selected);
// 	}
//
// 	selectAll(tree : TreeNode[]){
// 		this.selected = [];
// 		this.selectAllRecursive(tree);
// 	}
//
// 	selectAllRecursive(tree : TreeNode[]){
// 		for(let t of tree){
//
// 			if(t.children && t.children.length > 0){
// 				this.selectAll(t.children);
// 			}
// 			else {
// 				this.selected.push(t);
// 			}
// 		}
// 	}
//
// 	barChart(){
// 		this.chart = [];
// 		if(this.result){
// 			for(let va of this.result.valueAnalysis){
// 				for(let st of va.statistics){
// 					if(st.statisticId === 'not-valued'){
// 						this.chart.push({ name : va.field.path, value : (1 - (st.count / st.total)) * 100 });
// 					}
// 				}
// 			}
// 		}
// 	}
//
// 	async ngOnInit() {
// 		let ctrl = this;
// 		ctrl.chart = [];
// 		ctrl.tree = [];
// 		ctrl.result = null;
// 		let jobId : string;
// 		ctrl.route.parent.params.subscribe(params => {
// 			console.log(params);
// 			jobId = params['id'];
// 			this.$jobs.getResult(jobId).then(function (x) {
// 				ctrl.result = x;
// 				console.log(x);
// 				ctrl.tree = ctrl.$jobs.fieldsTree(x);
// 				//ctrl.selectAll(ctrl.tree);
// 				ctrl.barChart();
// 				console.log(ctrl.chart);
// 			});
// 		});
// 	}
//
// }
