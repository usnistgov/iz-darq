import { Component, OnInit, forwardRef } from '@angular/core';
import { DamWidgetComponent, IWorkspaceActive, selectWorkspaceActive, selectWorkspaceCurrentIsChanged } from '@usnistgov/ngx-dam-framework-legacy';
import { Store } from '@ngrx/store';
import { MatDialog } from '@angular/material/dialog';
import { Observable, combineLatest } from 'rxjs';
import { IReportTemplate, IReportSection } from '../../model/report-template.model';
import { selectReportTemplate, selectTableOfContentIsChanged } from '../../store/core.selectors';
import { map } from 'rxjs/operators';
import { Action as ResourceAction } from 'src/app/modules/core/model/action.enum';
import { ResourceType } from '../../../core/model/resouce-type.enum';
import { PermissionService } from '../../../core/services/permission.service';

export const RT_WIDGET_ID = 'RT_WIDGET_ID';

@Component({
  selector: 'app-report-template-widget',
  templateUrl: './report-template-widget.component.html',
  styleUrls: ['./report-template-widget.component.scss'],
  providers: [
    { provide: DamWidgetComponent, useExisting: forwardRef(() => ReportTemplateWidgetComponent) },
  ],
})
export class ReportTemplateWidgetComponent extends DamWidgetComponent implements OnInit {

  template$: Observable<IReportTemplate>;
  active$: Observable<IWorkspaceActive>;
  sections$: Observable<IReportSection[]>;
  viewOnly$: Observable<boolean>;

  constructor(
    store: Store<any>,
    dialog: MatDialog,
    private permissionService: PermissionService,
  ) {
    super(RT_WIDGET_ID, store, dialog);
    this.viewOnly$ = combineLatest([
      this.store.select(selectReportTemplate),
      this.permissionService.abilities$,
    ]).pipe(
      map(([rt, abilities]) => {
        return abilities.onResourceCant(ResourceAction.EDIT, ResourceType.REPORT_TEMPLATE, rt);
      })
    );

    this.template$ = store.select(selectReportTemplate);
    this.active$ = this.store.select(selectWorkspaceActive);
    this.sections$ = this.template$.pipe(
      map((rt) => rt.sections),
    );
  }

  containsUnsavedChanges$() {
    return combineLatest([
      this.store.select(selectWorkspaceCurrentIsChanged),
      this.store.select(selectTableOfContentIsChanged),
    ]).pipe(
      map(([ws, toc]) => {
        return ws || toc;
      })
    );
  }

  ngOnInit(): void {
  }

}
