import {Component, OnInit, Inject, TemplateRef, ViewChild} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from "@angular/material";
import {Field, _comp, AnalysisPayload, CG} from "../../../domain/report";
import {ConfigurationPayload} from "../../../domain/configuration";
import {RangesService} from "../../../services/ranges.service";
import {Detections} from "../../../domain/adf";

@Component({
	selector: 'app-analysis-payload-dialog',
	templateUrl: 'analysis-payload-dialog.component.html',
	styleUrls: ['analysis-payload-dialog.component.css']
})
export class AnalysisPayloadDialogComponent implements OnInit {

	@ViewChild('detection')
	detection: TemplateRef<any>;
	@ViewChild('table')
	table: TemplateRef<any>;
	@ViewChild('gender')
	gender: TemplateRef<any>;
	@ViewChild('event')
	event: TemplateRef<any>;
	@ViewChild('ageGroup')
	ageGroup: TemplateRef<any>;
	@ViewChild('value')
	value: TemplateRef<any>;

	types : {
		key : string,
		label : string,
		fields : Field[]
	}[];
	selected : {
		key : string,
		label : string,
		fields : Field[]
	};
	configuration : ConfigurationPayload;
	// payload : AnalysisPayload;
	filters : Field[];
	groupBy : Field[];
	values : {
		[index : string] : any
	};
	detectionList : {
		[index : string] : {
			label : string,
			value : {
				code : string,
				target : string,
				description : string
			}
		}[]
	};

	detectionsDescriptions : Detections;

	constructor(public dialogRef: MatDialogRef<AnalysisPayloadDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any, private $range : RangesService) {
	}

	removeField(i, list){
		list.splice(i,1);
	}

	tables(){
		if(this.selected){
			if(this.selected.key.charAt(0) === 'P'){
				return ['GENDER', 'ETH', 'LANGUAGE', 'RACE']
			}
			else {
				return ['MVX']
			}
		}
		else {
			return [];
		}
	}

	reset(){
		this.types = [];
		for(let x in this.data.names){
			this.types.push({
				key : x,
				label : this.data.names[x],
				fields : _comp[x].concat([])
			});
		}
		this.filters = [];
		this.groupBy = [];
		this.values = {};
	}

	submittable(){
		let f = this.filters.length > 0 || this.groupBy.length > 0;
		let v = true;
		for(let f of this.filters){
			v = v && this.values[f] && this.values[f] !== '';
		}

		return f && v;
	}

	resolve(){
		let payload : AnalysisPayload = new AnalysisPayload();
		payload.type = CG[this.selected.key];
		payload.groupBy = this.groupBy;
		for(let k in this.values){
			if(Field[k] === Field.DETECTION){
				payload.filters.push({ field : Field[k], value : this.values[k].code });
			}
			else {
				payload.filters.push({ field : Field[k], value : this.values[k] });
			}
		}
		return payload;
	}

	closeDialog() {
		this.dialogRef.close(this.resolve());
	}

	printRange(r){
		return this.$range.print(r);
	}

	remove(i, list, field){
		list.splice(i,1);
		this.selected.fields.push(field);
		if(this.values[field]){
			delete this.values[field];
		}
	}

	template(field : Field){
		switch (field){
			case Field.AGE_GROUP : return this.ageGroup;
			case Field.DETECTION : return this.detection;
			case Field.GENDER : return this.gender;
			case Field.TABLE : return this.table;
			case Field.EVENT : return this.event;
			default : return this.value;
		}
	}

	detectionsList(){
		if(this.selected) {
			if (this.selected.key.charAt(0) === 'V') {
				return this.detectionList["VACCINATION"];
			}
			else {
				return this.detectionList["PATIENT"];
			}
		}
	}

	same(a : any, b : any){
		return a.key === b.key;
	}


	ngOnInit() {
		this.reset();
		console.log(this.data.configuration);
		this.configuration = this.data.configuration;
		this.detectionsDescriptions = this.data.detections;
		this.detectionList = {
			'PATIENT' : [],
			'VACCINATION' : []
		};

		for(let d of this.configuration.detections){
			if(this.data.detections[d]['target'] === "VACCINATION"){
				this.detectionList["VACCINATION"].push({
					label : this.data.detections[d]['description'],
					value :{
						code : d,
						description : this.data.detections[d]['description'],
						target : this.data.detections[d]['target']
					}
				});
			}
			else {
				this.detectionList["PATIENT"].push({
					label : this.data.detections[d]['description'],
					value : {
						code : d,
						description : this.data.detections[d]['description'],
						target : this.data.detections[d]['target']
					}
				});
			}
		}

	}

}
