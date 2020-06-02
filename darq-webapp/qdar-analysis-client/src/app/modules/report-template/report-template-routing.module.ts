import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TemplatesListComponent } from './components/templates-list/templates-list.component';
import { LoadReportTemplates, CoreActionTypes, LoadReportTemplate, OpenReportTemplateMetadata, OpenReportTemplateSection } from './store/core.actions';
import { DataLoaderGuard, DamWidgetRoute, EditorActivateGuard, EditorDeactivateGuard } from 'ngx-dam-framework';
import { RT_WIDGET_ID, ReportTemplateWidgetComponent } from './components/report-template-widget/report-template-widget.component';
import { RtMetadataEditorComponent, RT_METADATA_EDITOR_METADATA } from './components/rt-metadata-editor/rt-metadata-editor.component';
import { RT_SECTION_NARRATIVE_EDITOR_METADATA, RtSectionNarrativeEditorComponent } from './components/rt-section-narrative-editor/rt-section-narrative-editor.component';
import { RtSectionPayloadEditorComponent, RT_SECTION_PAYLOAD_EDITOR_METADATA } from './components/rt-section-payload-editor/rt-section-payload-editor.component';


const routes: Routes = [
  {
    path: '',
    redirectTo: 'list',
  },
  {
    path: 'list',
    component: TemplatesListComponent,
    data: {
      loadAction: LoadReportTemplates,
      successAction: CoreActionTypes.LoadReportTemplatesSuccess,
      failureAction: CoreActionTypes.LoadReportTemplatesSuccess,
      redirectTo: ['/', 'error'],
    },
    canActivate: [
      DataLoaderGuard,
    ],
  },
  {
    path: ':templateId',
    ...DamWidgetRoute({
      widgetId: RT_WIDGET_ID,
      routeParam: 'templateId',
      loadAction: LoadReportTemplate,
      successAction: CoreActionTypes.LoadReportTemplateSuccess,
      failureAction: CoreActionTypes.LoadReportTemplateFailure,
      redirectTo: ['error'],
      component: ReportTemplateWidgetComponent,
    }),
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'metadata'
      },
      {
        path: 'metadata',
        component: RtMetadataEditorComponent,
        canActivate: [EditorActivateGuard],
        canDeactivate: [EditorDeactivateGuard],
        data: {
          editorMetadata: RT_METADATA_EDITOR_METADATA,
          onLeave: {
            saveEditor: true,
            saveTableOfContent: true,
          },
          action: OpenReportTemplateMetadata,
          idKey: 'templateId',
          redirectTo: ['/', 'error'],
        },
      },
      {
        path: 'section/:sectionId',
        children: [
          {
            path: '',
            pathMatch: 'full',
            redirectTo: 'narrative'
          },
          {
            path: 'narrative',
            component: RtSectionNarrativeEditorComponent,
            canActivate: [EditorActivateGuard],
            canDeactivate: [EditorDeactivateGuard],
            data: {
              editorMetadata: RT_SECTION_NARRATIVE_EDITOR_METADATA,
              onLeave: {
                saveEditor: true,
                saveTableOfContent: true,
              },
              action: OpenReportTemplateSection,
              idKey: 'sectionId',
              redirectTo: ['/', 'error'],
            },
          },
          {
            path: 'data-tables',
            component: RtSectionPayloadEditorComponent,
            canActivate: [EditorActivateGuard],
            canDeactivate: [EditorDeactivateGuard],
            data: {
              editorMetadata: RT_SECTION_PAYLOAD_EDITOR_METADATA,
              onLeave: {
                saveEditor: true,
                saveTableOfContent: true,
              },
              action: OpenReportTemplateSection,
              idKey: 'sectionId',
              redirectTo: ['/', 'error'],
            },
          }
        ]

      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportTemplateRoutingModule { }
