import { EntityType } from './entity.model';
import { IDamResource } from '@usnistgov/ngx-dam-framework-legacy';

export interface IDescriptor extends IDamResource {
  id: string;
  type: EntityType;
  name: string;
  owner: string;
  ownerDisplayName: string;
  lastUpdated: Date;
  locked: boolean;
  published: boolean;
  public: boolean;
}
