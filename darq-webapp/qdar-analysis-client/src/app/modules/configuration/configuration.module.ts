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
import { ComplexDetectionDialogComponent } from './components/complex-detection-dialog/complex-detection-dialog.component';
import { ComplexDetectionExpressionDialogComponent } from './components/complex-detection-expression-dialog/complex-detection-expression-dialog.component';
import { OrganizationChartModule } from 'primeng/organizationchart';
import { DragDropModule } from 'primeng/dragdrop';
import { CodemirrorModule } from '@ctrl/ngx-codemirror';

import 'codemirror/addon/display/placeholder';
import 'codemirror/addon/edit/closebrackets';
import 'codemirror/addon/edit/closetag';
import 'codemirror/addon/edit/matchbrackets';
import 'codemirror/addon/edit/matchbrackets';
import 'codemirror/addon/fold/brace-fold';
import 'codemirror/addon/fold/foldgutter';
import 'codemirror/addon/fold/xml-fold';
import 'codemirror/addon/selection/active-line';
import 'codemirror/mode/yaml/yaml';

@NgModule({
  declarations: [
    ConfigurationWidgetComponent,
    ConfigurationToolbarComponent,
    ConfigurationSideBarComponent,
    ConfigurationEditorComponent,
    ConfigurationTitleComponent,
    ComplexDetectionDialogComponent,
    ComplexDetectionExpressionDialogComponent
  ],
  imports: [
    CommonModule,
    ConfigurationRoutingModule,
    SharedModule,
    DamFrameworkModule,
    DamMessagesModule,
    OrganizationChartModule,
    DragDropModule,
    EffectsModule.forFeature([CoreEffects]),
    CodemirrorModule
  ],
  entryComponents: [ComplexDetectionDialogComponent, ComplexDetectionExpressionDialogComponent]
})
export class ConfigurationModule { }
