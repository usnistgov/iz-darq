import {Component, OnInit} from '@angular/core';
import {ADFData} from "../../../domain/adf";
import {ActivatedRoute} from "@angular/router";
import * as moment from 'moment';
import {MatDialog} from "@angular/material";
import {AnalysisDialogComponent} from "../../fragments/analysis-dialog/analysis-dialog.component";

@Component({
	selector: 'app-data',
	templateUrl: './data.component.html',
	styleUrls: ['./data.component.css']
})
export class DataComponent implements OnInit {

	files : ADFData[];

	constructor(private route: ActivatedRoute,public dialog: MatDialog) {
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

	openDialog(id): void {
		let ctrl = this;
		let dialogRef = this.dialog.open(AnalysisDialogComponent, {
			width : '400px',
			data: {
				id : id
			}
		});
	}

}
