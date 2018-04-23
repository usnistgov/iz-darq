import {Component, OnInit} from '@angular/core';
import {ConfigurationDescriptor, Configuration, CVX} from "../../../domain/configuration";
import {Router, ActivatedRoute} from "@angular/router";
import {Section, ReportTemplate, ReportDescriptor} from "../../../domain/report";
import {ConfigurationService} from "../../../services/configuration.service";
import {Detections} from "../../../domain/adf";
import {TemplateService} from "../../../services/template.service";

@Component({
	selector: 'app-template',
	templateUrl: './template.component.html',
	styleUrls: ['./template.component.css']
})
export class TemplateComponent implements OnInit {

	control : {
		edit : boolean,
		vonly : boolean
	} = {
		edit : false,
		vonly : false
	};
	configCatalog : ConfigurationDescriptor[];
	catalog : ReportDescriptor[];
	configuration : ConfigurationDescriptor;
	template : ReportTemplate;
	detections : Detections;
	baseOn : ConfigurationDescriptor;
	cvx : CVX[];

	constructor(private route: ActivatedRoute, private router : Router, private $config : ConfigurationService, private $template : TemplateService) {
		this.configCatalog = [];
	}

	add(){
		this.template.sections.push(new Section());
	}

	same(a : ConfigurationDescriptor, b : ConfigurationDescriptor){
		return a.id === b.id;
	}

	submittable(){
		let basic = this.template.name && this.template.name !== '' && this.template.configuration && this.template.sections && this.template.sections.length > 0;
		let sections = true;
		for(let s of this.template.sections){
			sections = sections && s.title && s.title !== '' && s.payloads && s.payloads.length > 0;
		}
		return basic && sections;
	}

	async save(){
		let ctrl = this;
		this.$template.save(this.template).then(async function (x) {
			ctrl.catalog = await ctrl.$template.catalog();
			if(x.status === "SUCCESS"){
				ctrl.router.navigate(["/report-templates/"+x.message]).then(function (y) {
					ctrl.baseOn = null;
				});
			}
		});
	}

	async deleteTemplate(){
		let ctrl = this;
		this.$template.remove(this.template).then(async function (x) {
			ctrl.catalog = await ctrl.$template.catalog();
			ctrl.router.navigate(["/report-templates/new"]).then(function (y) {
				ctrl.baseOn = null;
			});
		});
	}

	async selectConfig($event){
		let config = await this.$config.findById($event.value.id);
		this.template.configuration = config.payload;
		this.template.sections = [];
	}

	filtered(){
		let ctrl = this;
		if(!this.configuration)
			return "";
		return this.catalog.filter(x => {
			return x.compatibilities.filter(y => {
					return y.id === ctrl.configuration.id;
				}).length > 0;
		});
	}

	update(i,$event){
		if($event instanceof Section){
			this.template.sections[i] = $event;
		}
	}

	delete(i){
		this.template.sections.splice(i,1);
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
			if(data['configCatalog']){
				ctrl.configCatalog = data['configCatalog'];
			}
			if(data['catalog']){
				ctrl.catalog = data['catalog'];
			}
			if(data['detectionList']){
				ctrl.detections = data['detectionList'];
			}
			if(data['cvx']){
				ctrl.cvx = data['cvx'];
			}
			if(!data['template']){
				ctrl.template = new ReportTemplate();
			}
			else {
				ctrl.template = data['template'];
			}
		});
	}

}
