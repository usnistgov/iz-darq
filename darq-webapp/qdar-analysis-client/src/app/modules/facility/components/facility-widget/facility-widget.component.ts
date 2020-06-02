import { Component, OnInit, forwardRef } from '@angular/core';
import { DamWidgetComponent, IDamDataModel, selectWorkspaceActive } from 'ngx-dam-framework';
import { Store } from '@ngrx/store';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { IFacility, IFacilityDescriptor } from '../../model/facility.model';
import { selectFacilities } from '../../store/core.selectors';
import { filter, pluck } from 'rxjs/operators';

export const FACILITY_WIDGET = 'FACILITY_WIDGET';


@Component({
  selector: 'app-facility-widget',
  templateUrl: './facility-widget.component.html',
  styleUrls: ['./facility-widget.component.scss'],
  providers: [
    { provide: DamWidgetComponent, useExisting: forwardRef(() => FacilityWidgetComponent) },
  ],
})
export class FacilityWidgetComponent extends DamWidgetComponent implements OnInit {

  facilities$: Observable<IFacilityDescriptor[]>;
  active$: Observable<IFacilityDescriptor>;

  constructor(store: Store<IDamDataModel>, dialog: MatDialog) {
    super(FACILITY_WIDGET, store, dialog);
    this.facilities$ = this.store.select(selectFacilities);
    this.active$ = this.store.select(selectWorkspaceActive).pipe(
      filter(a => !!a),
      pluck('display'),
    );
  }

  ngOnInit(): void {
  }

}
