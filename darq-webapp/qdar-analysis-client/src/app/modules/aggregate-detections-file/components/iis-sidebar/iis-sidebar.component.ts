import { Component, Input, OnInit } from '@angular/core';
import { IUserFacilityDescriptor } from '../../../facility/model/facility.model';
import { BehaviorSubject, Observable, combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';


@Component({
  selector: 'app-iis-sidebar',
  templateUrl: './iis-sidebar.component.html',
  styleUrls: ['./iis-sidebar.component.scss']
})
export class IisSidebarComponent implements OnInit {

  @Input()
  set facilities(list: IUserFacilityDescriptor[]) {
    this.facilities$.next(list);
  }

  facilities$: BehaviorSubject<IUserFacilityDescriptor[]>;
  filtered$: Observable<IUserFacilityDescriptor[]>;
  filterText$: BehaviorSubject<string>;
  filterText: string;

  constructor() {
    this.facilities$ = new BehaviorSubject([]);
    this.filterText$ = new BehaviorSubject(undefined);
    this.filtered$ = combineLatest([
      this.facilities$,
      this.filterText$,
    ]).pipe(
      map(([facilities, text]) => {
        return [...facilities].filter((elm) => !text || elm.name.includes(text));
      }),
    );
  }

  filter(text: string) {
    this.filterText$.next(text);
  }

  ngOnInit(): void {
  }

}
