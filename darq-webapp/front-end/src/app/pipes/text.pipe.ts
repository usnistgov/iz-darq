import { Pipe, PipeTransform } from '@angular/core';
// import {Issue} from "../deprecated/qda-result/qda-result.component";

@Pipe({
  name: 'text'
})
export class TextPipe implements PipeTransform {

	transform(items: any[], searchText: string): any[] {
		if(!items) return [];
		if(!searchText) return items;
		// searchText = searchText.toLowerCase();
		// return items.filter( it => {
		// 	return it.code.toLowerCase().includes(searchText) || it.message.toLowerCase().includes(searchText);
		// });
	}

}
