import {TreeNode} from "primeng/components/common/treenode";
import {Range} from "./summary";

export class Configuration {
	id? : string;
	name : string;
	description : string;
	payload : ConfigurationPayload;
	vonly : boolean;
	owner : string;

	constructor(){
		this.name = "";
		this.vonly = false;
		this.description = "";
		this.payload = new ConfigurationPayload();
		this.owner = "";
	}
}

export class ConfigurationPayload {
	ageGroups : Range[];
	detections : string[];

	constructor(){
		this.ageGroups = [];
		this.detections = [];
	}
}

export class CVX {
	name : string;
	cvx : string;
}

export interface ConfigurationDescriptor {
	id : string,
	name : string,
	owner : string;
	lastUpdated : Date;
}

export interface ExtractTreeDelta {
	[index : string] : ExtractType
}

export class Metric {
	id : string;
	description : string;
	category : MetricCategory;
	detections : {
		code : string;
		target : string[];
		description : string;
		type : string;
	}[];
	params? : MetricParam[];
}

export interface MetricParam {
	name : string;
	type : ParamType;
	value : any;
}

export class Field {
	name: string;
	path: string;
	children: Field[] = [];
	extraction: ExtractType = ExtractType.VALUE_EXTRACTED;

	constructor(f : Field){
		this.name = f.name;
		this.path = f.path;
		this.extraction = ExtractType.VALUE_EXTRACTED;
		if(f.children){
			for(let c of f.children){
				this.children.push(new Field(c));
			}
		}
	}

	public asNode(): TreeNode {
		return {
			label: this.name,
			data: this,
			children: this.children.map(x => x.asNode()),
			leaf: this.children.length == 0
		}
	}
}

export enum ExtractType {
	NOT_COLLECTED = "NOT_COLLECTED",
	NOT_EXTRACTED = "NOT_EXTRACTED",
	VALUE_EXTRACTED = "VALUE_EXTRACTED",
	METADATA_EXTRACTED = "METADATA_EXTRACTED"
}

export enum MetricCategory {
	COMPLETENESS = "COMPLETENESS",
	ACCURACY = "ACCURACY"
}

export enum ParamType {
	PERCENTAGE = "Percentage",
	DISTRIBUTION = "Distribution"
}
