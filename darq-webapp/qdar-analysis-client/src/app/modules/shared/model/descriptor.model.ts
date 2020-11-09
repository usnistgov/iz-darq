import { EntityType } from './entity.model';
import { IDamResource } from 'ngx-dam-framework';

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
