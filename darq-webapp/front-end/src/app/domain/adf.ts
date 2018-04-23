import {Summary, Range, Vocabulary} from "./summary";
import {ConfigurationDescriptor, ConfigurationPayload} from "./configuration";
/**
 * Created by hnt5 on 3/22/18.
 */

export class ADFData {
	id : string;
	name : string;
	path : string;
	owner : string;
	analysedOn : Date;
	uploadedOn : Date;
	configuration : ConfigurationPayload;
	summary : Summary;
	vocabulary : Vocabulary;
}

export class ADFDescriptor {
	id : string;
	name : string;
	path : string;
	owner : string;
	analysedOn : Date;
	uploadedOn : Date;
	compatibilities : ConfigurationDescriptor[];
}

export class Detections {
	[index : string] : {
		target : string,
		description : string
	}
}
