import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Labelizer } from '../../../shared/services/values.service';
import { IReportSectionResult } from '../../model/report.model';
import { Field } from '../../../report-template/model/analysis.values';

@Component({
  selector: 'app-report-section',
  templateUrl: './report-section.component.html',
  styleUrls: ['./report-section.component.scss']
})
export class ReportSectionComponent implements OnInit {

  fields = Field;

  @Input()
  set value(v: IReportSectionResult) {
    this.pval = this.cloneAndChildren(v);
  }

  get value() {
    return this.pval;
  }

  @Input()
  filtered: IReportSectionResult;

  @Output()
  valueChange: EventEmitter<IReportSectionResult>;

  @Input()
  labelizer: Labelizer;
  editMode: boolean;
  pval: IReportSectionResult;

  @Input()
  viewOnly: boolean;

  constructor() {
    this.valueChange = new EventEmitter();
  }

  commentChange() {
    this.valueChange.emit(this.value);
  }

  childChange(child: IReportSectionResult, i: number) {
    const clone = this.cloneAndChildren(this.value);
    clone.children.splice(i, 1, this.cloneAndChildren(child));
    this.valueChange.emit(clone);
  }

  cloneAndChildren(section: IReportSectionResult) {
    return {
      ...section,
      children: [
        ...section.children.map((child) => {
          return {
            ...child,
          };
        })
      ],
    };
  }

  comment() {
    this.editMode = !this.editMode;
  }

  ngOnInit(): void {
  }

}
