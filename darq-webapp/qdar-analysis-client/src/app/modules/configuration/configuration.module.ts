import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfigurationRoutingModule } from './configuration-routing.module';
import { ConfigurationWidgetComponent } from './components/configuration-widget/configuration-widget.component';
import { SharedModule } from '../shared/shared.module';
import { DamFrameworkModule, DamMessagesModule } from 'ngx-dam-framework';
import { EffectsModule } from '@ngrx/effects';
import { CoreEffects } from './store/core.effects';
import { ConfigurationToolbarComponent } from './components/configuration-toolbar/configuration-toolbar.component';
import { ConfigurationSideBarComponent } from './components/configuration-side-bar/configuration-side-bar.component';
import { ConfigurationEditorComponent } from './components/configuration-editor/configuration-editor.component';
import { ConfigurationTitleComponent } from './components/configuration-title/configuration-title.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';


@NgModule({
  declarations: [
    ConfigurationWidgetComponent,
    ConfigurationToolbarComponent,
    ConfigurationSideBarComponent,
    ConfigurationEditorComponent,
    ConfigurationTitleComponent
  ],
  imports: [
    CommonModule,
    ConfigurationRoutingModule,
    SharedModule,
    DamFrameworkModule,
    DamMessagesModule,
    EffectsModule.forFeature([CoreEffects]),
  ]
})
export class ConfigurationModule { }
