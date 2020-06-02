import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DamWidgetRoute, AuthenticatedGuard, EditorActivateGuard, EditorDeactivateGuard } from 'ngx-dam-framework';
import { FACILITY_WIDGET, FacilityWidgetComponent } from './components/facility-widget/facility-widget.component';
import { LoadFacilities, CoreActionTypes, LoadFacility, OpenFacilityEditor } from './store/core.actions';
import { FacilityEditorComponent, FACILITY_EDITOR_METADATA } from './components/facility-editor/facility-editor.component';


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
