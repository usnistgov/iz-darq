import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AggregateDetectionsFileRoutingModule } from './aggregate-detections-file-routing.module';
import { AdfDashboardComponent } from './components/adf-dashboard/adf-dashboard.component';
import { AdfUploadComponent } from './components/adf-upload/adf-upload.component';
import { SharedModule } from '../shared/shared.module';
import { EffectsModule } from '@ngrx/effects';
import { CoreEffects } from './store/core.effects';
import { AdfListComponent } from './components/adf-list/adf-list.component';
import { JobListComponent } from './components/job-list/job-list.component';
import { AdfSummaryComponent } from './components/adf-summary/adf-summary.component';
import { AdfJobDialogComponent } from './components/adf-job-dialog/adf-job-dialog.component';
import { ReportListComponent } from './components/report-list/report-list.component';


@NgModule({
  declarations: [
    AdfDashboardComponent,
    AdfUploadComponent,
    AdfListComponent,
    JobListComponent,
    AdfSummaryComponent,
    AdfJobDialogComponent,
    ReportListComponent
  ],
  imports: [
    CommonModule,
    AggregateDetectionsFileRoutingModule,
    SharedModule,
    EffectsModule.forFeature([CoreEffects]),
  ]
})
export class AggregateDetectionsFileModule { }
