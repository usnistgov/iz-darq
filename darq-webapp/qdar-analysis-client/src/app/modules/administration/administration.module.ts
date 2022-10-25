import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdministrationRoutingModule } from './administration-routing.module';
import { AdminSidebarComponent } from './components/admin-sidebar/admin-sidebar.component';
import { EmailsTemplateEditorComponent } from './components/emails-template-editor/emails-template-editor.component';
import { DamFrameworkModule } from 'ngx-dam-framework';
import { SharedModule } from '../shared/shared.module';
import { AdminWidgetComponent } from './components/admin-widget/admin-widget.component';
import { UsersManagementEditorComponent } from './components/users-management-editor/users-management-editor.component';
import { WebContentEditorComponent } from './components/web-content-editor/web-content-editor.component';
import { CoreEffects } from './store/core.effects';
import { EffectsModule } from '@ngrx/effects';
import { UserProfileDialogComponent } from './components/user-profile-dialog/user-profile-dialog.component';
import { UserRoleDialogComponent } from './components/user-role-dialog/user-role-dialog.component';
import { ConfigurationEditorComponent } from './components/configuration-editor/configuration-editor.component';
import { ConfigurationLoadingDialogComponent } from './components/configuration-loading-dialog/configuration-loading-dialog.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ExternalVariableEditorComponent } from './components/external-variable-editor/external-variable-editor.component';
import { ExternalVariableDialogComponent } from './components/external-variable-dialog/external-variable-dialog.component';
import { ChipsModule } from 'primeng/chips';
import { VariablesCsvImportDialogComponent } from './components/variables-csv-import-dialog/variables-csv-import-dialog.component';


@NgModule({
  declarations: [
    AdminWidgetComponent,
    AdminSidebarComponent,
    UsersManagementEditorComponent,
    EmailsTemplateEditorComponent,
    WebContentEditorComponent,
    UserProfileDialogComponent,
    UserRoleDialogComponent,
    ConfigurationEditorComponent,
    ConfigurationLoadingDialogComponent,
    ExternalVariableEditorComponent,
    ExternalVariableDialogComponent,
    VariablesCsvImportDialogComponent,
  ],
  imports: [
    CommonModule,
    AdministrationRoutingModule,
    MatProgressSpinnerModule,
    SharedModule,
    DamFrameworkModule,
    EffectsModule.forFeature([CoreEffects]),
    ChipsModule,
  ],
  entryComponents: [ConfigurationLoadingDialogComponent]
})
export class AdministrationModule { }
