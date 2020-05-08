import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'chartFilter'
})
export class ChartFilterPipe implements PipeTransform {

  transform(value: any[], filter : any[]): any {
  	if(!filter || filter.length === 0){
  		return value;
	}
	else {
		return value.filter(x => {
			let f = filter.filter(y => {
				console.log(y.data.field.path+" - "+x.name);
				return x.name === y.data.field.path;
			});
			console.log(f);
			return f && f.length > 0;
		});
	}
  }

}
