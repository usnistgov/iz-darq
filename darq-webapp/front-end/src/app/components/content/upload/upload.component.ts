import {Component, OnInit} from '@angular/core';
import {NgBlockUI, BlockUI} from "ng-block-ui";
import {Http} from "@angular/http";
import {Router} from "@angular/router";

@Component({
	selector: 'app-upload',
	templateUrl: './upload.component.html',
	styleUrls: ['./upload.component.css']
})
export class UploadComponent implements OnInit {
	@BlockUI('adf-upload') blockUIList: NgBlockUI;
	name : string;
	file : File;
	check : boolean;
	terms : string;

	constructor(private http: Http, private router : Router) {
		this.terms = "Application for the use of the FITS does not guarantee that the applicant will be granted approval for use by the National Institute of Standards and Technology (NIST).The FITS software is hosted on a NIST server, and your information will not be visible to users other than NIST staff and yourselves. Information provided in the FITS does not imply NIST endorsement of any particular product, service, organization, company, information provider, or content. This software was developed at the NIST by employees of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the United States Code, this software is not subject to copyright protection and is in the public domain. NIST assumes no responsibility whatsoever for its use by other parties, and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic. We would appreciate acknowledgment if the software is ";
	}

	setFile(file: File) {
		this.file = file;
	}

	create() {
		let ctrl = this;
		let form : FormData = new FormData();
		form.append('name', this.name);
		form.append('file', this.file);
		this.blockUIList.start('Loading...'); // Start blocking element only
		return this.http.post('api/adf/upload', form).toPromise().then(function (x) {
				ctrl.blockUIList.stop();
				ctrl.router.navigate(["/data/files"]).then(function (x) {
				});
			},
			function (x) {
				ctrl.blockUIList.stop();
				ctrl.router.navigate(["/data/files"]).then(function (x) {
				});
			});
	}

	ngOnInit() {
	}

}
