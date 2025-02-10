import { EntityType } from '../../shared/model/entity.model';
import { IDamResource } from '@usnistgov/ngx-dam-framework-legacy';
import { IUser } from '../../core/model/user.model';


export interface IFacilityDescriptor extends IDamResource {
  id: string;
  type: EntityType.FACILITY;
  name: string;
  size: number;
}

export interface IUserFacilityDescriptor extends IFacilityDescriptor {
  reports: number;
  files: number;
  _private?: boolean;
}

export interface IFacility extends IFacilityDescriptor {
  members: string[];
}

export interface IFacilityContent extends IFacilityDescriptor {
  members: IUser[];
}
