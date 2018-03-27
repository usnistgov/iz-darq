import { Injectable } from '@angular/core';
import {Range} from "../domain/summary";

@Injectable()
export class RangesService {

  constructor() { }

	barket(b : any){
		if(b.year === 0 && b.day === 0 && b.month === 0) return "0 day";
		return this.str(b.year,"year")+this.str(b.month,"month")+this.str(b.day,"day");
	}

	str(i : number, str : string){
		return i > 0 ? i + " " + str + (i > 1 ? "s " : " ") : "";
	}

	max(r : Range[]){
		return this.barket(r[r.length - 1].max);
	}

	print(r : Range){
		return this.barket(r.min) +" -> "+ this.barket(r.max);
	}

}
