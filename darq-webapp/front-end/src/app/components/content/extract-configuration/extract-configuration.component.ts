import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ConfigurationDescriptor, Configuration} from "../../../domain/configuration";
import {ConfigurationService} from "../../../services/configuration.service";
import {Range} from "../../../domain/summary";

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


	constructor(private route: ActivatedRoute, private $config : ConfigurationService, private router : Router) {
		this.catalog = [];
	}

	submittable(){
		return this.conf.name && this.conf.name !== "";
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
		let ctrl = this;
		this.$config.save(this.conf).then(async function (x) {
			ctrl.catalog = await ctrl.$config.catalog();
			console.log(x);
			if(x.status === "SUCCESS"){
				ctrl.router.navigate(["/configuration/"+x.message]).then(function (y) {
				});
			}
		});
	}

	async delete(){
		let ctrl = this;
		this.$config.remove(this.conf).then(async function (x) {
			ctrl.catalog = await ctrl.$config.catalog();
			ctrl.router.navigate(["/configuration/new"]).then(function (y) {
			});
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
			if(context === 'new')
				ctrl.control.vonly = false;
		});
		this.route.data.subscribe(data => {
			let dObj = data['detections'];
			ctrl.detections = [];
			if(data['configuration']){
				ctrl.conf = data['configuration'];
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
