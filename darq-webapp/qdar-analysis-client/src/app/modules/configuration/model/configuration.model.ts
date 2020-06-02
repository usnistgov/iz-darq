import { IRange } from '../../shared/model/age-group.model';
import { IDamResource } from 'ngx-dam-framework';
import { EntityType } from '../../shared/model/entity.model';
import { IDescriptor } from '../../shared/model/descriptor.model';

export interface IConfigurationDescriptor extends IDescriptor, IDamResource {
  type: EntityType.CONFIGURATION;
}

export interface IDigestConfiguration extends IConfigurationDescriptor {
  description: string;
  payload: IConfigurationPayload;
}

export interface IConfigurationPayload {
  ageGroups: IRange[];
  detections: string[];
  asOf?: string;
}
