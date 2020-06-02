import { Component, OnInit, Input, TemplateRef } from '@angular/core';
import { Store } from '@ngrx/store';
import { DamWidgetComponent } from 'ngx-dam-framework';
import { Observable } from 'rxjs';
import { selectCurrentConfigurationIsViewOnly } from '../../store/core.selectors';

@Component({
  selector: 'app-configuration-toolbar',
  templateUrl: './configuration-toolbar.component.html',
  styleUrls: ['./configuration-toolbar.component.scss']
})
export class ConfigurationToolbarComponent implements OnInit {

  isViewOnly$: Observable<boolean>;
  @Input()
  controls: TemplateRef<any>;

  constructor(private store: Store<any>, public widget: DamWidgetComponent) {
    this.isViewOnly$ = this.store.select(selectCurrentConfigurationIsViewOnly);
  }

  ngOnInit(): void {
  }

}
