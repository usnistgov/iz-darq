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
	public barChartOptions:any = {
		responsive: true,
		scales: {
			yAxes: [{
				ticks: {
					beginAtZero: true,
					steps: 10,
					stepValue: 6,
					max: 60
				}
			}]
		}
	};
	cvxs : CVX[];
	public barChartLegend:boolean = true;
	ageGroups : {
		[index : string] : Range
	};
	constructor(private route: ActivatedRoute, private http: HttpClient, private $template : TemplateService) {
	}

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

	show(p : AnalysisPayloadResult){
		return Object.keys(p.values).length > 15;
	}

	flag(f : Fraction, display : Options){
		return display && display.threshold && this.percentage(f) > display.threshold;
	}

	// flag(count : number, total : number, display : Options){
	// 	return display && display.threshold && this.percentage(count, total) > display.threshold;
	// }

	percentage(f : Fraction){
		if(f.total > 0){
			return ((f.count / f.total) * 100).toFixed(2);
		}
		else
			return 0;
	}

	// percentage(count : number, total : number){
	// 	if(total > 0){
	// 		return ((count / total) * 100).toFixed(2);
	// 	}
	// 	else
	// 		return 0;
	// }

	chartData(p: AnalysisPayloadResult) {
		if (!p.groups && p.groups.length === 0) {
			return {};
		}

		let x : {
			labels: string[],
			values : number[]
		} = {
			labels : [],
			values : []
		};
		let i = 0;
		for(let g of p.groups){
			let name = "";
			if(g.length > 1){
				name = g[0].value;
				for(let i = 1; i < g.length; i++){
					name +=  "-" + g[i].value;
				}
			}
			else {
				name = g[0].value;
			}

			let value = ((p.values[i+'grp'].count / p.values[i+'grp'].total) * 100) ;
			x.labels.push(name);
			x.values.push(value);
			i++;
		}


		return x;
	}

	googleChartData(p: AnalysisPayloadResult) {
		if (!p.groups && p.groups.length === 0) {
			return {};
		}

		let x : any[][] = [ ["Label" , "Value"]];

		let i = 0;
		for(let g of p.groups){
			let name = "";
			if(g.length > 1){
				name = g[0].value;
				for(let i = 1; i < g.length; i++){
					name +=  "-" +  g[i].value;
				}
			}
			else {
				name =  g[0].value;
			}

			let value = ((p.values[i+'grp'].count / p.values[i+'grp'].total) * 100) ;
			if(value !== 0)
				x.push([name, value]);
			i++;
		}
		if(p.distribution){
			let i = -1;
			for(let n of x){
				if(i == -1){
					i = 0;
					continue;
				}
				i += n[1];
			}
			if(i < 100){
				x.push(['Other',100 - i]);
			}
		}
		return x;
	}

	max(a, b){
		if(a > b)
			return a;
		else
			return b;
	}

	ev($event){

	}

	value(f : Field, v : any){
		return this.$template.value(v, f, this.ageGroups, this.detections, this.cvxs);
	}

	async ngOnInit() {
		this.block.start('Analyzing');
		let ctrl = this;
		this.route.params.subscribe(async params => {
			ctrl.ageGroups = {};
			let tId = params['tId'];
			let fId = params['fId'];
			ctrl.result = await this.http.get<AnalysisResult>("/api/analyze/" + tId + "/" + fId).toPromise();
			for(let s of ctrl.result.sections){
				for(let r of s.results){
					r.chart = ctrl.googleChartData(r);
				}
			}
			let i = 0;
			for(let r of ctrl.result.configuration.ageGroups){
				ctrl.ageGroups[i+'g'] = r;
				i++;
			}
			ctrl.block.stop();
		});

		this.route.data.subscribe(data => {
			ctrl.detections = data['detections'];
			ctrl.cvxs = data['cvx'];
		});
	}

}
