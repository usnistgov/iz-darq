/**
 * Created by hnt5 on 2/11/18.
 */
import {Resolve, ActivatedRouteSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {Summary} from "../domain/summary";
import {AdfService} from "../services/adf.service";


@Injectable()
export class SummaryResolver implements Resolve<Summary> {

	constructor(private $adf : AdfService) {}

	resolve(route: ActivatedRouteSnapshot) {
		let ctrl = this;
		return new Promise<Summary>(function (resolve, reject) {
			let id = route.params['fid'];
			try {
				resolve(null);
			}
			catch(e){
				reject(null);
			}
		});
	}
}

