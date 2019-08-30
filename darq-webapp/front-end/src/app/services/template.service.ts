import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ReportTemplate, ReportDescriptor, AnalysisPayload, Field} from "../domain/report";
import {Detections} from "../domain/adf";
import {RangesService} from "./ranges.service";
import {Range} from "../domain/summary";
import {CVX} from "../domain/configuration";
import {OpRes} from "../domain/info";

@Injectable()
export class TemplateService {

  constructor(private http: HttpClient, private $range : RangesService) { }

	async findById(id: string) {
		return await this.http.get<ReportTemplate>("api/template/" + id).toPromise();
	}

	async findCodeSet(id: string) {
		return await this.http.get<string[]>("api/template/codesets/" + id).toPromise();
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
		let fg = p.groupFilters && p.groupFilters.length > 1;
		if(groups && fg) return true;
		else if(groups && !fg){
			if(p.groupFilters.length === 0) return true;
			else if(p.groupFilters.length === 1){
				return Object.keys(p.groupFilters[0].values).length !== p.groupBy.length;
			}
		}
		return false;
	}

	groupFilterActivated(p : AnalysisPayload) : boolean {
		if(!p) return false;
		return p.groupFilters && p.groupFilters.length >= 1;
	}

	value(v : any, field : Field, ageGroups : {
		[index : string] : Range
	}, detections : Detections, cvx : CVX[]){

  		if(field === Field.DETECTION){
  			return v + " - " + detections[v].description;
		}
		else if(field === Field.AGE_GROUP){
  			let index : number = +(<string> v).replace("g","");
  			let nb = Object.keys(ageGroups).length;
  			// console.log(v);
			// console.log((<string> v).replace("grp",""));
  			// console.log(index);
  			// console.log(nb);
			if(index < nb) {
				return this.$range.print(ageGroups[v]);
			}
			else if(index === nb){
				return this.$range.barket(ageGroups[(nb-1)+"g"].max) + " -> +";
			}
			else {
				return "Unrecognized";
			}
		}
		else if(field === Field.VACCINE_CODE && cvx){
			for(let code of cvx){
				if(code.cvx === v){
					return v + " - " + code.name;
				}
			}
			return v + " - Unknown";
		}
		else if(field === Field.EVENT){
  			if(v === "00") return "00 - Administered";
			else if(v === "01") return "01 - Historical";
			else return v + " - Unknown"
		}
		else
			return v;
	}

}
