import {Component, OnInit, Inject, TemplateRef, ViewChild} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from "@angular/material";
import {Field, _comp, AnalysisPayload, CG, Options, GroupFilter} from "../../../domain/report";
import {ConfigurationPayload} from "../../../domain/configuration";
import {RangesService} from "../../../services/ranges.service";
import {Detections} from "../../../domain/adf";
import {TemplateService} from "../../../services/template.service";

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
	step: number;
	types: {
		key: string,
		label: string,
		fields: Field[]
	}[];
	selected: {
		key: string,
		label: string,
		fields: Field[]
	};
	configuration: ConfigurationPayload;
	threshold: boolean;

	filters: Field[];
	groupBy: Field[];
	values: {
		[index: string]: any
	};
	groupFilters: GroupFilter[];
	enabledFilters: {
		[index: string]: boolean
	};

	detectionList: {
		[index: string]: {
			label: string,
			value: {
				code: string,
				target: string,
				description: string
			}
		}[]
	};

	cvxs: {};
	options: Options;


	detectionsDescriptions: Detections;

	constructor(public dialogRef: MatDialogRef<AnalysisPayloadDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any, private $range: RangesService, private $template: TemplateService) {
		this.step = 1;
	}

	activateThreshold(){
		if(!this.threshold){
			this.options.threshold = 0;
		}
		else {
			this.options.threshold = -1;
		}
	}

	activateGroupThreshold(g){
		if(!g.enableT){
			g.threshold = 0;
		}
		else {
			g.threshold = -1;
		}
	}

	removeField(i, list) {
		let e = list[i];
		for (let gF of this.groupFilters) {
			delete gF[e];
		}
		list.splice(i, 1);
	}

	tables() {
		if (this.selected) {
			if (this.selected.key.charAt(0) === 'P') {
				return this.data.codes['patient'];
			}
			else {
				return this.data.codes['vaccine'];
			}
		}
		else {
			return [];
		}
	}

	reset() {
		this.types = [];
		for (let x in this.data.names) {
			this.types.push({
				key: x,
				label: this.data.names[x],
				fields: _comp[x].concat([])
			});
		}
		this.threshold = false;
		this.filters = [];
		this.groupBy = [];
		this.groupFilters = [];
		this.enabledFilters = {};
		this.values = {};
		this.options = {
			threshold: 0,
			chartType: 'bar',
			countType: 'per'
		};

		if (this.selected && (CG[this.selected.key] === CG.PT || CG[this.selected.key] === CG.VT)) {
			this.selected.fields.splice(this.selected.fields.lastIndexOf(Field.TABLE), 1);
			this.filters.push(Field.TABLE);
		}
	}

	submit() {

	}

	fixed(g: Field) {
		if (CG[this.selected.key] === CG.PT || CG[this.selected.key] === CG.VT && g === Field.TABLE) {
			return true;
		}
		else {
			return false;
		}
	}

	submittable() {
		let f = this.filters.length > 0 || this.groupBy.length > 0;
		let v = true;
		for (let f of this.filters) {
			v = v && this.values[f] && this.values[f] !== '';
		}

		return f && v;
	}

	resolve() {
		let payload: AnalysisPayload = new AnalysisPayload();
		payload.type = CG[this.selected.key];
		payload.groupBy = this.groupBy;
		payload.options = this.options;
		payload.options.threshold = this.threshold ? payload.options.threshold : -1;

		for (let k in this.values) {
			if (Field[k] === Field.DETECTION) {
				payload.filters.push({field: Field[k], value: this.values[k].code});
			}
			else {
				payload.filters.push({field: Field[k], value: this.values[k]});
			}
		}

		for (let row of this.groupFilters) {
			let rerow: GroupFilter = {values: {}, threshold: row.threshold};

			for (let cell in row.values) {
				if (Field[cell] === Field.DETECTION) {
					rerow.values[cell] = row.values[cell].code;
				}
				else {
					rerow.values[cell] = row.values[cell];
				}
			}
			payload.groupFilters.push(rerow);
		}

		return payload;
	}

	search($event, obj, list) {
		list.values = Object.keys(obj).filter(x => {
			return x.includes($event.query) || obj[x].includes($event.query);
		});
	}

	suggestions(f: Field) {
		if (Field[f] === Field.VACCINE_CODE)
			return this.cvxs;
		else
			return {};
	}

	closeDialog() {
		this.dialogRef.close(this.resolve());
	}

	printRange(r) {
		return this.$range.print(r);
	}

	remove(i, list, field) {
		list.splice(i, 1);
		this.selected.fields.push(field);
		if (this.values[field]) {
			delete this.values[field];
		}
	}

	template(field: Field) {
		switch (field) {
			case Field.AGE_GROUP :
				return this.ageGroup;
			case Field.DETECTION :
				return this.detection;
			case Field.GENDER :
				return this.gender;
			case Field.TABLE :
				return this.table;
			case Field.EVENT :
				return this.event;
			default :
				return this.value;
		}
	}

	detectionsList() {
		console.log(this.selected.key.charAt(0));
		return this.selected.key.charAt(0);
	}

	same(a: any, b: any) {
		return a.key === b.key;
	}

	print(x) {
		console.log(x);
	}


	ngOnInit() {
		this.reset();
		console.log(this.data.configuration);
		this.configuration = this.data.configuration;
		this.detectionsDescriptions = this.data.detections;
		this.detectionList = {
			'P': [],
			'V': []
		};
		this.cvxs = this.data.cvx;

		console.log(this.configuration.detections);
		console.log(this.data.detections);

		for (let d of this.configuration.detections) {
			if (this.data.detections[d]['target'] === "VACCINATION") {
				this.detectionList["V"].push({
					label: this.data.detections[d]['description'],
					value: {
						code: d,
						description: this.data.detections[d]['description'],
						target: this.data.detections[d]['target']
					}
				});
			}
			else {
				this.detectionList["P"].push({
					label: this.data.detections[d]['description'],
					value: {
						code: d,
						description: this.data.detections[d]['description'],
						target: this.data.detections[d]['target']
					}
				});
			}
		}
	}

	addGroupFilter() {
		this.groupFilters.push({values: {}, threshold : -1});
	}

	removeGroupFilter(i: number) {
		this.groupFilters.splice(i, 1);
	}

	enable(e: Field, b) {
		console.log(this.groupFilters);
		if (b.checked === false) {
			for (let gF of this.groupFilters) {
				delete gF[e];
			}
		}
		this.enabledFilters[e] = b.checked;
	}

	aFilterIsEnabled() {
		let x = false;
		for (let g of this.groupBy) {
			x = x || this.enabledFilters[g];
		}
		return x;
	}

	canStep() {
		let i = this.step;
		if (i === 1) {
			let v = true;
			for (let f of this.filters) {
				v = v && this.values[f] && this.values[f] !== '';
			}
			return v;
		}
		else if (i === 2) {
			let must = (((CG[this.selected.key] === CG.VD) || (CG[this.selected.key] === CG.PD)) && this.selected.fields.lastIndexOf(Field.DETECTION) != -1) || (((CG[this.selected.key] === CG.VT) || (CG[this.selected.key] === CG.PT)) && this.selected.fields.lastIndexOf(Field.CODE) != -1)
			return (this.filters && this.filters.length > 0) || (this.groupBy && this.groupBy.length > 0) && !must;
		}
		else if (i === 3) {
			let v = true;
			for (let gF of this.groupFilters) {
				for (let g of this.groupBy) {
					v = v && (!this.enabledFilters[g] || (gF.values[g] && gF.values[g] !== ''));
				}
			}
			return v;
		}
		else if (i === 4) {
			return true;
		}
		return false;
	}

	keysOf(x) {
		if (x) {
			return Object.keys(x);
		}
		else
			return [];
	}

	next() {
		if (this.step === 1)
			this.step = 2;
		else if (this.step === 2) {
			if (this.groupBy && this.groupBy.length > 0)
				this.step = 3;
			else
				this.step = 4;
		}
		else if (this.step === 3) {
			if (!this.needOption()) {
				this.closeDialog();
			}
			else {
				this.step++;
			}
		}
		else if (this.step === 4) {
			this.closeDialog();
		}
		else {
			this.step++;
		}
	}

	plural() {
		let p: AnalysisPayload = new AnalysisPayload();
		p.groupFilters = this.groupFilters;
		p.groupBy = this.groupBy;
		return this.$template.plural(p);
	}

	needOption(){
		let p: AnalysisPayload = new AnalysisPayload();
		p.groupFilters = this.groupFilters;
		p.groupBy = this.groupBy;
		return !this.$template.groupFilterActivated(p) && !this.$template.plural(p);
	}

	back() {
		if (this.step === 4) {
			if (this.groupBy && this.groupBy.length > 0)
				this.step = 3;
			else
				this.step = 2;
		}
		else {
			this.step--;
		}
	}


	issues() {
		if (this.step === 1) {
			let v = true;
			for (let f of this.filters) {
				v = v && this.values[f] && this.values[f] !== '';
			}
			return v ? [] : ["Please fill all the selected filters"];
		}
		else if (this.step === 2) {
			let errors = [];
			if (!((this.filters && this.filters.length > 0) || (this.groupBy && this.groupBy.length > 0))) errors.push("At least one filter or group is required");
			switch (CG[this.selected.key]) {
				case CG.VD :
				case CG.PD :
					if (this.selected.fields.lastIndexOf(Field.DETECTION) != -1) errors.push("[DETECTION] Criterion must be used as a Filter or Group");
					break;
				case CG.VT :
				case CG.PT :
					if (this.selected.fields.lastIndexOf(Field.CODE) != -1) errors.push("[CODE] Criterion must be used as a Filter or Group");
					break;
				case CG.V :
					break;
			}
			return errors;
		}
		else if (this.step === 3) {
			let v = true;
			for (let gF of this.groupFilters) {
				for (let g of this.groupBy) {
					v = v && (!this.enabledFilters[g] || (gF.values[g] && gF.values[g] !== ''));
				}
			}
			return v ? [] : ["Please fill all the selected group filters"];
		}
		else if (this.step === 4) {
			return [];
		}
	}


}
