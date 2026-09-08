import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {TemplatesListComponent} from './components/templates-list/templates-list.component';
import {
  CoreActionTypes,
  LoadReportTemplate,
  LoadReportTemplates,
  OpenReportTemplateLabels,
  OpenReportTemplateMetadata,
  OpenReportTemplateSection
} from './store/core.actions';
import {
  DamWidgetRoute,
  DataLoaderGuard,
  EditorActivateGuard,
  EditorDeactivateGuard
} from '@usnistgov/ngx-dam-framework-legacy';
import {
  ReportTemplateWidgetComponent,
  RT_WIDGET_ID
} from './components/report-template-widget/report-template-widget.component';
import {
  RT_METADATA_EDITOR_METADATA,
  RtMetadataEditorComponent
} from './components/rt-metadata-editor/rt-metadata-editor.component';
import {
  RT_SECTION_NARRATIVE_EDITOR_METADATA,
  RtSectionNarrativeEditorComponent
} from './components/rt-section-narrative-editor/rt-section-narrative-editor.component';
import {
  RT_SECTION_PAYLOAD_EDITOR_METADATA,
  RtSectionPayloadEditorComponent
} from './components/rt-section-payload-editor/rt-section-payload-editor.component';
import {
  RT_LABEL_EDITOR_METADATA,
  RtLabelsEditorComponent
} from './components/rt-labels-editor/rt-labels-editor.component';


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
        path: 'labels',
        component: RtLabelsEditorComponent,
        canActivate: [EditorActivateGuard],
        canDeactivate: [EditorDeactivateGuard],
        data: {
          editorMetadata: RT_LABEL_EDITOR_METADATA,
          onLeave: {
            saveEditor: true,
            saveTableOfContent: true,
          },
          action: OpenReportTemplateLabels,
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
