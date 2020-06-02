import { Component, OnInit, Input } from '@angular/core';
import { IFraction } from '../../../report/model/report.model';
import { IThreshold, Comparator } from '../../../report-template/model/report-template.model';

export interface IBarArea {
  width: number;
  danger: boolean;
  value: number;
  barWidth: number;
}

@Component({
  selector: 'app-visual-bar',
  templateUrl: './visual-bar.component.html',
  styleUrls: ['./visual-bar.component.scss']
})
export class VisualBarComponent implements OnInit {

  @Input()
  showValue: boolean;
  hasThreshold;

  @Input()
  set value(data: { value: IFraction, threshold: IThreshold }) {
    const barWidth = this.barWidth(data.value);
    const threshold = data.threshold || {
      comparator: Comparator.GT,
      value: 0,
    };
    this.hasThreshold = !!data.threshold;

    this.start = {
      width: threshold.value,
      danger: threshold.comparator === Comparator.GT,
      value: barWidth > threshold.value ? threshold.value : barWidth,
      barWidth: barWidth > threshold.value ? 100 : this.scale(threshold.value, barWidth),
    };

    this.end = {
      width: 100 - threshold.value,
      danger: threshold.comparator === Comparator.LT,
      value: barWidth < threshold.value ? 0 : barWidth - threshold.value,
      barWidth: barWidth < threshold.value ? 0 : this.scale(100 - threshold.value, barWidth - threshold.value),
    };
  }

  start: IBarArea;
  end: IBarArea;

  constructor() { }

  barWidth(fraction: IFraction) {
    return (fraction.count / fraction.total) * 100;
  }

  scale(area: number, value: number) {
    return 100 / (area / value);
  }

  ngOnInit(): void {
  }

}
