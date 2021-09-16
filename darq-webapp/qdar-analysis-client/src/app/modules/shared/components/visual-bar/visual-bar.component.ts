import { Component, OnInit, Input } from '@angular/core';
import { IFraction } from '../../../report/model/report.model';
import { IThreshold, Comparator } from '../../../report-template/model/report-template.model';

export enum BarStyle {
  NEUTRAL = 'NEUTRAL',
  SUCCESS = 'SUCCESS',
  FAIL = 'FAILS',
}

@Component({
  selector: 'app-visual-bar',
  templateUrl: './visual-bar.component.html',
  styleUrls: ['./visual-bar.component.scss']
})
export class VisualBarComponent implements OnInit {
  hasThreshold: boolean;
  style: BarStyle;
  bar: number;
  threshold: number;
  BarStyle = BarStyle;

  @Input()
  set value(data: { value: IFraction, threshold: IThreshold, pass: boolean }) {
    this.bar = this.barWidth(data.value);
    this.hasThreshold = !!data.threshold;
    this.style = this.hasThreshold ? data.pass ? BarStyle.SUCCESS : BarStyle.FAIL : BarStyle.NEUTRAL;
    this.threshold = this.hasThreshold ? data.threshold.value : 0;
  }

  constructor() { }

  barWidth(fraction: IFraction) {
    return (fraction.count / fraction.total) * 100;
  }

  ngOnInit(): void {
  }

}
