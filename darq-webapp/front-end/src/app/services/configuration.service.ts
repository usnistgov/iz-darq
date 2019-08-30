import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {
	Configuration, ConfigurationDescriptor, CVX
} from "../domain/configuration";
import {Detections} from "../domain/adf";
import {OpRes} from "../domain/info";

@Injectable()
export class ConfigurationService {

	constructor(private http: HttpClient) {}

	async findById(id: string) {
		return await this.http.get<Configuration>("api/configuration/" + id).toPromise();
	}

	async catalog() {
		return this.http.get<ConfigurationDescriptor[]>("api/configuration").toPromise();
	}

	async save(conf : Configuration){
		return this.http.post<OpRes>("api/configuration", conf).toPromise();
	}

	async remove(conf : Configuration){
		return this.http.delete<OpRes>("api/configuration/"+conf.id).toPromise();
	}

	async getDetections(){
		return await this.http.get<Detections>("public/detections").toPromise();
	}

	async getCVX(){
		return await this.http.get<CVX[]>("public/cvx").toPromise();
	}


}
