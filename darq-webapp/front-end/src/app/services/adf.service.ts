import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ADFData, ADFDescriptor} from "../domain/adf";


@Injectable()
export class AdfService {

	constructor(private http: HttpClient) {
	}

	async getFiles() : Promise<ADFDescriptor[]> {
		let summary: ADFDescriptor[] = await this.http.get<ADFDescriptor[]>("api/adf").toPromise();
		return summary;
	}

	async getADF(id : string) : Promise<ADFData> {
		let summary: ADFData = await this.http.get<ADFData>("api/adf/"+id).toPromise();
		return summary;
	}

}
