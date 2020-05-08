import {TreeNode} from "primeng/components/common/treenode";
import {Range} from "./summary";
import {FormBuilder, FormControl, Validators} from "@angular/forms";

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
	asOf : string;

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

export class CvxCodes {
	[cvx: string]: string;
}
