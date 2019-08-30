import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {Range} from "../../../domain/summary";
import {CVX} from "../../../domain/configuration";
import {Detections} from "../../../domain/adf";
import {TemplateService} from "../../../services/template.service";
import {Field} from "../../../domain/report";

@Component({
	selector: 'app-data-table',
	templateUrl: './data-table.component.html',
	styleUrls: ['./data-table.component.css']
})
export class DataTableComponent implements OnInit {

	_table: any;
	_cvxs : CVX[];
	_ageGroups : {
		[index : string] : Range
	};
	_detections: Detections;

	constructor(private $template : TemplateService, private cdRef : ChangeDetectorRef) {
	}

	@Input() set table(table) {
		console.log(table);
		this._table = table;
	}

	@Input() set cvxs(cvxs) {
		this._cvxs = cvxs;
	}

	@Input() set ageGroups(ageGroups) {
		this._ageGroups = ageGroups;
	}

	@Input() set detections(detections) {
		this._detections = detections;
	}

	NaN(x) {
		return isNaN(x);
	}

	below(flag: string, threshold: number, value: number) {
		if (flag == 'NONE' || value < threshold) {
			return value;
		} else {
			return threshold;
		}
	}

	inside(flag: string, threshold: number, value: number) {
		if (flag == 'NONE') {
			return 0;
		} else {
			return Math.abs(value - threshold);
		}
	}

	above(flag: string, value: number) {
		return 100 - value;
	}

	value(f: Field, v: any) {
		return this.$template.value(v, f, this._ageGroups, this._detections, this._cvxs);
	}

	ngOnInit() {
		this.cdRef.detectChanges();
	}

	onResized(d, row) {
		console.log(d.newHeight);
		console.log(row);
		row.height = d.newHeight;
	}

}
