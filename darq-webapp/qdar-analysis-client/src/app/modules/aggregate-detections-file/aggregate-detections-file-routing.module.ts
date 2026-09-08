import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AdfUploadComponent} from './components/adf-upload/adf-upload.component';
import {
  DamWidgetRoute,
  DataLoaderGuard,
  EditorActivateGuard,
  EditorDeactivateGuard
} from '@usnistgov/ngx-dam-framework-legacy';
import {
  CoreActionTypes,
  LoadADFDashboard,
  LoadADFile,
  LoadUserFacilities,
  OpenADFListEditor,
  OpenAnalysisJobEditor,
  OpenMergeJobEditor,
  OpenReportsEditor
} from './store/core.actions';
import {AdfSummaryComponent} from './components/adf-summary/adf-summary.component';
import {ADF_WIDGET, AdfWidgetComponent} from './components/adf-widget/adf-widget.component';
import {
  ADF_FILE_LIST_EDITOR_METADATA,
  FilesListEditorComponent
} from './components/files-list-editor/files-list-editor.component';
import {
  ANALYSIS_JOB_LIST_EDITOR_METADATA,
  JobListEditorComponent
} from './components/job-list-editor/job-list-editor.component';
import {
  REPORT_LIST_EDITOR_METADATA,
  ReportListEditorComponent
} from './components/report-list-editor/report-list-editor.component';
import {LoaderGuard} from '../shared/guards/loader.guard';
import {
  MERGE_JOB_LIST_EDITOR_METADATA,
  MergeJobListEditorComponent
} from './components/merge-job-list-editor/merge-job-list-editor.component';


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
          path: 'analysis-jobs',
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
          path: 'merge-jobs',
          component: MergeJobListEditorComponent,
          canActivate: [EditorActivateGuard],
          canDeactivate: [EditorDeactivateGuard],
          data: {
            editorMetadata: MERGE_JOB_LIST_EDITOR_METADATA,
            onLeave: {
              saveEditor: true,
              saveTableOfContent: true,
            },
            action: OpenMergeJobEditor,
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
