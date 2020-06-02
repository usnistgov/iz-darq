import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { IRange, IBracket } from '../../model/age-group.model';
import { AgeGroupService } from '../../services/age-group.service';
import { FormGroup, Validators, FormControl } from '@angular/forms';

@Component({
  selector: 'app-age-groups',
  templateUrl: './age-groups.component.html',
  styleUrls: ['./age-groups.component.scss']
})
export class AgeGroupsComponent implements OnInit {

  @Input()
  viewOnly: boolean;

  @Output()
  valueChange: EventEmitter<IRange[]>;

  @Input()
  set ageGroups(ranges: IRange[]) {
    this.ranges = [...ranges];
    this.openBracket = this.agService.openBracket(ranges);
    this.sizes = this.ranges.map((r) => this.agService.rangeWeight(r));
    this.maxSize = Math.max(...this.sizes);
  }

  years: number;
  months: number;
  days: number;
  form: FormGroup;
  ranges: IRange[];
  sizes: number[];
  maxSize: number;
  openBracket: IBracket;

  constructor(private agService: AgeGroupService) {
    this.valueChange = new EventEmitter<IRange[]>();
    this.form = new FormGroup({
      year: new FormControl(0, [Validators.required, Validators.min(0)]),
      month: new FormControl(0, [Validators.required, Validators.min(0)]),
      day: new FormControl(0, [Validators.required, Validators.min(0)]),
    });
  }

  addBracket() {
    const rangeSize = this.ranges.length;
    const bracket: IBracket = this.form.getRawValue();
    this.ageGroups = this.agService.insertBracket(this.ranges, bracket);
    this.triggerChange(rangeSize);
  }

  remove(i: number) {
    const rangeSize = this.ranges.length;
    this.ageGroups = this.agService.removeBracket(this.ranges, i);
    this.triggerChange(rangeSize);
  }

  triggerChange(previousSize: number) {
    if (previousSize !== this.ranges.length) {
      this.valueChange.emit(this.ranges);
    }
  }

  rangeWidth(i: number) {
    return (this.sizes[i] / this.maxSize) * 100;
  }

  ngOnInit(): void {
  }

}
