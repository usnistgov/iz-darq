import { IHomePage } from '../../core/services/web-content.service';

export interface IWebContent {
  id: string;
  homePage: IHomePage;
  registerTermsAndConditions: string;
  uploadTermsAndConditions: string;
}
