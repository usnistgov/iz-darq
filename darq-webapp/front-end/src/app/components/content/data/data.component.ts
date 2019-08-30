import {Component, OnInit} from '@angular/core';
import {ADFData, ADFDescriptor} from "../../../domain/adf";
import {ActivatedRoute, Router} from "@angular/router";
import * as moment from 'moment';
import {MatDialog} from "@angular/material";
import {AnalysisDialogComponent} from "../../fragments/analysis-dialog/analysis-dialog.component";
import {AdfService} from "../../../services/adf.service";
import {NotifierService} from "angular-notifier";
import {ConfirmDialogComponent} from "../../fragments/confirm-dialog/confirm-dialog.component";

@Component({
	selector: 'app-data',
	templateUrl: './data.component.html',
	styleUrls: ['./data.component.css']
})
export class DataComponent implements OnInit {

	files : ADFDescriptor[];

	constructor(private router : Router, private route: ActivatedRoute,private _adf : AdfService, public dialog: MatDialog, private notifierService: NotifierService) {
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

	deleteADF(id : string, name : string){
		let ctrl = this;
		let dialogRef = this.dialog.open(ConfirmDialogComponent, {
			data: {
				action : "delete aggregate detections file "+name
			}
		});

		dialogRef.afterClosed().subscribe(result => {
			if (result) {
				this._adf.deleteADF(id).then(function (s) {
						ctrl.notifierService.notify("success", "Aggregate Detections File deleted successfully");
						ctrl._adf.getFiles().then((files) => {
							ctrl.files = files;
						});
						},
					function (e) {
						ctrl.notifierService.notify("error", "Could not delete Aggregate Detections File due to : " + e.error);
					});
			}
		});
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
