import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {AnalysisPayloadDialogComponent} from "../analysis-payload-dialog/analysis-payload-dialog.component";

@Component({
	selector: 'app-confirm-dialog',
	templateUrl: './confirm-dialog.component.html',
	styleUrls: ['./confirm-dialog.component.css']
})
export class ConfirmDialogComponent implements OnInit {

	action: string;

	constructor(public dialogRef: MatDialogRef<AnalysisPayloadDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {
		this.action = data.action;
	}

	do(){
		this.dialogRef.close(true);
	}

	cancel(){
		this.dialogRef.close(false);
	}

	ngOnInit() {
	}

}
