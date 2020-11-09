import { Component, OnInit, Input, TemplateRef } from '@angular/core';
import { Store } from '@ngrx/store';
import { DamWidgetComponent } from 'ngx-dam-framework';
import { Observable, combineLatest } from 'rxjs';
import { selectCurrentConfiguration } from '../../store/core.selectors';
import { ResourceType } from '../../../core/model/resouce-type.enum';
import { Action } from '../../../core/model/action.enum';
import { map } from 'rxjs/operators';
import { PermissionService } from '../../../core/services/permission.service';

@Component({
  selector: 'app-configuration-toolbar',
  templateUrl: './configuration-toolbar.component.html',
  styleUrls: ['./configuration-toolbar.component.scss']
})
export class ConfigurationToolbarComponent implements OnInit {

  isViewOnly$: Observable<boolean>;
  @Input()
  controls: TemplateRef<any>;

  constructor(
    private permissionService: PermissionService,
    private store: Store<any>,
    public widget: DamWidgetComponent) {
    this.isViewOnly$ = combineLatest([
      this.store.select(selectCurrentConfiguration),
      this.permissionService.abilities$,
    ]).pipe(
      map(([configuration, abilities]) => {
        return abilities.onResourceCant(Action.EDIT, ResourceType.CONFIGURATION, configuration) || configuration.locked;
      })
    );
  }

  ngOnInit(): void {
  }

}
