/**
 * Created by hnt5 on 2/11/18.
 */
import {Resolve, ActivatedRouteSnapshot} from "@angular/router";
import {ConfigurationService} from "../services/configuration.service";
import {Injectable} from "@angular/core";
import {Configuration, Field, Metric, ConfigurationDescriptor} from "../domain/configuration";
import {Detections} from "../domain/adf";


@Injectable()
export class ConfigurationResolver implements Resolve<Configuration> {

	constructor(private $config : ConfigurationService) {}

	resolve(route: ActivatedRouteSnapshot) {
		let ctrl = this;
		return new Promise<Configuration>(function (resolve, reject) {
			let ctx = route.params['ctx'];
			if(ctx !== 'new'){
				try {
					resolve(ctrl.$config.findById(ctx));
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
export class ConfigurationCatalogResolver implements Resolve<ConfigurationDescriptor[]> {

	constructor(private $config : ConfigurationService) {}

	resolve(route: ActivatedRouteSnapshot) {
		return this.$config.catalog();
	}
}

@Injectable()
export class DetectionsListResolver implements Resolve<Detections> {

	constructor(private $config : ConfigurationService) {}

	resolve(route: ActivatedRouteSnapshot) {
		return this.$config.getDetections();
	}
}

