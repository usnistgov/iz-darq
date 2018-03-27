import {Component, OnInit} from '@angular/core';
import {ConfigurationDescriptor, Configuration} from "../../../domain/configuration";
import {Router, ActivatedRoute} from "@angular/router";
import {Section, ReportTemplate, ReportDescriptor} from "../../../domain/report";
import {ConfigurationService} from "../../../services/configuration.service";
import {Detections} from "../../../domain/adf";

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
	configurationValue : Configuration;
	template : ReportTemplate;
	detections : Detections;

	constructor(private route: ActivatedRoute, private router : Router, private $config : ConfigurationService) {
		this.configCatalog = [];
	}

	add(){
		this.template.sections.push(new Section());
	}

	same(a : ConfigurationDescriptor, b : ConfigurationDescriptor){
		return a.id === b.id;
	}

	async configChange($event){
		this.configurationValue = await this.$config.findById($event.value.id);
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
			if(!data['template']){
				ctrl.template = new ReportTemplate();
			}
			else {
				ctrl.template = data['template'];
			}
		});
	}

}
