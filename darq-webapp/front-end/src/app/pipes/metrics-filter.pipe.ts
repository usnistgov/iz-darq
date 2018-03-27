import { Pipe, PipeTransform } from '@angular/core';
import {TreeNode} from "primeng/components/common/treenode";
import {ExtractType, Metric} from "../domain/configuration";

@Pipe({
  name: 'metric'
})
export class MetricPipe implements PipeTransform {

	transform(items: Metric[], extract: TreeNode[]): any[] {
		if(!items) return [];
		if(!extract) return items;
		let elms :  {[index : string] : ExtractType } = {};
		this.itemize(extract, elms);
		return items.filter( it => {
			let x = it.detections.map(x => x.target).reduce(
				function(a, b) {
					return a.concat(b);
				},
				[]
			).map( target => {
				return elms.hasOwnProperty(target) && elms[target] !== ExtractType.NOT_EXTRACTED && elms[target] !== ExtractType.NOT_COLLECTED;
			})
			.reduce(
				function(a, b) {
					return a && b;
				},
				true
			);
			return x;
		});
	}

	itemize(extract: TreeNode[], acc : {[index : string] : ExtractType }){

		if(extract && extract.length > 0){
			for(let node of extract){
				acc[node.data.path] = node.data.extraction ;
				this.itemize(node.children, acc);
			}
		}

	}

}
