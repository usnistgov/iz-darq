import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CoreRoutingModule } from './core-routing.module';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { HomeComponent } from './components/home/home.component';
import { ErrorPageComponent } from './components/error-page/error-page.component';
import { DamAuthenticationModule, DamMessagesModule } from '@usnistgov/ngx-dam-framework-legacy';
import { CardModule } from 'primeng/card';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RegistrationComponent } from './components/registration/registration.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { SharedModule } from '../shared/shared.module';
import { VerifyEmailComponent } from './components/verify-email/verify-email.component';
import { UpdateProfileComponent } from './components/update-profile/update-profile.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { DownloadComponent } from './components/download/download.component';

@NgModule({
  declarations: [
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    ErrorPageComponent,
    RegistrationComponent,
    ForgotPasswordComponent,
    VerifyEmailComponent,
    UpdateProfileComponent,
    ResetPasswordComponent,
    DownloadComponent,
  ],
  imports: [
    CommonModule,
    CoreRoutingModule,
    DamAuthenticationModule,
    DamMessagesModule,
    NgbModule,
    CardModule,
    SharedModule,
  ],
  exports: [
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    ErrorPageComponent,
    RegistrationComponent,
    ForgotPasswordComponent,
    UpdateProfileComponent,
    DownloadComponent,
  ],
})
export class CoreModule { }
