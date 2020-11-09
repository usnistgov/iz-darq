import { IDamResource } from 'ngx-dam-framework';
export interface IEmailTemplate extends IDamResource {
  type: EmailType;
  enabled: boolean;
  subject: string;
  template: string;
  params: string[];
}

export enum EmailType {
  ACCOUNT_APPROVED = 'ACCOUNT_APPROVED',
  ACCOUNT_LOCKED = 'ACCOUNT_LOCKED',
  ACCOUNT_UNLOCKED = 'ACCOUNT_UNLOCKED',
  ACCOUNT_PASSWORD_CHANGE = 'ACCOUNT_PASSWORD_CHANGE',
  ACCOUNT_ROLE_CHANGE = 'ACCOUNT_ROLE_CHANGE',
  REPORT_PUBLISHED = 'REPORT_PUBLISHED',
  ADF_UPLOADED = 'ADF_UPLOADED',
}
