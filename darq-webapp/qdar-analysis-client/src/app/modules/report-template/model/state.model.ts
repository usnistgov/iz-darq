import { IReportTemplate, IReportSection } from './report-template.model';
import { IDamResource } from 'ngx-dam-framework';
import { EntityType } from '../../shared/model/entity.model';

export interface IReportTemplateStatePayload {
  payload: IReportTemplate;
  tree: ISectionTree[];
}

export interface ISectionTree {
  data: IReportSectionDisplay;
  children: ISectionTree[];
}

export interface IReportSectionDisplay {
  id: string;
  path: string;
  position: number;
  type: EntityType.SECTION;
  name: string;
}
