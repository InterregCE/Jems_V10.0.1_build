import {Routes} from '@angular/router';
import {UserDetailPageComponent} from '../system/user-page/user-detail-page/user-detail-page.component';

export const routes: Routes = [
  {
    path: '',
    component: UserDetailPageComponent,
    data: {
      breadcrumb: 'user.breadcrumb.your.profile'
    },
  }
];
