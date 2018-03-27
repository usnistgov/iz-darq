import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ReportTemplate, ReportDescriptor} from "../domain/report";
import {OpRes} from "./jobs.service";

@Injectable()
export class TemplateService {

  constructor(private http: HttpClient) { }

	async findById(id: string) {
		return await this.http.get<ReportTemplate>("api/template/" + id).toPromise();
	}

	async catalog() {
		return this.http.get<ReportDescriptor[]>("api/template").toPromise();
	}

	async save(conf : ReportTemplate){
		return this.http.post<OpRes>("api/template", conf).toPromise();
	}

	async remove(conf : ReportTemplate){
		return this.http.delete<OpRes>("api/template/"+conf.id).toPromise();
	}

}
