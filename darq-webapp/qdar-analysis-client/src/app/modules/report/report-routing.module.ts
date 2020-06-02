import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DamWidgetRoute, AuthenticatedGuard, EditorActivateGuard, EditorDeactivateGuard } from 'ngx-dam-framework';
import { REPORT_WIDGET, ReportWidgetComponent } from './components/report-widget/report-widget.component';
import { LoadReport, CoreActionTypes, OpenReportEditor } from './store/core.actions';
import { ReportEditorComponent, REPORT_EDITOR_METADATA } from './components/report-editor/report-editor.component';


const routes: Routes = [{
  path: ':reportId',
  ...DamWidgetRoute({
    widgetId: REPORT_WIDGET,
    routeParam: 'reportId',
    loadAction: LoadReport,
    successAction: CoreActionTypes.LoadReportSuccess,
    failureAction: CoreActionTypes.LoadReportFailure,
    redirectTo: ['error'],
    component: ReportWidgetComponent,
  }),
  children: [
    {
      path: '',
      pathMatch: 'full',
      component: ReportEditorComponent,
      canActivate: [EditorActivateGuard],
      canDeactivate: [EditorDeactivateGuard],
      data: {
        editorMetadata: REPORT_EDITOR_METADATA,
        onLeave: {
          saveEditor: true,
          saveTableOfContent: true,
        },
        action: OpenReportEditor,
        idKey: 'reportId',
        redirectTo: ['/', 'error'],
      },
    }
  ]
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportRoutingModule { }
