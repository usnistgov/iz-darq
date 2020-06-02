import { createEntityAdapter, Dictionary } from '@ngrx/entity';
import { IDetectionResource, ICvxResource } from '../model/public.model';
import { selectFromCollection, selectValue } from 'ngx-dam-framework';
import { createSelector } from '@ngrx/store';

const detectionsAdapter = createEntityAdapter<IDetectionResource>();
const detectionsSelectors = detectionsAdapter.getSelectors();
export const selectDetectionsRepo = selectFromCollection('detections');
export const selectDetectionsEntities = createSelector(
  selectDetectionsRepo,
  detectionsSelectors.selectEntities,
);
export const selectAllDetections = createSelector(
  selectDetectionsRepo,
  detectionsSelectors.selectAll,
);
export const selectDetectionById = createSelector(
  selectDetectionsEntities,
  (dict: Dictionary<IDetectionResource>, props: any): IDetectionResource => {
    if (dict[props.id]) {
      return dict[props.id];
    } else {
      return undefined;
    }
  }
);

const cvxAdapter = createEntityAdapter<ICvxResource>();
const cvxSelectors = cvxAdapter.getSelectors();
export const selectCvxRepo = selectFromCollection('cvx');
export const selectCvxEntities = createSelector(
  selectCvxRepo,
  cvxSelectors.selectEntities,
);
export const selectAllCvx = createSelector(
  selectCvxRepo,
  cvxSelectors.selectAll,
);
export const selectCvxById = createSelector(
  selectCvxEntities,
  // tslint:disable-next-line: no-identical-functions
  (dict: Dictionary<ICvxResource>, props: any): ICvxResource => {
    if (dict[props.id]) {
      return dict[props.id];
    } else {
      return undefined;
    }
  }
);
export const selectPatientTables = selectValue<string[]>('patientTables');
export const selectVaccinationTables = selectValue<string[]>('vaccinationTables');

