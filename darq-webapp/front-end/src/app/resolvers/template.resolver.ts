import {Injectable} from "@angular/core";
import {ReportDescriptor, ReportTemplate} from "../domain/report";
import {Resolve, ActivatedRouteSnapshot} from "@angular/router";
import {TemplateService} from "../services/template.service";
/**
 * Created by hnt5 on 3/26/18.
 */
@Injectable()
export class TemplateCatalogResolver implements Resolve<ReportDescriptor[]> {

	constructor(private $template : TemplateService) {}

	resolve(route: ActivatedRouteSnapshot) {
		return this.$template.catalog();
	}
}

@Injectable()
export class TemplateResolver implements Resolve<ReportTemplate> {

	constructor(private $template : TemplateService) {}

	resolve(route: ActivatedRouteSnapshot) {
		let ctrl = this;
		return new Promise<ReportTemplate>(function (resolve, reject) {
			let ctx = route.params['ctx'];
			if(ctx !== 'new'){
				try {
					resolve(ctrl.$template.findById(ctx));
				}
				catch(e){
					reject(null);
				}
			}
			else {
				resolve(null);
			}
		});
	}
}

@Injectable()
export class PatientCodeSetResolver implements Resolve<string[]> {

	constructor(private $template : TemplateService) {}

	resolve(route: ActivatedRouteSnapshot) {
		let ctrl = this;
		return new Promise<string[]>(function (resolve, reject) {
			resolve(ctrl.$template.findCodeSet("patient"));
		});
	}
}

@Injectable()
export class VaccineCodeSetResolver implements Resolve<string[]> {

	constructor(private $template : TemplateService) {}

	resolve(route: ActivatedRouteSnapshot) {
		let ctrl = this;
		return new Promise<string[]>(function (resolve, reject) {
			resolve(ctrl.$template.findCodeSet("vaccination"));
		});
	}
}


