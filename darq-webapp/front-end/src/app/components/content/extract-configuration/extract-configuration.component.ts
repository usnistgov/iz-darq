import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ConfigurationDescriptor, Configuration} from "../../../domain/configuration";
import {ConfigurationService} from "../../../services/configuration.service";
import {Range} from "../../../domain/summary";
import {NotifierService} from "angular-notifier";
import {MatDialog} from "@angular/material";
import {ConfirmDialogComponent} from "../../fragments/confirm-dialog/confirm-dialog.component";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import * as moment from 'moment';
import {DatePipe} from "@angular/common";

@Component({
	selector: 'app-extract-configuration',
	templateUrl: './extract-configuration.component.html',
	styleUrls: ['./extract-configuration.component.css']
})
export class ExtractConfigurationComponent implements OnInit {

	control : {
		edit : boolean,
		vonly : boolean
	} = {
		edit : false,
		vonly : false
	};
	detections : any;
	catalog : ConfigurationDescriptor[];
	conf : Configuration;
	detSupport : any[];
	formFields: FormGroup;


	constructor(private dialog : MatDialog, private route: ActivatedRoute, private $config : ConfigurationService, private router : Router, private notifierService: NotifierService, private formBuilder: FormBuilder, private dateFormat: DatePipe) {
		this.catalog = [];
		this.formFields = this.formBuilder.group({
			asOf : [new Date(), Validators.required],
			name : ['', Validators.required]
		});
		console.log(this.formFields);
	}

	submittable(){
		return this.formFields.valid && this.detSupport.length > 0;
	}

	ageGroups($event : Range[]){
		if($event instanceof Array){
			this.conf.payload.ageGroups = $event;
		}
	}

	async save(){
		this.conf.payload.detections = [];
		for(let d of this.detSupport){
			this.conf.payload.detections.push(d.id);
		}
		this.conf.payload.asOf = moment(this.formFields.getRawValue()['asOf']).format('MM/DD/YYYY');
		this.conf.name = this.formFields.getRawValue()['name'];
		let ctrl = this;

		this.$config.save(this.conf).then(async function (x) {
			ctrl.catalog = await ctrl.$config.catalog();
			if(x.status === "SUCCESS"){
				ctrl.notifierService.notify( 'success', 'Configuration saved successfully' );
					ctrl.router.navigate(["/configuration/"+x.message]).then(function (y) {
				});
			}
		},
		function (e) {
			ctrl.notifierService.notify( 'error', 'Could not save configuration due to : '+ e.error );
		});
	}

	async delete(){
		let ctrl = this;
		let dialogRef = this.dialog.open(ConfirmDialogComponent, {
			data: {
				action : "delete configuration "+ctrl.conf.name
			}
		});

		dialogRef.afterClosed().subscribe(result => {
			if (result) {
				this.$config.remove(this.conf).then(async function (x) {
					ctrl.catalog = await ctrl.$config.catalog();
					ctrl.notifierService.notify('success', 'Configuration deleted successfully');
					ctrl.router.navigate(["/configuration/new"]).then(function (y) {
					});
				},
				function (e) {
					ctrl.notifierService.notify('error', 'Could not delete configuration due to : ' + e.error);
				});
			}
		});
	}

	items(detections : any){
		let list : {
			id : string,
			description : string,
			target : string
		}[] = [];

		for(let key in detections){
			list.push({ id : key, description : detections[key].description, target :  detections[key].target});
		}

		return list;
	}


	ngOnInit() {
		let ctrl = this;
		this.route.params.subscribe(params => {
			let context = params['ctx'];
			ctrl.control.edit = context !== 'new';
			ctrl.formFields.patchValue({
				asOf : new Date(),
				name : ''
			});
			if(context === 'new')
				ctrl.control.vonly = false;
		});
		this.route.data.subscribe(data => {
			let dObj = data['detections'];
			ctrl.detections = [];
			if(data['configuration']){
				ctrl.conf = data['configuration'];
				const date = ctrl.conf.payload.asOf ? moment(ctrl.conf.payload.asOf,'MM/DD/YYYY').toDate() : new Date();
				ctrl.formFields.patchValue({
					asOf : date,
					name : ctrl.conf.name
				});
				ctrl.control.vonly = ctrl.conf.vonly;
				ctrl.detSupport = [];
				if(ctrl.control.vonly){
					for(let d of ctrl.conf.payload.detections){
						ctrl.detections.push({ id : d, description : dObj[d].description, target : dObj[d].target});
					}
				}
				else {
					ctrl.detections = ctrl.items(dObj);
					for(let d of ctrl.conf.payload.detections){
						ctrl.detSupport.push({ id : d, description : dObj[d].description, target : dObj[d].target});
					}
				}
			}
			else {
				ctrl.detSupport = [];
				ctrl.control.vonly = false;
				ctrl.detections = ctrl.items(dObj);
				ctrl.conf = new Configuration();
			}
			if(data['catalog']){
				ctrl.catalog = data['catalog'];
			}
		});
	}

}
