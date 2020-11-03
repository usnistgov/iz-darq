import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdfUploadComponent } from './components/adf-upload/adf-upload.component';
import { DataLoaderGuard, DamWidgetRoute, EditorActivateGuard, EditorDeactivateGuard } from 'ngx-dam-framework';
import {
  CoreActionTypes,
  LoadADFile,
  LoadUserFacilities,
  LoadADFDashboard,
  OpenADFListEditor,
  OpenAnalysisJobEditor,
  OpenReportsEditor
} from './store/core.actions';
import { AdfSummaryComponent } from './components/adf-summary/adf-summary.component';
import { ADF_WIDGET, AdfWidgetComponent } from './components/adf-widget/adf-widget.component';
import { FilesListEditorComponent, ADF_FILE_LIST_EDITOR_METADATA } from './components/files-list-editor/files-list-editor.component';
import { JobListEditorComponent, ANALYSIS_JOB_LIST_EDITOR_METADATA } from './components/job-list-editor/job-list-editor.component';
import { ReportListEditorComponent, REPORT_LIST_EDITOR_METADATA } from './components/report-list-editor/report-list-editor.component';
import { LoaderGuard } from '../shared/guards/loader.guard';


const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
  {
    path: ':fileId/summary',
    component: AdfSummaryComponent,
    data: {
      loadAction: LoadADFile,
      routeParam: 'fileId',
      successAction: CoreActionTypes.LoadADFileSuccess,
      failureAction: CoreActionTypes.LoadADFileFailure,
      redirectTo: ['/', 'error'],
    },
    canActivate: [DataLoaderGuard],
  },
  {
    path: 'dashboard',
    ...DamWidgetRoute({
      widgetId: ADF_WIDGET,
      loadAction: LoadADFDashboard,
      successAction: CoreActionTypes.LoadADFDashboardSuccess,
      failureAction: CoreActionTypes.LoadADFDashboardFailure,
      redirectTo: ['error'],
      component: AdfWidgetComponent,
    }, {
      canActivate: [
        LoaderGuard,
      ],
      canDeactivate: [],
    }),
    children: [{
      path: ':facility',
      children: [
        {
          path: '',
          pathMatch: 'full',
          redirectTo: 'files',
        },
        {
          path: 'files',
          component: FilesListEditorComponent,
          canActivate: [EditorActivateGuard],
          canDeactivate: [EditorDeactivateGuard],
          data: {
            editorMetadata: ADF_FILE_LIST_EDITOR_METADATA,
            onLeave: {
              saveEditor: true,
              saveTableOfContent: true,
            },
            action: OpenADFListEditor,
            idKey: 'facility',
            redirectTo: ['/', 'error'],
          },
        },
        {
          path: 'jobs',
          component: JobListEditorComponent,
          canActivate: [EditorActivateGuard],
          canDeactivate: [EditorDeactivateGuard],
          data: {
            editorMetadata: ANALYSIS_JOB_LIST_EDITOR_METADATA,
            onLeave: {
              saveEditor: true,
              saveTableOfContent: true,
            },
            action: OpenAnalysisJobEditor,
            idKey: 'facility',
            redirectTo: ['/', 'error'],
          },
        },
        {
          path: 'reports',
          component: ReportListEditorComponent,
          canActivate: [EditorActivateGuard],
          canDeactivate: [EditorDeactivateGuard],
          data: {
            editorMetadata: REPORT_LIST_EDITOR_METADATA,
            onLeave: {
              saveEditor: true,
              saveTableOfContent: true,
            },
            action: OpenReportsEditor,
            idKey: 'facility',
            redirectTo: ['/', 'error'],
          },
        },
      ]
    }],
  },
  {
    path: 'upload',
    component: AdfUploadComponent,
    data: {
      loadAction: LoadUserFacilities,
      successAction: CoreActionTypes.LoadUserFacilitiesSuccess,
      failureAction: CoreActionTypes.LoadUserFacilitiesFailure,
      redirectTo: ['/', 'error'],
    },
    canActivate: [DataLoaderGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AggregateDetectionsFileRoutingModule { }
