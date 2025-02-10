import { selectValue } from '@usnistgov/ngx-dam-framework-legacy';
import { AdminTabs } from '../components/admin-sidebar/admin-sidebar.component';

export const selectAdminActiveTab = selectValue<AdminTabs>('adminActiveTab');

