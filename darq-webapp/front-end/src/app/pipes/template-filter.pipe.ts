import { Pipe, PipeTransform } from '@angular/core';
import {ReportDescriptor} from "../domain/report";
import {ConfigurationDescriptor} from "../domain/configuration";

@Pipe({
  name: 'templateFilter'
})
export class TemplateFilterPipe implements PipeTransform {

  transform(value: ReportDescriptor[], arg: ConfigurationDescriptor): any {
  	if(!arg){
  		return value;
	}
  	return value.filter(x => {
  		return x.compatibilities.filter(y => {
  			return y.id === arg.id;
		}).length > 0;
	});
  }

}
