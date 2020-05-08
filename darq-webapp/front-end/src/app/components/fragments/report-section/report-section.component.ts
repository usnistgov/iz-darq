import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Section, AnalysisPayload, names } from '../../../domain/report';
import { MatDialog } from '@angular/material';
import { AnalysisPayloadDialogComponent } from '../analysis-payload-dialog/analysis-payload-dialog.component';
import { ConfigurationPayload, CVX } from '../../../domain/configuration';
import { Detections } from '../../../domain/adf';
import { RangesService } from '../../../services/ranges.service';
import { Range } from '../../../domain/summary';
import { TemplateService } from '../../../services/template.service';

@Component({
  selector: 'app-report-section',
  templateUrl: 'report-section.component.html',
  styleUrls: ['report-section.component.css']
})
export class ReportSectionComponent implements OnInit {

  _section: Section;
  _configuration: ConfigurationPayload;
  _detections: Detections;
  _cvxs: {};
  _cvx: CVX[];
  ageGroups: {
    [index: string]: Range
  };
  _codeSet: {
    [index: string]: string[]
  };
  names = names;

  @Output('change') change: EventEmitter<Section> = new EventEmitter();
  @Output('delete') delEv: EventEmitter<boolean> = new EventEmitter();
  constructor(public dialog: MatDialog, public $range: RangesService, public $template: TemplateService) {
    this.section = new Section();
  }

  @Input() set detections(d: Detections) {
    this._detections = d;
  }

  @Input() set section(s: Section) {
    this._section = s;
  }
  @Input() set cvx(s: CVX[]) {
    this._cvxs = {};
    this._cvx = s;
    for (const cvx of s) {
      this._cvxs[cvx.cvx] = cvx.name;
    }
  }

  @Input() set configuration(c: ConfigurationPayload) {
    this._configuration = c;
    this.ageGroups = {};
    let i = 0;
    for (const r of this._configuration.ageGroups) {
      this.ageGroups[i + 'g'] = r;
      i++;
    }
  }

  @Input() set codeset(c: {
    [index: string]: string[]
  }) {
    this._codeSet = c;
  }

  remove(i: number, list: AnalysisPayload[]) {
    list.splice(i, 1);
  }

  del() {
    this.delEv.emit();
  }

  changed() {
    this.change.emit(this._section);
  }

  print(r: string) {
    return this.$range.print(this.ageGroups[r]);
  }

  openDialog(): void {
    const ctrl = this;
    const dialogRef = this.dialog.open(AnalysisPayloadDialogComponent, {
      data: {
        names: names,
        configuration: this._configuration,
        detections: this._detections,
        cvx: this._cvxs,
        codes: this._codeSet
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        ctrl._section.payloads.push(result);
      }
    });
  }

  ngOnInit() {
  }
}
