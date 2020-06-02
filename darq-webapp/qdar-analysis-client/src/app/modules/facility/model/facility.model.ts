import { EntityType } from '../../shared/model/entity.model';
import { IDamResource } from 'ngx-dam-framework';


export interface IFacilityDescriptor extends IDamResource {
  id: string;
  type: EntityType.FACILITY;
  name: string;
  size: number;
}

export interface IFacility extends IFacilityDescriptor {
  members: string[];
}
