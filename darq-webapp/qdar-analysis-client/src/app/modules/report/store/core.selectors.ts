import { createSelector } from '@ngrx/store';
import { selectPayloadData, selectValue } from '@usnistgov/ngx-dam-framework-legacy';
import { IReport } from '../model/report.model';
import { IReportFilter } from '../../report-template/model/report-template.model';
import { ITocNode } from '../components/report-toc/report-toc.component';


export const selectReportGeneralFilter = selectValue<IReportFilter>('reportGeneralFilter');
export const selectReportingGroups = selectValue<Record<string, string>>('reportingGroups');
export const selectReportTocNodes = selectValue<ITocNode[]>('reportTocNodes');

export const selectReportPayload = createSelector(
  selectPayloadData,
  (payload: IReport) => {
    return payload;
  }
);
