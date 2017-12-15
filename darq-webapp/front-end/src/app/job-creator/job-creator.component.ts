import {Component, OnInit} from '@angular/core';
import {Http, Headers} from "@angular/http";
import {Router} from "@angular/router";
import {ToastyService, ToastyConfig} from "ng2-toasty";
import {NgBlockUI, BlockUI} from "ng-block-ui";

@Component({
	selector: 'app-job-creator',
	templateUrl: './job-creator.component.html',
	styleUrls: ['./job-creator.component.css']
})
export class JobCreatorComponent implements OnInit {
	@BlockUI('job-create') blockUIList: NgBlockUI;
	patients: File;
	vaccines: File;
	name: string;
	configuration: {
		id: string;
		name: string;
	};
	configurations: any[];
	confToggle: boolean;

	constructor(private http: Http, private router : Router, private toastyService: ToastyService, private toastyConfig: ToastyConfig) {
		this.toastyConfig.theme = 'material';
	}

	ngOnInit() {
		this.configurations = [
			{
				id: "342ae98fcd",
				name: "default"
			}
		];
		this.confToggle = false;
	}

	toggleConfig() {
		this.confToggle = !this.confToggle;
	}

	filesValid(): boolean {
		if (this.patients && this.vaccines)
			return true;
		else
			return false;
	}

	config(): boolean {
		if (this.name && this.configuration)
			return true;
		else
			return false;
	}

	create() {
		let ctrl = this;
		let form : FormData = new FormData();
		form.append('name', this.name);
		form.append('configuration', JSON.stringify(this.configuration));
		form.append('patient', this.patients);
		form.append('vaccination', this.vaccines);
		this.blockUIList.start('Loading...'); // Start blocking element only
		//
		return this.http.post('api/jobs/create', form).toPromise().then(function (x) {
			ctrl.blockUIList.stop();
			if(x && x.status){

				ctrl.router.navigate(["/jobs","dashboard"]).then(function (x) {
					ctrl.growl("Job Added Successfully");
				});
			}
			else {
				ctrl.router.navigate(["/jobs","dashboard"]).then(function (x) {
					ctrl.growl("Failed to create job");
				});
			}
		},
		function (x) {
			ctrl.blockUIList.stop();
			ctrl.router.navigate(["/jobs","dashboard"]).then(function (x) {
				ctrl.growl("Failed to create job");
			});
		});
	}

	growl(message : string){
		this.toastyService.info(message);
	}

	patientsUpdate(file: File) {
		this.patients = file;
	}

	vaccinesUpdate(file: File) {
		this.vaccines = file;
	}

	selectConfig(conf) {
		this.configuration = conf;
		this.toggleConfig();
	}
}
