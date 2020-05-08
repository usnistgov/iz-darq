import {Component, OnInit} from '@angular/core';
import {Summary, Range, Vocabulary} from "../../../domain/summary";
import {ActivatedRoute} from "@angular/router";
import {ADFData, Detections} from "../../../domain/adf";
import {RangesService} from "../../../services/ranges.service";
import {TreeNode} from "primeng/components/common/treenode";

@Component({
	selector: 'app-adf-summary',
	templateUrl: './adf-summary.component.html',
	styleUrls: ['./adf-summary.component.css']
})
export class ADFSummaryComponent implements OnInit {

	summary : Summary;
	file : ADFData;
	detections : Detections;
	vocabularyTreeByField : TreeNode[];
	vocabularyTreeByTable : TreeNode[];

	constructor(private route: ActivatedRoute, private $range : RangesService) {
	}

	ngOnInit() {
		let ctrl = this;
		this.route.data.subscribe(data => {
			ctrl.summary = data['file'].summary;
			ctrl.file = data['file'];
			ctrl.detections = data['detections'];
			let v : Vocabulary = ctrl.file.vocabulary;
			ctrl.vocabularyTreeByField = [];
			ctrl.vocabularyTreeByTable = [];
			for(let k in v.byField){
				let node : TreeNode = {
					data : {
						a : k,
					},
					children : v.byField[k].map(x => {
						return {
							data : {
								b : x
							},
							children : []
						}
					})
				};
				ctrl.vocabularyTreeByField.push(node);
			}

			for(let t in v.byTable){
				let node : TreeNode = {
					label : t,
					data : {
						a : t
					},
					children : v.byTable[t].map(x => {
						return {
							data : {
								b : x
							},
							children : []
						}
					})
				};
				ctrl.vocabularyTreeByTable.push(node);
			}
		});
	}

	printRange(r : Range){
		return this.$range.print(r);
	}


	detectionsList(){
		if(this.file && this.file.configuration && this.file.configuration.detections){
			let items = [];
			for(let code of this.file.configuration.detections){
				items.push({ id : code, description : this.detections[code].description, target :  this.detections[code].target});
			}
			return items;
		}
		return [];
	}

	itemizeExtract(){
		if(this.summary){
			let items = [];
			for(let key in this.summary.extract){
				items.push({ label : key, value : this.summary.extract[key] });
			}
			return items;
		}
		return [];
	}

}
