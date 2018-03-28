import {Component, OnInit} from '@angular/core';
import {Summary, Range} from "../../../domain/summary";
import {ActivatedRoute} from "@angular/router";
import {ADFData, Detections} from "../../../domain/adf";
import {RangesService} from "../../../services/ranges.service";

@Component({
	selector: 'app-adf-summary',
	templateUrl: './adf-summary.component.html',
	styleUrls: ['./adf-summary.component.css']
})
export class ADFSummaryComponent implements OnInit {

	summary : Summary;
	file : ADFData;
	detections : Detections;

	constructor(private route: ActivatedRoute, private $range : RangesService) {
	}

	ngOnInit() {
		let ctrl = this;
		this.route.data.subscribe(data => {
			ctrl.summary = data['file'].summary;
			ctrl.file = data['file'];
			ctrl.detections = data['detections'];
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
