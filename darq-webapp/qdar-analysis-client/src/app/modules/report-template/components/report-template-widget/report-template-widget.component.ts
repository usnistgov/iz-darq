import { Component, OnInit, forwardRef } from '@angular/core';
import { DamWidgetComponent, IWorkspaceActive, selectWorkspaceActive, selectWorkspaceCurrentIsChanged } from 'ngx-dam-framework';
import { Store } from '@ngrx/store';
import { MatDialog } from '@angular/material/dialog';
import { Observable, combineLatest } from 'rxjs';
import { IReportTemplate, IReportSection } from '../../model/report-template.model';
import { selectReportTemplate, selectTableOfContentIsChanged, selectRtIsViewOnly } from '../../store/core.selectors';
import { map } from 'rxjs/operators';

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
  ) {
    super(RT_WIDGET_ID, store, dialog);
    this.viewOnly$ = store.select(selectRtIsViewOnly);
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
