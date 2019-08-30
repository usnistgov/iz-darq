import {Component, OnInit, ViewChild, TemplateRef} from '@angular/core';
import {BlockUI, NgBlockUI} from "ng-block-ui";
import {ActivatedRoute} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {AnalysisResult, AnalysisPayloadResult, CG, names, Field, Options, Fraction} from "../../../domain/report";
import {Detections} from "../../../domain/adf";
import {TemplateService} from "../../../services/template.service";
import {Range} from "../../../domain/summary";
import {CVX} from "../../../domain/configuration";

@Component({
	selector: 'app-report',
	templateUrl: './report.component.html',
	styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {
	@BlockUI('report') block: NgBlockUI;

	result: AnalysisResult;
	@ViewChild('indicator')
	indicator: TemplateRef<any>;
	@ViewChild('chart')
	chart: TemplateRef<any>;
	detections: Detections;
	cvxs : CVX[];
	ageGroups : {
		[index : string] : Range
	};
	constructor(private route: ActivatedRoute, private http: HttpClient, private $template : TemplateService) {}

	template(p: AnalysisPayloadResult) {
		if (p.groups && p.groups.length > 0) {
			return this.chart;
		}
		else {
			return this.indicator;
		}
	}

	typeName(cg : CG){
		return names[cg];
	}

	flag(f : Fraction, display : Options){
		return display && display.threshold >= 0 &&  display.threshold <= 100 && this.percentage(f) > display.threshold;
	}

	percentage(f : Fraction){
		if(f.total > 0){
			return ((f.count / f.total) * 100).toFixed(2);
		}
		else
			return 0;
	}

	analysisTableData(p: AnalysisPayloadResult){
		let results : {
			headers : string[],
			rows : any[]
		} = {
			headers : [],
			rows : []
		};

		let j = 0;
		for(let g of p.groups){
			let dummy : {
				[index : string] : any;
			} = {};
			for(let i = 0; i < g.fields.length; i++){
				if(results.headers.indexOf(g.fields[i].field) === -1)
					results.headers.push(g.fields[i].field);
				dummy[g.fields[i].field] = g.fields[i].value;
			}
			dummy.value = this.percentage(p.values[j+'grp']);
			dummy.threshold = g.threshold === -1 ? NaN :  g.threshold;
			dummy.fraction = p.values[j+'grp'];
			dummy.flag = isNaN(dummy.threshold) ? 'NONE' : (this.percentage(p.values[j+'grp']) > dummy.threshold ? 'ABOVE' : 'BELOW');
			p.hasOverThreshold = p.hasOverThreshold || dummy.flag === 'ABOVE';
			results.rows.push(dummy);
			j++;
		}

		if((!p.groups || p.groups.length === 0) && p.values['indicator']){
			let dummy : {
				[index : string] : any;
			} = {};
			dummy.value = this.percentage(p.values['indicator']);
			dummy.threshold = p.display.threshold	 === -1 ? NaN :  p.display.threshold;
			dummy.fraction = p.values['indicator'];
			dummy.flag = isNaN(dummy.threshold) ? 'NONE' : (dummy.value > dummy.threshold ? 'ABOVE' : 'BELOW');
			results.rows.push(dummy);
			p.hasOverThreshold = p.hasOverThreshold || dummy.flag === 'ABOVE';
		}

		return results;
	}

	max(a, b) {
		if(a > b)
			return a;
		else
			return b;
	}

	value(f : Field, v : any) {
		return this.$template.value(v, f, this.ageGroups, this.detections, this.cvxs);
	}

	async ngOnInit() {
		let ctrl = this;
		this.route.params.subscribe(async params => {
			ctrl.ageGroups = {};
			let tId = params['tId'];
			let fId = params['fId'];
			ctrl.result = await this.http.get<AnalysisResult>("/api/analyze/" + tId + "/" + fId).toPromise();
			let i = 0;
			for(let r of ctrl.result.configuration.ageGroups){
				ctrl.ageGroups[i+'g'] = r;
				i++;
			}
			for(let s of ctrl.result.sections){
				for(let r of s.results){
					r.table = ctrl.analysisTableData(r);
					s.hasOverThreshold = s.hasOverThreshold || r.hasOverThreshold;
				}
			}
			ctrl.block.stop();
		});

		this.route.data.subscribe(data => {
			ctrl.detections = data['detections'];
			ctrl.cvxs = data['cvx'];
		});
	}

}
