import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FacilityRoutingModule } from './facility-routing.module';
import { FacilityWidgetComponent } from './components/facility-widget/facility-widget.component';
import { FacilityEditorComponent } from './components/facility-editor/facility-editor.component';
import { SharedModule } from '../shared/shared.module';
import { DamFrameworkModule, DamMessagesModule } from 'ngx-dam-framework';
import { FacilityListComponent } from './components/facility-list/facility-list.component';
import { EffectsModule } from '@ngrx/effects';
import { CoreEffects } from './store/core.effects';
import { UserListComponent } from './components/user-list/user-list.component';


@NgModule({
  declarations: [FacilityWidgetComponent, FacilityEditorComponent, FacilityListComponent, UserListComponent],
  imports: [
    CommonModule,
    FacilityRoutingModule,
    SharedModule,
    DamFrameworkModule,
    DamMessagesModule,
    EffectsModule.forFeature([CoreEffects]),
  ]
})
export class FacilityModule { }
