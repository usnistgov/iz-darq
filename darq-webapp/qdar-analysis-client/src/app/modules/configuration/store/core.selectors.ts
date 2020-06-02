import { selectFromCollection, selectWorkspaceCurrent } from 'ngx-dam-framework';
import { createEntityAdapter, Dictionary } from '@ngrx/entity';
import { IConfigurationDescriptor } from '../model/configuration.model';
import { createSelector } from '@ngrx/store';

const configurationsAdapter = createEntityAdapter<IConfigurationDescriptor>();
const configurationsSelectors = configurationsAdapter.getSelectors();
export const selectConfigurationsRepo = selectFromCollection('configurations');
export const selectConfigurationsEntities = createSelector(
  selectConfigurationsRepo,
  configurationsSelectors.selectEntities,
);
export const selectConfigurations = createSelector(
  selectConfigurationsRepo,
  configurationsSelectors.selectAll,
);
export const selectConfigurationById = createSelector(
  selectConfigurationsEntities,
  (dict: Dictionary<IConfigurationDescriptor>, props: any): IConfigurationDescriptor => {
    if (dict[props.id]) {
      return dict[props.id];
    } else {
      return undefined;
    }
  }
);
export const selectCurrentConfigurationIsViewOnly = createSelector(
  selectWorkspaceCurrent,
  (active: {
    data: any;
    valid: boolean;
    time: Date;
  }) => {
    return !active || !active.data || active.data.viewOnly;
  }
);
