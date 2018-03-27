/**
 * Created by hnt5 on 3/22/18.
 */
import {Resolve, ActivatedRouteSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {AdfService} from "../services/adf.service";
import {ADFData, ADFDescriptor} from "../domain/adf";


@Injectable()
export class ADFListResolver implements Resolve<ADFDescriptor[]> {

	constructor(private $adf : AdfService) {}

	resolve(route: ActivatedRouteSnapshot) {
		let ctrl = this;
		return new Promise<ADFDescriptor[]>(function (resolve, reject) {
			try {
				resolve(ctrl.$adf.getFiles());
			}
			catch(e){
				reject(null);
			}
		});
	}
}

@Injectable()
export class ADFResolver implements Resolve<ADFData> {

	constructor(private $adf : AdfService) {}

	resolve(route: ActivatedRouteSnapshot) {
		let ctrl = this;
		return new Promise<ADFData>(function (resolve, reject) {
			let id = route.params['fid'];
			try {
				resolve(ctrl.$adf.getADF(id));
			}
			catch(e){
				reject(null);
			}
		});
	}
}
