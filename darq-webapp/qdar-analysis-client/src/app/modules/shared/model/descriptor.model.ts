import { EntityType } from './entity.model';
import { IDamResource } from 'ngx-dam-framework';

export interface IDescriptor extends IDamResource {
  id: string;
  type: EntityType;
  name: string;
  owner: string;
  lastUpdated: Date;
  viewOnly: boolean;
  locked: boolean;
  owned: boolean;
  published: boolean;
}
