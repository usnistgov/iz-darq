import {Component, Input, OnInit} from '@angular/core';
import {CG, Field} from "../../../../../domain/report";
import {DisplayUtil} from "../../../../../services/display-util";

@Component({
	selector: 'app-payload-filter',
	templateUrl: './payload-filter.component.html',
	styleUrls: ['./payload-filter.component.css']
})
export class PayloadFilterComponent implements OnInit {

	_filters: AnalysisFilters;
	_display: DisplayUtil;
	_title: string = "Filter Data";
	_type: CG;
	_position: number;
	_active: number;

	constructor() {
	}

	@Input() set filters(filters: AnalysisFilters) {
		this._filters = filters;
	}

	@Input() set type(type: CG) {
		this._type = type;
	}

	@Input() set position(position: number) {
		this._position = position;
	}

	@Input() set display(display: DisplayUtil) {
		this._display = display;
	}

	@Input() set active(active: number) {
		this._active = active;
	}

	ngOnInit() {
	}

}

export interface AnalysisFilters {
	[field: string] :  string[]
}
