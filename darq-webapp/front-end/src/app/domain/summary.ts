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

export class Vocabulary {
	byField : {
		[index : string] : string[]
	};
	byTable : {
		[index : string] : string[]
	};
}

export class Range {
	min : Barket;
	max : Barket;

	constructor(){

	}

	public toString = () : string  => {
		return this.min.toString() +" -> "+this.max.toString();
	}
}

export class Barket {
	year : number = 0;
	month : number = 0;
	day : number = 0;


	constructor(y? : number, m? : number, d? : number){
		this.year = y;
		this.month = m;
		this.day = d;
	}
    //
	public toString = () : string  => {
		if(this.year === 0 && this.day === 0 && this.month === 0) return "0 day";
		return this.str(this.year,"year")+this.str(this.month,"month")+this.str(this.day,"day");
	}

	str(i : number, str : string){
		return i > 0 ? i + " " + str + (i > 1 ? "s " : " ") : "";
	}
}
