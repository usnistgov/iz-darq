import {CvxCodes} from "../domain/configuration";
import {Detections} from "../domain/adf";
import {Field} from "../domain/report";
import {Range} from "../domain/summary";
import {RangesService} from "./ranges.service";

export class DisplayUtil {

	constructor(private detections: Detections, private ageGroups: Range[], private cvx: CvxCodes, private codeTables: any, private $range: RangesService) {}

	stringify(field: Field, value: any) {
		switch(field) {
			case Field.DETECTION: return this.stringifyDetection(value);
			case Field.VACCINE_CODE: return this.stringifyCvx(value);
			case Field.AGE_GROUP: return this.stringifyAgeGroup(value);
			case Field.EVENT: return this.stringifyEvent(value);
			default: return value;
		}
	}

	detectionOptions(detections?: string[]) {
		const list = detections || Object.keys(this.detections);
		return this.createOptions(Field.DETECTION, list);
	}

	ageGroupOptions() {
		let i: number = 0;
		const list = this.ageGroups.map( range => (i++)+'g');
		return this.createOptions(Field.AGE_GROUP, list);
	}

	cvxOptions() {
		return this.createOptions(Field.VACCINE_CODE, Object.keys(this.cvx));
	}

	sexOptions() {
		return [
			{ value: 'M', label: 'M - Male'},
			{ value: 'F', label: 'F - Female'}
		];
	}

	eventOptions() {
		return [
			{ value: '00', label: '00 - Administered'},
			{ value: '01', label: '01 - Historical'}
		];
	}

	tableOptions(key: 'patient' | 'vaccine') {
		const list = this.codeTables[key];
		return this.createOptions(Field.TABLE, list);
	}

	private createOptions(field: Field, values: any[]) {
		const options: { value: string, label: string }[] = [];
		for(const value of values) {
			options.push({ value: value, label: this.stringify(field, value)});
		}
		return options;
	}


	private stringifyDetection(value: string): string {
		if(this.detections[value]) {
			return value + ' - ' + this.detections[value].description;
		}
		else {
			return this.unknown(value);
		}
	}

	private stringifyCvx(value: string): string {
		if(this.cvx[value]) {
			return value + ' - ' + this.cvx[value];
		}
		else {
			return this.unknown(value);
		}
	}

	private stringifyAgeGroup(value: string): string {
		const index: number = +value.replace("g", "");
		if (index < this.ageGroups.length) {
			return this.$range.print(this.ageGroups[index]);
		}
		else if(index == this.ageGroups.length) {
			if(index > 0) {
				return this.$range.barket(this.ageGroups[index - 1].max) + " -> +";
			}
			else if(index == 0) {
				return "0 -> +";
			}
		}
		else {
			return this.unknown("Age Group ID " + value);
		}
	}

	private stringifyEvent(value: string) {
		if (value === "00") return "00 - Administered";
		else if (value === "01") return "01 - Historical";
		else return this.unknown(value);
	}

	private unknown(value: string): string {
		return value + ' - Unknown';
	}
}
