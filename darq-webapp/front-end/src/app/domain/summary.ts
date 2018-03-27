/**
 * Created by hnt5 on 3/20/18.
 */
export class Summary {
	extract : {
		[index : string] : number
	};
	counts : {
		totalReadVaccinations : number;
		totalReadPatientRecords : number;
		totalSkippedPatientRecords : number;
		maxVaccinationsPerRecord : number;
		minVaccinationsPerRecord : number;
		numberOfProviders : number;
		avgVaccinationsPerRecord : number;
		maxVaccinationsPerProvider : number;
		minVaccinationsPerProvider : number;
		avgVaccinationsPerProvider : number;
	};
	outOfRange : number;
	countByAgeGroup : {
		range : Range,
		nb : number;
	}[];
}

export class Range {
	min : Barket;
	max : Barket;
}

export class Barket {
	year : number;
	month : number;
	day : number;

	constructor(){
		this.year = 0;
		this.month = 0;
		this.day = 0;
	}
}
