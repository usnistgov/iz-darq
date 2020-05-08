import {Component, OnInit, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {HttpClient} from "@angular/common/http";
import {ReportDescriptor} from "../../../domain/report";
import {Router} from "@angular/router";

@Component({
  selector: 'app-analysis-dialog',
  templateUrl: './analysis-dialog.component.html',
  styleUrls: ['./analysis-dialog.component.css']
})
export class AnalysisDialogComponent implements OnInit {

	templates : ReportDescriptor[];
	template : ReportDescriptor;
z
	constructor(public dialogRef: MatDialogRef<AnalysisDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any, private http : HttpClient, private router : Router) {

	}

	go(){
		this.dialogRef.close();
		this.router.navigate(["/report/"+this.template.id+"/"+this.data.id]).then(function (y) {
		});
	}

	async ngOnInit() {
		let id = this.data.id;
		this.templates = await this.http.get<ReportDescriptor[]>('api/template/for/'+id).toPromise();
	}

}
