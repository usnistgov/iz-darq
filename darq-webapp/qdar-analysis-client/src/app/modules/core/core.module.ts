import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CoreRoutingModule } from './core-routing.module';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { HomeComponent } from './components/home/home.component';
import { ErrorPageComponent } from './components/error-page/error-page.component';
import { DamAuthenticationModule, DamMessagesModule } from 'ngx-dam-framework';
import { CardModule } from 'primeng/card';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RegistrationComponent } from './components/registration/registration.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { UserManagementComponent } from './components/user-management/user-management.component';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    ErrorPageComponent,
    RegistrationComponent,
    ForgotPasswordComponent,
    UserManagementComponent
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
    UserManagementComponent
  ],
})
export class CoreModule { }
