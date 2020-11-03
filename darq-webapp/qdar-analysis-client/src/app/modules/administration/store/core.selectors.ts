import { selectValue } from 'ngx-dam-framework';
import { AdminTabs } from '../components/admin-sidebar/admin-sidebar.component';

export const selectAdminActiveTab = selectValue<AdminTabs>('adminActiveTab');

