import { Component, OnInit, forwardRef } from '@angular/core';
import { DamWidgetComponent, IDamDataModel } from '@usnistgov/ngx-dam-framework-legacy';
import { Store } from '@ngrx/store';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { AdminTabs } from '../admin-sidebar/admin-sidebar.component';
import { selectAdminActiveTab } from '../../store/core.selectors';

export const ADMIN_WIDGET = 'ADMIN_WIDGET';

@Component({
  selector: 'app-admin-widget',
  templateUrl: './admin-widget.component.html',
  styleUrls: ['./admin-widget.component.scss'],
  providers: [
    { provide: DamWidgetComponent, useExisting: forwardRef(() => AdminWidgetComponent) },
  ],
})
export class AdminWidgetComponent extends DamWidgetComponent implements OnInit {

  // activeTab
  activeTab$: Observable<AdminTabs>;

  constructor(
    store: Store<IDamDataModel>,
    dialog: MatDialog,
  ) {
    super(ADMIN_WIDGET, store, dialog);
    this.activeTab$ = store.select(selectAdminActiveTab);
  }

  ngOnInit(): void {
  }

}
