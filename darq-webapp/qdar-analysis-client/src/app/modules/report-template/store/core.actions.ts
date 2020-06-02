import { Action } from '@ngrx/store';
import { IReportTemplateDescriptor, IReportTemplate } from '../model/report-template.model';
import { OpenEditorBase, IEditorMetadata } from 'ngx-dam-framework';

export enum CoreActionTypes {
  LoadReportTemplates = '[Report Template] Load Report Templates',
  LoadReportTemplatesSuccess = '[Report Template] Load Report Templates Success',
  LoadReportTemplatesFailure = '[Report Template] Load Report Templates Failure',

  LoadReportTemplate = '[Report Template] Load Report Template',
  LoadReportTemplateSuccess = '[Report Template] Load Report Template Success',
  LoadReportTemplateFailure = '[Report Template] Load Report Template Failure',

  CreateChildSection = '[Report Template] Create Child Section',

  OpenReportTemplateMetadata = '[Report Template] Open Report Template Metadata',
  OpenReportTemplateSection = '[Report Template] Open Report Template Section',

}

export class LoadReportTemplates implements Action {
  readonly type = CoreActionTypes.LoadReportTemplates;
}

export class LoadReportTemplatesSuccess implements Action {
  readonly type = CoreActionTypes.LoadReportTemplatesSuccess;
  constructor(public payload: IReportTemplateDescriptor[]) { }
}

export class LoadReportTemplatesFailure implements Action {
  readonly type = CoreActionTypes.LoadReportTemplatesFailure;
  constructor(public payload: { error: any }) { }
}

export class LoadReportTemplate implements Action {
  readonly type = CoreActionTypes.LoadReportTemplate;
  constructor(public id: string) { }
}

export class LoadReportTemplateSuccess implements Action {
  readonly type = CoreActionTypes.LoadReportTemplateSuccess;
  constructor(public payload: IReportTemplate) { }
}

export class LoadReportTemplateFailure implements Action {
  readonly type = CoreActionTypes.LoadReportTemplateFailure;
  constructor(public payload: { error: any }) { }
}


export class OpenReportTemplateMetadata extends OpenEditorBase implements Action {
  readonly type = CoreActionTypes.OpenReportTemplateMetadata;
  constructor(public payload: {
    id: string,
    editor: IEditorMetadata,
  }) {
    super();
  }
}

export class OpenReportTemplateSection extends OpenEditorBase implements Action {
  readonly type = CoreActionTypes.OpenReportTemplateSection;
  constructor(public payload: {
    id: string,
    editor: IEditorMetadata,
  }) {
    super();
  }
}


export type CoreActions =
  LoadReportTemplates
  | LoadReportTemplatesSuccess
  | LoadReportTemplatesFailure
  | LoadReportTemplate
  | LoadReportTemplateSuccess
  | LoadReportTemplateFailure
  | OpenReportTemplateMetadata
  | OpenReportTemplateSection;

