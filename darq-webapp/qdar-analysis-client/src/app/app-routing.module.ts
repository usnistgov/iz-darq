import { NgModule } from '@angular/core';
import { Routes, RouterModule, ActivatedRouteSnapshot } from '@angular/router';
import {
  LoginComponent,
  AuthenticatedGuard,
  NotAuthenticatedGuard,
  TokenValidGuard,
  UserPredicateGuard,
  IDamUser,
} from 'ngx-dam-framework';
import { HomeComponent } from './modules/core/components/home/home.component';
import { ErrorPageComponent } from './modules/core/components/error-page/error-page.component';
import { RegistrationComponent } from './modules/core/components/registration/registration.component';
import { CreateCredentialsComponent } from './modules/core/components/create-credentials/create-credentials.component';
import { VerifyEmailComponent } from './modules/core/components/verify-email/verify-email.component';
import { UpdateProfileComponent } from './modules/core/components/update-profile/update-profile.component';
import { ForgotPasswordComponent } from './modules/core/components/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './modules/core/components/reset-password/reset-password.component';
import { DownloadComponent } from './modules/core/components/download/download.component';


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
    path: 'download',
    component: DownloadComponent,
  },
  {
    path: 'forgotten-password',
    component: ForgotPasswordComponent,
    canActivate: [
      NotAuthenticatedGuard,
    ],
  },
  {
    path: 'verify-email',
    component: VerifyEmailComponent,
    data: {
      token: (value: ActivatedRouteSnapshot) => value.queryParams.token,
      context: () => 'EMAIL_VERIFICATION',
    },
    canActivate: [
      NotAuthenticatedGuard,
      TokenValidGuard,
    ],
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent,
    data: {
      token: (value: ActivatedRouteSnapshot) => value.queryParams.token,
      context: () => 'PASSWORD_CHANGE',
    },
    canActivate: [
      NotAuthenticatedGuard,
      TokenValidGuard,
    ],
  },
  {
    path: 'create-credentials',
    component: CreateCredentialsComponent,
    data: {
      predicate: (user: any) => {
        return !user.payload?.credentials && user.payload?.source === 'AART';
      }
    },
    canActivate: [
      AuthenticatedGuard,
      UserPredicateGuard,
    ],
  },
  {
    path: 'update-profile',
    component: UpdateProfileComponent,
    data: {
      predicate: (user: any) => {
        return !user.payload?.source;
      }
    },
    canActivate: [
      AuthenticatedGuard,
      UserPredicateGuard,
    ],
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
    data: {
      predicate: (user: IDamUser) => {
        return user.administrator;
      }
    },
    loadChildren: () => import('./modules/facility/facility.module').then(
      m => m.FacilityModule
    ),
    canActivate: [
      AuthenticatedGuard,
      UserPredicateGuard
    ]
  },
  {
    path: 'administration',
    loadChildren: () => import('./modules/administration/administration.module').then(
      m => m.AdministrationModule
    ),
    data: {
      predicate: (user: IDamUser) => {
        return user.administrator;
      }
    },
    canActivate: [
      AuthenticatedGuard,
      UserPredicateGuard
    ]
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
