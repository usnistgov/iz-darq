import { createEntityAdapter, Dictionary } from '@ngrx/entity';
import { IADFDescriptor, IADFMetadata } from '../model/adf.model';
import { selectFromCollection, selectValue } from 'ngx-dam-framework';
import { createSelector } from '@ngrx/store';
import { IAnalysisJob, IReportDescriptor } from '../../report/model/report.model';
import { IFacilityDescriptor } from '../../facility/model/facility.model';

const filesAdapter = createEntityAdapter<IADFDescriptor>();
const filesSelectors = filesAdapter.getSelectors();
export const selectFilesRepo = selectFromCollection('files');
export const selectFilesEntities = createSelector(
  selectFilesRepo,
  filesSelectors.selectEntities,
);
export const selectFiles = createSelector(
  selectFilesRepo,
  filesSelectors.selectAll,
);
export const selectFilesById = createSelector(
  selectFilesEntities,
  byId<IADFDescriptor>(),
);

const jobsAdapter = createEntityAdapter<IAnalysisJob>();
const jobsSelectors = jobsAdapter.getSelectors();
export const selectJobsRepo = selectFromCollection('jobs');
export const selectJobsEntities = createSelector(
  selectJobsRepo,
  jobsSelectors.selectEntities,
);
export const selectJobs = createSelector(
  selectJobsRepo,
  jobsSelectors.selectAll,
);
export const selectJobsById = createSelector(
  selectJobsEntities,
  byId<IAnalysisJob>(),
);

const reportsAdapter = createEntityAdapter<IReportDescriptor>();
const reportsSelectors = reportsAdapter.getSelectors();
export const selectReportsRepo = selectFromCollection('reports');
export const selectReportsEntities = createSelector(
  selectReportsRepo,
  reportsSelectors.selectEntities,
);
export const selectReports = createSelector(
  selectReportsRepo,
  reportsSelectors.selectAll,
);
export const selectReportsById = createSelector(
  selectReportsEntities,
  byId<IReportDescriptor>(),
);

export const selectReportsNumber = createSelector(
  selectReports,
  (reports) => {
    return reports.length;
  }
);

export const selectFacilityList = selectValue<IFacilityDescriptor[]>('facilityList');
export const selectCurrentFacility = selectValue<string>('facility');

export const selectOpenFileMetadata = selectValue<IADFMetadata>('file');
function byId<T>() {
  return (dict: Dictionary<T>, props: any): T => {
    if (dict[props.id]) {
      return dict[props.id];
    } else {
      return undefined;
    }
  };
}
