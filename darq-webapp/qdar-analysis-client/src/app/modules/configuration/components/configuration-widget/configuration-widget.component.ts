import { Component, OnInit, forwardRef } from '@angular/core';
import { DamWidgetComponent, IDamDataModel, selectWorkspaceActive } from 'ngx-dam-framework';
import { Store } from '@ngrx/store';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { IConfigurationDescriptor } from '../../model/configuration.model';
import { selectConfigurations } from '../../store/core.selectors';
import { pluck, filter } from 'rxjs/operators';

export const CONFIG_WIDGET = 'CONFIG_WIDGET';

@Component({
  selector: 'app-configuration-widget',
  templateUrl: './configuration-widget.component.html',
  styleUrls: ['./configuration-widget.component.scss'],
  providers: [
    { provide: DamWidgetComponent, useExisting: forwardRef(() => ConfigurationWidgetComponent) },
  ],
})
export class ConfigurationWidgetComponent extends DamWidgetComponent implements OnInit {

  configurations$: Observable<IConfigurationDescriptor[]>;
  active$: Observable<IConfigurationDescriptor>;

  constructor(store: Store<IDamDataModel>, dialog: MatDialog) {
    super(CONFIG_WIDGET, store, dialog);
    this.configurations$ = this.store.select(selectConfigurations);
    this.active$ = this.store.select(selectWorkspaceActive).pipe(
      filter(a => !!a),
      pluck('display'),
    );
  }

  ngOnInit(): void {
  }

}
