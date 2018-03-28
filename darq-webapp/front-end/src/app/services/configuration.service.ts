import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {
	Configuration, Metric, Field, ExtractTreeDelta, MetricParam, ExtractType,
	ConfigurationDescriptor
} from "../domain/configuration";
import {TreeNode} from "primeng/components/common/treenode";
import * as _ from "lodash";
import {OpRes} from "./jobs.service";
import {Detections} from "../domain/adf";

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

	// treeDelta(dataExtractTree : TreeNode[]) : ExtractTreeDelta {
	// 	let delta : ExtractTreeDelta = {};
	// 	let loop = function(node : TreeNode, delta : ExtractTreeDelta) {
	// 		if(node.leaf && node.data.extraction !== ExtractType.VALUE_EXTRACTED){
	// 			delta[node.data.path] = node.data.extraction
	// 		}
	// 		if(node.children){
	// 			for(let child of node.children){
	// 				loop(child, delta);
	// 			}
	// 		}
	// 	};
    //
	// 	for(let node of dataExtractTree){
	// 		loop(node, delta);
	// 	}
    //
	// 	return delta;
	// }
    //
	// extractMetrics(metrics : Metric[]){
	// 	let result : { [index : string] : MetricParam[] } = {};
	// 	for(let m of metrics){
	// 		result[m.id] = m.params;
	// 	}
	// 	return result;
	// }
    //
	// applyDelta(dataExtractTree : TreeNode[], delta : ExtractTreeDelta){
	// 	for(let node of dataExtractTree){
	// 		if(delta.hasOwnProperty(node.data.path)){
	// 			node.data.extraction = delta[node.data.path];
	// 		}
	// 		if(node.children){
	// 			this.applyDelta(node.children, delta);
	// 		}
	// 	}
	// }
    //
	// initMetrics(list : Metric[], metrics : {[index : string] : MetricParam[]}){
	// 	let result : Metric[] = [];
	// 	for(let key in metrics){
	// 		if(metrics.hasOwnProperty(key)){
	// 			let m = _.find(list, function (o) {
	// 				return o.id === key;
	// 			});
	// 			if(m){
	// 				m.params = metrics[key];
	// 				result.push(m);
	// 			}
	// 		}
	// 	}
	// 	return result;
	// }

	async getMetrics(){
		return await this.http.get<Metric[]>("api/detections").toPromise();
	}

	async getDetections(){
		return await this.http.get<Detections>("public/detections").toPromise();
	}

	async getExtractTree(){
		return await this.http.get<Field[]>("api/extract/model").toPromise();
	}

}
