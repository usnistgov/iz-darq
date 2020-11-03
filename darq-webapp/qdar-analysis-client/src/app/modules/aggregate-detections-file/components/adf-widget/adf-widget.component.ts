import { Component, OnInit, forwardRef, AfterViewInit } from '@angular/core';
import { DamWidgetComponent, IDamDataModel, selectRouteParams, TurnOffLoader } from 'ngx-dam-framework';
import { Store } from '@ngrx/store';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { IUserFacilityDescriptor } from '../../../facility/model/facility.model';
import { selectCurrentFacility, selectUserFacilitiesSorted } from '../../store/core.selectors';
import { map } from 'rxjs/operators';

export const ADF_WIDGET = 'ADF_WIDGET';

@Component({
  selector: 'app-adf-widget',
  templateUrl: './adf-widget.component.html',
  styleUrls: ['./adf-widget.component.scss'],
  providers: [
    { provide: DamWidgetComponent, useExisting: forwardRef(() => AdfWidgetComponent) },
  ],
})
export class AdfWidgetComponent extends DamWidgetComponent implements OnInit, AfterViewInit {

  facilities$: Observable<IUserFacilityDescriptor[]>;
  currentEditorFacility$: Observable<IUserFacilityDescriptor>;
  facilityIdFromURL$: Observable<string>;

  constructor(
    store: Store<IDamDataModel>,
    dialog: MatDialog,
  ) {
    super(ADF_WIDGET, store, dialog);
    this.facilities$ = store.select(selectUserFacilitiesSorted);
    this.currentEditorFacility$ = this.store.select(selectCurrentFacility);
    this.facilityIdFromURL$ = this.store.select(selectRouteParams).pipe(
      map((p) => p['facility']),
    );
  }

  ngAfterViewInit(): void {
    this.store.dispatch(new TurnOffLoader());
  }

  ngOnInit(): void {
  }

}

