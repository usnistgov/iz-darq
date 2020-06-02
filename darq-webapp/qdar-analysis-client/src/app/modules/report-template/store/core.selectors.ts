import { selectFromCollection, selectPayloadData, selectValue, selectUsername } from 'ngx-dam-framework';
import { createEntityAdapter, Dictionary } from '@ngrx/entity';
import { createSelector } from '@ngrx/store';
import { IReportTemplateDescriptor, IReportTemplate, IReportSection } from '../model/report-template.model';

const reportTemplatesAdapter = createEntityAdapter<IReportTemplateDescriptor>();
const reportTemplatesSelectors = reportTemplatesAdapter.getSelectors();
export const selectReportTemplatesRepo = selectFromCollection('templates');
export const selectReportTemplatesEntities = createSelector(
  selectReportTemplatesRepo,
  reportTemplatesSelectors.selectEntities,
);
export const selectReportTemplates = createSelector(
  selectReportTemplatesRepo,
  reportTemplatesSelectors.selectAll,
);
export const selectReportTemplateById = createSelector(
  selectReportTemplatesEntities,
  (dict: Dictionary<IReportTemplateDescriptor>, props: any): IReportTemplateDescriptor => {
    if (dict[props.id]) {
      return dict[props.id];
    } else {
      return undefined;
    }
  }
);


const sectionsAdapter = createEntityAdapter<IReportSection>();
const sectionsSelectors = sectionsAdapter.getSelectors();
export const selectSectionsRepo = selectFromCollection('sections');
export const selectSectionsEntities = createSelector(
  selectSectionsRepo,
  sectionsSelectors.selectEntities,
);
export const selectSections = createSelector(
  selectSectionsRepo,
  sectionsSelectors.selectAll,
);
export const selectSectionById = createSelector(
  selectSectionsEntities,
  // tslint:disable-next-line: no-identical-functions
  (dict: Dictionary<IReportSection>, props: any): IReportSection => {
    if (dict[props.id]) {
      return dict[props.id];
    } else {
      return undefined;
    }
  }
);


export const selectReportTemplate = createSelector(
  selectPayloadData,
  (payload: IReportTemplate) => {
    return payload;
  }
);
export const selectReportTemplateConfiguration = createSelector(
  selectReportTemplate,
  (payload: IReportTemplate) => {
    return payload.configuration;
  }
);
export const selectTableOfContentIsChanged = selectValue<boolean>('tableOfContentChanged');
export const selectInitSections = selectValue<IReportSection[]>('sections');
export const selectRtIsViewOnly = createSelector(
  selectReportTemplate,
  (rt: IReportTemplate) => {
    return rt.viewOnly;
  }
);
export const selectRtIsPublished = createSelector(
  selectReportTemplate,
  (rt: IReportTemplate) => {
    return rt.published;
  }
);
export const selectRtIsOwned = createSelector(
  selectReportTemplate,
  selectUsername,
  (rt: IReportTemplate, username: string) => {
    return rt.owner === username;
  }
);
