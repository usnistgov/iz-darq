import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AggregateDetectionsFileRoutingModule } from './aggregate-detections-file-routing.module';
import { AdfUploadComponent } from './components/adf-upload/adf-upload.component';
import { SharedModule } from '../shared/shared.module';
import { EffectsModule } from '@ngrx/effects';
import { CoreEffects } from './store/core.effects';
import { AdfSummaryComponent } from './components/adf-summary/adf-summary.component';
import { AdfJobDialogComponent } from './components/adf-job-dialog/adf-job-dialog.component';
import { AdfWidgetComponent } from './components/adf-widget/adf-widget.component';
import { DamFrameworkModule } from 'ngx-dam-framework';
import { IisSidebarComponent } from './components/iis-sidebar/iis-sidebar.component';
import { FilesListEditorComponent } from './components/files-list-editor/files-list-editor.component';
import { JobListEditorComponent } from './components/job-list-editor/job-list-editor.component';
import { WidgetEffects } from './store/widget.effects';
import { ReportListEditorComponent } from './components/report-list-editor/report-list-editor.component';
import { AdfMergeDialogComponent } from './components/adf-merge-dialog/adf-merge-dialog.component';
import { AdfEditDialogComponent } from './components/adf-edit-dialog/adf-edit-dialog.component';


@NgModule({
  declarations: [
    AdfUploadComponent,
    JobListEditorComponent,
    AdfSummaryComponent,
    AdfJobDialogComponent,
    ReportListEditorComponent,
    AdfWidgetComponent,
    IisSidebarComponent,
    FilesListEditorComponent,
    AdfMergeDialogComponent,
    AdfEditDialogComponent
  ],
  imports: [
    CommonModule,
    AggregateDetectionsFileRoutingModule,
    SharedModule,
    DamFrameworkModule,
    EffectsModule.forFeature([CoreEffects, WidgetEffects]),
  ]
})
export class AggregateDetectionsFileModule { }
