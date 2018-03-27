import { Pipe, PipeTransform } from '@angular/core';
import {Barket} from "../domain/summary";

@Pipe({
  name: 'ageGroups'
})
export class AgeGroupsPipe implements PipeTransform {

  transform(value: Array<Barket>): any {
  	console.log(value);
    value.sort((a: Barket, b: Barket) => {
		let dec  = a.year * 10000 + a.month * 100 + a.day * 10;
		let odec = b.year * 10000 + b.month * 100 + b.day * 10;
		return dec - odec;
	});
    return value;
  }

}
