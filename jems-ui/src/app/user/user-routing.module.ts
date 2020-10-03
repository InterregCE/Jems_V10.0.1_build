import {Routes} from '@angular/router';
import {UserPageComponent} from './user-page/containers/user-page/user-page.component';
import {UserDetailComponent} from './user-page/containers/user-detail/user-detail.component';
import {UserNameResolver} from './user-page/services/user-name.resolver';

export const routes: Routes = [
  {
    path: '',
    data: {
      breadcrumb: 'user.breadcrumb.create',
    },
    children: [
      {
        path: '',
        component: UserPageComponent,
      },
      {
        path: 'detail/:userId',
        component: UserDetailComponent,
        data: {dynamicBreadcrumb: true},
        resolve: {breadcrumb$: UserNameResolver}
      }
    ]
  }
];
