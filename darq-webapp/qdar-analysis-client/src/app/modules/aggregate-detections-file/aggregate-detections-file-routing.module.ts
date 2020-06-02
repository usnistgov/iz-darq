import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdfDashboardComponent } from './components/adf-dashboard/adf-dashboard.component';
import { AdfUploadComponent } from './components/adf-upload/adf-upload.component';
import { DataLoaderGuard } from 'ngx-dam-framework';
import { LoadADFiles, CoreActionTypes, LoadADFile, LoadUserFacilities } from './store/core.actions';
import { AdfListComponent } from './components/adf-list/adf-list.component';
import { JobListComponent } from './components/job-list/job-list.component';
import { AdfSummaryComponent } from './components/adf-summary/adf-summary.component';
import { ReportListComponent } from './components/report-list/report-list.component';


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
    children: [{
      path: ':forFacility',
      component: AdfDashboardComponent,
      data: {
        routeParam: 'forFacility',
        loadAction: LoadADFiles,
        successAction: CoreActionTypes.LoadADFilesSuccess,
        failureAction: CoreActionTypes.LoadADFilesFailure,
        redirectTo: ['/', 'error'],
      },
      canActivate: [DataLoaderGuard],
      children: [
        {
          path: '',
          pathMatch: 'full',
          redirectTo: 'files',
        },
        {
          path: 'files',
          component: AdfListComponent,
        },
        {
          path: 'jobs',
          component: JobListComponent
        },
        {
          path: 'reports',
          component: ReportListComponent,
        }
      ]
    },
    {
      path: '',
      pathMatch: 'full',
      redirectTo: 'local',
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
