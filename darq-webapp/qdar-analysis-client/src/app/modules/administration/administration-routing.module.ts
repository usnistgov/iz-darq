import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DamWidgetRoute, DataLoaderGuard, EditorActivateGuard, EditorDeactivateGuard } from 'ngx-dam-framework';
import { ADMIN_WIDGET, AdminWidgetComponent } from './components/admin-widget/admin-widget.component';
import { LoadAdministrationWidget, CoreActionTypes, LoadUsers, OpenWebContentEditor, OpenEmailTemplateEditor, OpenConfigurationEditor } from './store/core.actions';
import { UsersManagementEditorComponent } from './components/users-management-editor/users-management-editor.component';
import { WebContentEditorComponent, WEB_CONTENT_EDITOR_METADATA } from './components/web-content-editor/web-content-editor.component';
import { EmailsTemplateEditorComponent, EMTAIL_TEMPLATE_EDITOR_METADATA } from './components/emails-template-editor/emails-template-editor.component';
import { ConfigurationEditorComponent, ADMIN_CONFIG_EDITOR_METADATA } from './components/configuration-editor/configuration-editor.component';

const routes: Routes = [
  {
    path: '',
    ...DamWidgetRoute({
      widgetId: ADMIN_WIDGET,
      loadAction: LoadAdministrationWidget,
      successAction: CoreActionTypes.LoadAdministrationWidgetSuccess,
      failureAction: CoreActionTypes.LoadAdministrationWidgetFailure,
      redirectTo: ['error'],
      component: AdminWidgetComponent,
    }),
    children: [
      {
        path: 'users',
        data: {
          loadAction: LoadUsers,
          successAction: CoreActionTypes.LoadUsersSuccess,
          failureAction: CoreActionTypes.LoadUsersFailure,
          redirectTo: ['error'],
        },
        component: UsersManagementEditorComponent,
        canActivate: [DataLoaderGuard],
      },
      {
        path: 'web-content',
        component: WebContentEditorComponent,
        canActivate: [EditorActivateGuard],
        canDeactivate: [EditorDeactivateGuard],
        data: {
          editorMetadata: WEB_CONTENT_EDITOR_METADATA,
          onLeave: {
            saveEditor: true,
            saveTableOfContent: true,
          },
          action: OpenWebContentEditor,
          idKey: '',
          redirectTo: ['/', 'error'],
        },
      },
      {
        path: 'email-templates',
        component: EmailsTemplateEditorComponent,
        canActivate: [EditorActivateGuard],
        canDeactivate: [EditorDeactivateGuard],
        data: {
          editorMetadata: EMTAIL_TEMPLATE_EDITOR_METADATA,
          onLeave: {
            saveEditor: true,
            saveTableOfContent: true,
          },
          action: OpenEmailTemplateEditor,
          idKey: '',
          redirectTo: ['/', 'error'],
        },
      },
      {
        path: 'tool-configuration',
        component: ConfigurationEditorComponent,
        canActivate: [EditorActivateGuard],
        canDeactivate: [EditorDeactivateGuard],
        data: {
          editorMetadata: ADMIN_CONFIG_EDITOR_METADATA,
          onLeave: {
            saveEditor: true,
            saveTableOfContent: true,
          },
          action: OpenConfigurationEditor,
          idKey: '',
          redirectTo: ['/', 'error'],
        },
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdministrationRoutingModule { }
