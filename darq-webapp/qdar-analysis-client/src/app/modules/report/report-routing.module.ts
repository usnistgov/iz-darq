import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DamWidgetRoute, EditorActivateGuard, EditorDeactivateGuard} from '@usnistgov/ngx-dam-framework-legacy';
import {REPORT_WIDGET, ReportWidgetComponent} from './components/report-widget/report-widget.component';
import {CoreActionTypes, LoadReport, OpenReportEditor} from './store/core.actions';
import {REPORT_EDITOR_METADATA, ReportEditorComponent} from './components/report-editor/report-editor.component';
import {LoaderGuard} from '../shared/guards/loader.guard';


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
  }, {
    canActivate: [
      LoaderGuard,
    ],
    canDeactivate: [],
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
