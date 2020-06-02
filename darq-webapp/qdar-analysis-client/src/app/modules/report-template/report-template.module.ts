import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportTemplateRoutingModule } from './report-template-routing.module';
import { TemplatesListComponent } from './components/templates-list/templates-list.component';
import { SharedModule } from '../shared/shared.module';
import { EffectsModule } from '@ngrx/effects';
import { CoreEffects } from './store/core.effects';
import { ReportTemplateWidgetComponent } from './components/report-template-widget/report-template-widget.component';
import { DamFrameworkModule, DamMessagesModule } from 'ngx-dam-framework';
import { RtSideBarComponent } from './components/rt-side-bar/rt-side-bar.component';
import { RtToolbarComponent } from './components/rt-toolbar/rt-toolbar.component';
import { RtActiveTitleComponent } from './components/rt-active-title/rt-active-title.component';
import { RtMetadataEditorComponent } from './components/rt-metadata-editor/rt-metadata-editor.component';
import { WidgetEffects } from './store/widget.effects';
import { RtSectionNarrativeEditorComponent } from './components/rt-section-narrative-editor/rt-section-narrative-editor.component';
import { RtSectionPayloadEditorComponent } from './components/rt-section-payload-editor/rt-section-payload-editor.component';
import { CreateRtDialogComponent } from './components/create-rt-dialog/create-rt-dialog.component';


@NgModule({
  declarations: [
    TemplatesListComponent,
    ReportTemplateWidgetComponent,
    RtSideBarComponent,
    RtToolbarComponent,
    RtActiveTitleComponent,
    RtMetadataEditorComponent,
    RtSectionNarrativeEditorComponent,
    RtSectionPayloadEditorComponent,
    CreateRtDialogComponent,
  ],
  imports: [
    CommonModule,
    ReportTemplateRoutingModule,
    SharedModule,
    DamFrameworkModule,
    DamMessagesModule,
    EffectsModule.forFeature([CoreEffects, WidgetEffects]),
  ]
})
export class ReportTemplateModule { }
