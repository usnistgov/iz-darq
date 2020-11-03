import { IDamResource, IDamUser } from 'ngx-dam-framework';
import { EntityType } from '../../shared/model/entity.model';
import { ResourceType } from './resouce-type.enum';
import { Scope } from './scope.enum';
import { Action } from './action.enum';

export interface IUser extends IDamResource, IDamUser {
  id: string;
  type: EntityType.USER;
  username: string;
  roles: string[];
  email: string;
  name: string;
  organization: string;
  administrator: boolean;
  source: string;
  screenName: string;
  credentials: boolean;
  issuerIdList: {
    issuer: string;
    userId: string;
  }[];
  verified: boolean;
  status: AccountStatus;
}

export interface ICurrentUser extends IUser {
  permissions: IPermissionSet;
}

export interface IPermissionSet {
  facilities: string[];
  permissions: string[];
  authorize: {
    [scope: string]: {
      [resourceType: string]: {
        [tokey: string]: Action[];
      }
    }
  };
}

export interface IAuthorizeAction {
  resourceType: ResourceType;
  actions: IScopedAction[];
}

export interface IScopedAction {
  scope: Scope;
  action: Action;
}

export enum AccountStatus {
  ACTIVE = 'ACTIVE',
  LOCKED = 'LOCKED',
  PENDING = 'PENDING'
}

export interface IUserAccountRegister {
  username: string;
  email: string;
  fullName: string;
  organization: string;
  password: string;
  signedConfidentialityAgreement: boolean;
}

export interface ICreateCredentials {
  id: string;
  username: string;
  password: string;
}

