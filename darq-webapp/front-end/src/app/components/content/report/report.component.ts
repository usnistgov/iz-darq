import {Component, OnInit, ViewChild, TemplateRef} from '@angular/core';
import {BlockUI, NgBlockUI} from "ng-block-ui";
import {ActivatedRoute} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {AnalysisResult, AnalysisPayloadResult} from "../../../domain/report";
import {Detections} from "../../../domain/adf";

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
		scaleShowVerticalLines: false,
		responsive: true
	};
	public barChartType:string = 'bar';
	public barChartLegend:boolean = true;

	constructor(private route: ActivatedRoute, private http: HttpClient) {
	}

	template(p: AnalysisPayloadResult) {
		if (p.groups && p.groups.length > 0) {
			return this.chart;
		}
		else {
			return this.indicator;
		}
	}

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
				name = "[ ";
				for(let x of g){
					name += "( " + x.field + ' : ' + x.value + " ) ";
				}
				name +="]";
			}
			else {
				name = g[0].value;
			}

			let value = ((p.values[i+'grp'].count / p.values[i+'grp'].total) * 100);
			x.labels.push(name);
			x.values.push(value);
			// x.push({ name, value});
			i++;
		}
		// console.log(x);
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



	async ngOnInit() {
		this.block.start('Analyzing');
		let ctrl = this;
		this.route.params.subscribe(async params => {
			let tId = params['tId'];
			let fId = params['fId'];
			ctrl.result = await this.http.get<AnalysisResult>("/api/analyze/" + tId + "/" + fId).toPromise();
			for(let s of ctrl.result.sections){
				for(let r of s.results){
					r.chart = ctrl.chartData(r);
				}
			}
			console.log("RESULT");
			console.log(ctrl.result);
			ctrl.block.stop();
		});

		this.route.data.subscribe(data => {
			ctrl.detections = data['detections'];
			console.log(Object.keys(ctrl.detections).length);
		});
	}

}
