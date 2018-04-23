import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ReportTemplate, ReportDescriptor, AnalysisPayload, Field} from "../domain/report";
import {OpRes} from "./jobs.service";
import {Detections} from "../domain/adf";
import {RangesService} from "./ranges.service";
import {Range} from "../domain/summary";
import {CVX} from "../domain/configuration";

@Injectable()
export class TemplateService {

  constructor(private http: HttpClient, private $range : RangesService) { }

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

	plural(p : AnalysisPayload) : boolean {
  		if(!p) return false;

		let groups = p.groupBy && p.groupBy.length > 0;
		let fg = p.groupFilters && p.groupFilters.length != 1;
		let all = true;
		if(p.groupFilters){
			for(let gF of p.groupFilters){
				all = all && Object.keys(gF).length === p.groupBy.length;
			}
		}
		return groups && fg && all;
	}

	value(v : any, field : Field, ageGroups : {
		[index : string] : Range
	}, detections : Detections, cvx : CVX[]){

  		if(field === Field.DETECTION){
  			return v + " - " + detections[v].description;
		}
		else if(field === Field.AGE_GROUP){
  			return this.$range.print(ageGroups[v]);
		}
		else if(field === Field.VACCINE_CODE && cvx){
			for(let code of cvx){
				if(code.cvx === v){
					return v + " - " + code.name;
				}
			}
			return v + " - Unknown";
		}
		else
			return v;
	}

}
