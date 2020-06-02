import { IDamResource } from 'ngx-dam-framework';
import { EntityType } from '../../shared/model/entity.model';

export interface IUserResource extends IDamResource {
  id: string;
  type: EntityType.USER;
  username: string;
  authorities: string[];
  email: string;
  pending: boolean;
  fullName: string;
  organization: string;
}


export interface IUserAccountRegister {
  username: string;
  email: string;
  fullName: string;
  organization: string;
  password: string;
  signedConfidentialityAgreement: boolean;
}
