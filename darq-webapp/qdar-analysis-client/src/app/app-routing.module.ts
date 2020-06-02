import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent, AuthenticatedGuard, NotAuthenticatedGuard } from 'ngx-dam-framework';
import { HomeComponent } from './modules/core/components/home/home.component';
import { ErrorPageComponent } from './modules/core/components/error-page/error-page.component';
import { RegistrationComponent } from './modules/core/components/registration/registration.component';


const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'home',
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [NotAuthenticatedGuard],
  },
  {
    path: 'register',
    component: RegistrationComponent,
    canActivate: [NotAuthenticatedGuard],
  },
  {
    path: 'home',
    component: HomeComponent,
  },
  {
    path: 'configurations',
    loadChildren: () => import('./modules/configuration/configuration.module').then(
      m => m.ConfigurationModule
    ),
    canActivate: [AuthenticatedGuard],
  },
  {
    path: 'adf',
    loadChildren: () => import('./modules/aggregate-detections-file/aggregate-detections-file.module').then(
      m => m.AggregateDetectionsFileModule
    ),
    canActivate: [AuthenticatedGuard],
  },
  {
    path: 'report-templates',
    loadChildren: () => import('./modules/report-template/report-template.module').then(
      m => m.ReportTemplateModule
    ),
    canActivate: [AuthenticatedGuard],
  },
  {
    path: 'report',
    loadChildren: () => import('./modules/report/report.module').then(
      m => m.ReportModule
    ),
    canActivate: [AuthenticatedGuard],
  },
  {
    path: 'facility',
    loadChildren: () => import('./modules/facility/facility.module').then(
      m => m.FacilityModule
    ),
    canActivate: [AuthenticatedGuard],
  },
  {
    path: 'error',
    component: ErrorPageComponent,
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
