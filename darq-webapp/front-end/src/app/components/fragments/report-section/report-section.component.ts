import {Component, OnInit, Input, EventEmitter, Output} from '@angular/core';
import {Section, AnalysisPayload} from "../../../domain/report";
import {MatDialog} from "@angular/material";
import {AnalysisPayloadDialogComponent} from "../analysis-payload-dialog/analysis-payload-dialog.component";
import {ConfigurationPayload} from "../../../domain/configuration";
import {Detections} from "../../../domain/adf";
import {RangesService} from "../../../services/ranges.service";
import {Range} from "../../../domain/summary";

@Component({
	selector: 'app-report-section',
	templateUrl: 'report-section.component.html',
	styleUrls: ['report-section.component.css']
})
export class ReportSectionComponent implements OnInit {

	_section : Section;
	_configuration : ConfigurationPayload;
	_detections : Detections;
	names = {
		V : "Vaccination Events",
		VD : "Vaccination Related Detections",
		VT : "Vaccination Related Code Table",
		PD : "Patient Related Detections",
		PT : "Patient Related Code Table"
	};
	ageGroups : {
		[index : string] : Range
	};

	@Output("change") change : EventEmitter<Section> = new EventEmitter();
	constructor(public dialog: MatDialog, public $range : RangesService) {
		this.section = new Section();
	}

	@Input() set detections(d : Detections){
		this._detections = d;
	}

	@Input() set section(s : Section){
		this._section = s;
	}

	@Input() set configuration(c : ConfigurationPayload){
		this._configuration = c;
		this.ageGroups = {};
		let i = 0;
		for(let r of this._configuration.ageGroups){
			this.ageGroups[i+'g'] = r;
			i++;
		}
	}

	remove(i : number, list : AnalysisPayload[]){
		list.splice(i,1);
	}

	changed(){
		this.change.emit(this._section);
	}

	print(r : string){
		return this.$range.print(this.ageGroups[r]);
	}

	openDialog(): void {
		let ctrl = this;
		let dialogRef = this.dialog.open(AnalysisPayloadDialogComponent, {
			data: {
				names : ctrl.names,
				configuration : this._configuration,
				detections : this._detections
			}
		});

		dialogRef.afterClosed().subscribe(result => {
			if(result){
				ctrl._section.payloads.push(result);
			}
		});
	}

	ngOnInit() {
	}
}
