import { Component, OnInit, Input, TemplateRef } from '@angular/core';
import { Observable, combineLatest } from 'rxjs';
import { Store } from '@ngrx/store';
import { map } from 'rxjs/operators';
import { selectReportTemplate } from '../../store/core.selectors';
import { ResourceType } from '../../../core/model/resouce-type.enum';
import { Action as ResourceAction } from 'src/app/modules/core/model/action.enum';
import { PermissionService } from '../../../core/services/permission.service';

@Component({
  selector: 'app-rt-toolbar',
  templateUrl: './rt-toolbar.component.html',
  styleUrls: ['./rt-toolbar.component.scss']
})
export class RtToolbarComponent implements OnInit {

  isViewOnly$: Observable<boolean>;
  @Input()
  controls: TemplateRef<any>;

  constructor(
    private store: Store<any>,
    private permissionService: PermissionService,
  ) {
    this.isViewOnly$ = combineLatest([
      this.store.select(selectReportTemplate),
      this.permissionService.abilities$,
    ]).pipe(
      map(([rt, abilities]) => {
        return abilities.onResourceCant(ResourceAction.EDIT, ResourceType.REPORT_TEMPLATE, rt);
      })
    );
  }

  ngOnInit(): void {
  }

}
