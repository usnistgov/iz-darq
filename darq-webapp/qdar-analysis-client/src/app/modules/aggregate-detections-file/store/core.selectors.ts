import { createEntityAdapter, Dictionary } from '@ngrx/entity';
import { IADFDescriptor, IADFMetadata } from '../model/adf.model';
import { selectFromCollection, selectValue } from 'ngx-dam-framework';
import { createSelector } from '@ngrx/store';
import { IAnalysisJob, IReportDescriptor } from '../../report/model/report.model';
import { IFacilityDescriptor, IUserFacilityDescriptor } from '../../facility/model/facility.model';


// ------------- ADF Files Repository -------------
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
// ----------------------------------------------------

// ------------- Analysis Jobs Repository -------------
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
// ----------------------------------------------------

// ------------- Reports Repository -------------
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
// ----------------------------------------------


// ------------- User Facilities Repository -------------
const userFacilitiesAdapter = createEntityAdapter<IUserFacilityDescriptor>();
const userFacilitiesSelectors = userFacilitiesAdapter.getSelectors();
export const selectUserFacilitiesRepo = selectFromCollection('user-facilities');
export const selectUserFacilitiesEntities = createSelector(
  selectUserFacilitiesRepo,
  userFacilitiesSelectors.selectEntities,
);
export const selectUserFacilities = createSelector(
  selectUserFacilitiesRepo,
  userFacilitiesSelectors.selectAll,
);
export const selectUserFacilitiesSorted = createSelector(
  selectUserFacilities,
  (facilities: IUserFacilityDescriptor[]) => {
    const LOCAL = facilities.filter((elm) => elm._private);
    const REST = facilities.filter((elm) => !elm._private);
    REST.sort((x, y) => {
      return x.name > y.name ? 1 : -1;
    });
    return [
      ...LOCAL,
      ...REST,
    ];
  },
);
export const selectUserFacilityById = createSelector(
  selectUserFacilitiesEntities,
  byId<IUserFacilityDescriptor>(),
);
export const selectCurrentFacility = createSelector(
  selectValue<string>('facilityId'),
  selectUserFacilitiesEntities,
  (id: string, dict: Dictionary<IUserFacilityDescriptor>): IUserFacilityDescriptor => {
    if (id && dict[id]) {
      return dict[id];
    } else {
      return undefined;
    }
  }
);
// -----------------------------------------------------


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
