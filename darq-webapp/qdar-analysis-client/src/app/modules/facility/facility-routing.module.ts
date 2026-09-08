import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DamWidgetRoute, EditorActivateGuard, EditorDeactivateGuard} from '@usnistgov/ngx-dam-framework-legacy';
import {FACILITY_WIDGET, FacilityWidgetComponent} from './components/facility-widget/facility-widget.component';
import {CoreActionTypes, LoadFacilities, OpenFacilityEditor} from './store/core.actions';
import {
  FACILITY_EDITOR_METADATA,
  FacilityEditorComponent
} from './components/facility-editor/facility-editor.component';


const routes: Routes = [
  {
    path: '',
    ...DamWidgetRoute({
      widgetId: FACILITY_WIDGET,
      loadAction: LoadFacilities,
      successAction: CoreActionTypes.LoadFacilitiesSuccess,
      failureAction: CoreActionTypes.LoadFacilitiesFailure,
      redirectTo: ['error'],
      component: FacilityWidgetComponent,
    }),
    children: [
      {
        path: ':facilityId',
        component: FacilityEditorComponent,
        canActivate: [EditorActivateGuard],
        canDeactivate: [EditorDeactivateGuard],
        data: {
          editorMetadata: FACILITY_EDITOR_METADATA,
          onLeave: {
            saveEditor: true,
            saveTableOfContent: true,
          },
          action: OpenFacilityEditor,
          idKey: 'facilityId',
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
export class FacilityRoutingModule { }
