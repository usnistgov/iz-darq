import {Component, OnInit} from '@angular/core';
// import {TreeNode} from "primeng/components/common/treenode";
// import {ActivatedRoute} from "@angular/router";
// import {MetricPipe} from "../../../pipes/metrics-filter.pipe";
// import {Metric, ExtractType, Field, Configuration, ConfigurationDescriptor} from "../../../domain/configuration";
// import {ConfigurationService} from "../../../services/configuration.service";
// import * as _ from "lodash";

@Component({
	selector: 'app-configuration',
	templateUrl: 'configuration.component.html',
	styleUrls: ['configuration.component.css']
})
export class ConfigurationComponent implements OnInit {

	// catalog : ConfigurationDescriptor[];
	// _add : boolean;
	// dataExtractTree : TreeNode[];
	// metrics : Metric[];
	// selectedMetrics : Metric[];
	// name : string;
	// cursor : Configuration;
	// extractionStrategy : {
	// 	label : string,
	// 	value : string,
	// 	color : string
	// }[];
	// nbFiltered : number;
    //
	// constructor(private metricFilter : MetricPipe, private route: ActivatedRoute, private $config : ConfigurationService) {
	// 	this.extractionStrategy = [
	// 		{label : 'Not Extracted', value : ExtractType.NOT_EXTRACTED, color : '#e2a5aa'},
	// 		{label : 'Not Collected', value : ExtractType.NOT_COLLECTED, color : '#e2cf77'},
	// 		{label : 'Value Extracted', value : ExtractType.VALUE_EXTRACTED, color : '#c1e2a5'},
	// 		{label : 'Metadata Extracted', value : ExtractType.METADATA_EXTRACTED, color : '#8fd0e2'}
	// 	];
	// 	this.selectedMetrics = [];
	// 	this.name = "";
	// 	this._add = true;
	// }
    //
	// filter(){
	// 	let x = this.metricFilter.transform(this.metrics, this.dataExtractTree);
	// 	this.nbFiltered = this.metrics.length - x.length;
	// 	return x;
	// }
    //
	// getColor(type : ExtractType) : string {
	// 	for(let x of this.extractionStrategy){
	// 		if(x.value === type) return x.color;
	// 	}
	// 	return "#ffffff";
	// }
    //
	// nbTreeValue(type : string, dataExtractTree : TreeNode[]){
	// 	let nb = 0;
	// 	for(let node of dataExtractTree){
	// 		nb += (node.data.extraction === type ? 1 : 0) + this.nbTreeValue(type, node.children ? node.children : []);
	// 	}
	// 	return nb;
	// }

	async ngOnInit() {}
		// let ctrl = this;
        //
		// this.route.params.subscribe(params => {
		// 	let context = params['ctx'];
		// 	if(context === 'new') {
		// 		ctrl.cursor = {
		// 			name : "NEW",
		// 			dataExtract : {},
		// 			metrics : {}
		// 		};
		// 		ctrl._add = true;
		// 	}
		// 	else {
		// 		ctrl._add = false;
		// 	}
		// });
    //
	// 	this.route.data.subscribe(data => {
	// 		ctrl.catalog = data['catalog'];
	// 		ctrl.dataExtractTree = data['tree'].map(x => new Field(x).asNode());
	// 		ctrl.metrics = data['metrics'];
	// 		ctrl.cursor = data['cursor'] ? data['cursor'] : ctrl.cursor;
	// 		if(data['cursor']){
	// 			ctrl.name = ctrl.cursor.name;
	// 			ctrl.$config.applyDelta(ctrl.dataExtractTree, ctrl.cursor.dataExtract);
	// 			ctrl.selectedMetrics = ctrl.$config.initMetrics(ctrl.metrics, ctrl.cursor.metrics);
	// 		}
	// 		else {
	// 			ctrl.selectedMetrics = ctrl.metrics;
	// 		}
    //
	// 	});
	// }
    //
	// save(){
	// 	this.$config.save(this._add ? null : this.cursor.id, this.name, this.dataExtractTree, this.selectedMetrics);
	// }
    //
	// remove(conf : Configuration){
	// 	this.$config.remove(conf);
	// }
    //
	// extractChange(){
	// 	let possible = this.filter();
	// 	this.selectedMetrics = _.remove(this.selectedMetrics, function(n) {
	// 		return !_.find(possible, function (m) {
	// 			return m.id === n.id;
	// 		});
	// 	});
	// }

}
