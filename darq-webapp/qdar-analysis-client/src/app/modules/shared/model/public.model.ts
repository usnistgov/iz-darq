import { IDamResource } from 'ngx-dam-framework';

export interface IDetectionDescriptor {
  description: string;
  target: string;
  active: boolean;
}

export interface IDetectionMap {
  [code: string]: IDetectionDescriptor;
}

export interface ICvxCode {
  cvx: string;
  name: string;
}

export interface ICvxMap {
  [code: string]: ICvxCode;
}

export interface IDetectionResource extends IDamResource, IDetectionDescriptor { }
export interface ICvxResource extends IDamResource, ICvxCode { }
