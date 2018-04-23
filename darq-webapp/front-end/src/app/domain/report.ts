import {Configuration, ConfigurationDescriptor, ConfigurationPayload} from "./configuration";
/**
 * Created by hnt5 on 3/25/18.
 */
export class ReportDescriptor {
	id : string;
	name : string;
	owner : string;
	compatibilities : ConfigurationDescriptor[]

	constructor(){
		this.id = "";
		this.name = "";
		this.owner = "";
		this.compatibilities = [];
	}
}
export class ReportTemplate {
	id : string;
	name : string;
	description : string;
	sections : Section[];
	configuration : ConfigurationPayload;

	constructor(){
		this.id = "";
		this.name = "";
		this.description = "";
		this.sections = [];
	}
}
export class Section {
	title : string;
	description : string;
	payloads : AnalysisPayload[];

	constructor() {
		this.title = "";
		this.description = "";
		this.payloads = [];
	}
}
export class AnalysisPayload {
	type : CG;
	filters : FieldValue[];
	groupBy : Field[];
	groupFilters : {
		[index : string] : any
	}[];
	options : Options;

	constructor(){
		this.type = null;
		this.filters = [];
		this.groupBy = [];
		this.groupFilters = [];
		this.options = {
			threshold : 0,
			chartType : 'bar',
			countType : 'per'
		};
	}
}

export class Options {
	threshold : number;
	chartType : string;
	countType : string;
}

export class FieldValue {
	field : Field;
	value : string;
}

export enum CG {
	V="V",
	VD="VD",
	VT="VT",
	PD="PD",
	PT="PT"
}
export enum Field {
	PROVIDER="PROVIDER",
	AGE_GROUP="AGE_GROUP",
	TABLE="TABLE",
	CODE="CODE",
	DETECTION="DETECTION",
	EVENT="EVENT",
	GENDER="GENDER",
	VACCINE_CODE="VACCINE_CODE",
	VACCINATION_YEAR="VACCINATION_YEAR"
}

export interface Compatibility  {
	[index : string] : Field[];
};

export let _comp : Compatibility = {
	V : [Field.PROVIDER, Field.AGE_GROUP, Field.EVENT, Field.GENDER, Field.VACCINATION_YEAR, Field.VACCINE_CODE],
	VD : [Field.PROVIDER, Field.AGE_GROUP, Field.DETECTION],
	VT : [Field.PROVIDER, Field.AGE_GROUP, Field.TABLE, Field.CODE],
	PD : [Field.AGE_GROUP, Field.DETECTION],
	PT : [Field.AGE_GROUP, Field.TABLE, Field.CODE]
};

export let names = {
	V : "Vaccination Events",
	VD : "Vaccination Related Detections",
	VT : "Vaccination Related Code Table",
	PD : "Patient Related Detections",
	PT : "Patient Related Code Table"
};

export class AnalysisResult {
	adfName : string;
	configuration : ConfigurationPayload;
	name : string;
	description : string;
	sections : AnalysisSectionResult[];
}

export class AnalysisSectionResult {
	title : string;
	description : string;
	results : AnalysisPayloadResult[];
}

export class AnalysisPayloadResult {
	type : CG;
	filters : FieldValue[];
	groups : FieldValue[][];
	values : {
		[index : string] : Fraction
	};
	distribution : boolean;
	display : Options;
	chart : any;
}

export class Fraction {
	count : number;
	total : number;
}
