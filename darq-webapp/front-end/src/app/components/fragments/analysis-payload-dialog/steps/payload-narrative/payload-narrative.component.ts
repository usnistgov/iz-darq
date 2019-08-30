import {Component, Input, OnInit} from '@angular/core';

@Component({
	selector: 'app-payload-narative',
	templateUrl: './payload-narative.component.html',
	styleUrls: ['./payload-narative.component.css']
})
export class PayloadNarrativeComponent implements OnInit {

	_narrative: Narrative;
	_title: string = "Section Information";
	_position: number;
	_active: number;

	constructor() {
	}

	@Input() set narrative(narrative: Narrative) {
		this._narrative = narrative;
	}

	@Input() set position(position: number) {
		this._position = position;
	}

	@Input() set active(active: number) {
		this._active = active;
	}

	canStep() {
		return this._narrative && this._narrative.title && this._narrative.title.length > 0;
	}

	ngOnInit() {
	}

}

export interface Narrative {
	title: string;
	description: string;
}
