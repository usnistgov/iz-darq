import {Component, Input, OnInit} from '@angular/core';

@Component({
	selector: 'app-step-header',
	templateUrl: './step-header.component.html',
	styleUrls: ['./step-header.component.css']
})
export class StepHeaderComponent implements OnInit {

	_title: string;
	_position: number;
	_active: number;

	constructor() {
	}

	@Input() set title(title: string) {
		this._title = title;
	}

	@Input() set position(position: number) {
		this._position = position;
	}

	@Input() set active(active: number) {
		this._active = active;
	}

	ngOnInit() {
	}

}
