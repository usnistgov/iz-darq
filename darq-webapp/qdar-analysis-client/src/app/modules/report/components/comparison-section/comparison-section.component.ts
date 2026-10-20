import {Component, Input, OnInit} from '@angular/core';
import {Labelizer} from '../../../shared/services/values.service';
import {IComparisonSummary, ISectionDifferential} from '../../model/comparison.model';
import {ReportComparisonService} from '../../services/report-comparison.service';

@Component({
  selector: 'app-comparison-section',
  templateUrl: './comparison-section.component.html',
  styleUrls: ['./comparison-section.component.scss']
})
export class ComparisonSectionComponent implements OnInit {

  summary: IComparisonSummary;

  @Input()
  baseLabel: string;

  @Input()
  newLabel: string;

  @Input()
  changesOnly: boolean;

  @Input()
  set section(section: ISectionDifferential) {
    this.psection = section;
    this.refresh();
  }

  get section() {
    return this.psection;
  }

  @Input()
  set labelizer(labelizer: Labelizer) {
    this.plabelizer = labelizer;
    this.refresh();
  }

  get labelizer() {
    return this.plabelizer;
  }

  private psection: ISectionDifferential;
  private plabelizer: Labelizer;

  constructor(private comparisonService: ReportComparisonService) { }

  refresh() {
    this.summary = this.psection
      ? this.comparisonService.deepSummarize([this.psection], this.plabelizer)
      : undefined;
  }

  get visible(): boolean {
    if (!this.changesOnly) {
      return true;
    }
    return this.comparisonService.sectionHasChange(this.psection, this.plabelizer);
  }

  ngOnInit(): void {
  }

}
