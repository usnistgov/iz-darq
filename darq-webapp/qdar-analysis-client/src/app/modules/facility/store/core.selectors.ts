import { createEntityAdapter, Dictionary } from '@ngrx/entity';
import { createSelector } from '@ngrx/store';
import { selectFromCollection, selectPayloadData } from 'ngx-dam-framework';
import { IFacility, IFacilityDescriptor } from '../model/facility.model';

const facilitiesAdapter = createEntityAdapter<IFacilityDescriptor>();
const facilitiesSelectors = facilitiesAdapter.getSelectors();
export const selectFacilitiesRepo = selectFromCollection('facilities');
export const selectFacilitiesEntities = createSelector(
  selectFacilitiesRepo,
  facilitiesSelectors.selectEntities,
);
export const selectFacilities = createSelector(
  selectFacilitiesRepo,
  facilitiesSelectors.selectAll,
);
export const selectFacilityById = createSelector(
  selectFacilitiesEntities,
  (dict: Dictionary<IFacilityDescriptor>, props: any): IFacilityDescriptor => {
    if (dict[props.id]) {
      return dict[props.id];
    } else {
      return undefined;
    }
  }
);

export const selectFacility = createSelector(
  selectPayloadData,
  (data: IFacility) => {
    return data;
  }
);
