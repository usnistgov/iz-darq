import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DamWidgetRoute, EditorActivateGuard, EditorDeactivateGuard} from '@usnistgov/ngx-dam-framework-legacy';
import {
  CONFIG_WIDGET,
  ConfigurationWidgetComponent
} from './components/configuration-widget/configuration-widget.component';
import {CoreActionTypes, OpenConfigurationEditor, RouteLoadConfigurationPage} from './store/core.actions';
import {
  CONFIGURATION_EDITOR_MD,
  ConfigurationEditorComponent
} from './components/configuration-editor/configuration-editor.component';

const routes: Routes = [
  {
    path: '',
    ...DamWidgetRoute({
      widgetId: CONFIG_WIDGET,
      loadAction: RouteLoadConfigurationPage,
      successAction: CoreActionTypes.RouteLoadConfigurationPageSuccess,
      failureAction: CoreActionTypes.RouteLoadConfigurationPageFailure,
      redirectTo: ['error'],
      component: ConfigurationWidgetComponent,
    }),
    children: [
      {
        path: ':configId',
        component: ConfigurationEditorComponent,
        canActivate: [EditorActivateGuard],
        canDeactivate: [EditorDeactivateGuard],
        data: {
          editorMetadata: CONFIGURATION_EDITOR_MD,
          onLeave: {
            saveEditor: true,
            saveTableOfContent: true,
          },
          action: OpenConfigurationEditor,
          idKey: 'configId',
          redirectTo: ['error'],
        },
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConfigurationRoutingModule { }
