import {Component, Input, OnInit} from '@angular/core';
import {IStatDifferential} from '../../model/adf-comparison.model';

@Component({
  selector: 'app-stat-delta',
  templateUrl: './stat-delta.component.html',
  styleUrls: ['./stat-delta.component.scss']
})
export class StatDeltaComponent implements OnInit {

  @Input()
  value: IStatDifferential;

  /**
   * Renders the values as percentages, matching the extract table of the summary page.
   */
  @Input()
  percent = false;

  /**
   * Number of decimals for the raw values; deltas always keep one decimal when
   * fractional so a small change is not rounded away to zero.
   */
  @Input()
  digits = '1.0-2';

  constructor() { }

  get incomparable(): boolean {
    return !this.value || this.value.missingInBase || this.value.missingInNew;
  }

  get delta(): number {
    return this.value ? this.value.delta : undefined;
  }

  get positive(): boolean {
    return this.delta > 0;
  }

  get negative(): boolean {
    return this.delta < 0;
  }

  ngOnInit(): void {
  }

}
