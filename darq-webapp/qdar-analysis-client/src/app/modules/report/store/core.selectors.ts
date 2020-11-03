import { createSelector } from '@ngrx/store';
import { selectPayloadData, selectValue } from 'ngx-dam-framework';
import { IReport } from '../model/report.model';
import { IReportFilter } from '../../report-template/model/report-template.model';


export const selectReportGeneralFilter = selectValue<IReportFilter>('reportGeneralFilter');

export const selectReportPayload = createSelector(
  selectPayloadData,
  (payload: IReport) => {
    return payload;
  }
);
