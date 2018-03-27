import {Component, OnInit} from '@angular/core';
import {ADFData} from "../../../domain/adf";
import {ActivatedRoute} from "@angular/router";
import * as moment from 'moment';

@Component({
	selector: 'app-data',
	templateUrl: './data.component.html',
	styleUrls: ['./data.component.css']
})
export class DataComponent implements OnInit {

	files : ADFData[];

	constructor(private route: ActivatedRoute) {
	}

	ngOnInit() {
		let ctrl = this;
		this.route.data.subscribe(data => {
			ctrl.files = data['files'];
		});
	}

	age(date : Date){
		return moment(date).fromNow();
	}

}
