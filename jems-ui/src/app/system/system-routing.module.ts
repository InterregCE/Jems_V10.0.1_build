import {Routes} from '@angular/router';
import {UserPageComponent} from './user-page/containers/user-page/user-page.component';
import {UserDetailComponent} from './user-page/containers/user-detail/user-detail.component';
import {UserNameResolver} from './user-page/services/user-name.resolver';
import {PermissionGuard} from '../security/permission.guard';
import {Permission} from '../security/permissions/permission';
import {AuditLogComponent} from './audit-log/audit-log.component';

export const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        component: AuditLogComponent,
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'audit.list.title',
          permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
        },
      },
      {
        path: 'user',
        component: UserPageComponent,
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'user.breadcrumb.create',
          permissionsOnly: [Permission.ADMINISTRATOR],
        },
      },
      {
        path: 'user/detail/:userId',
        component: UserDetailComponent,
        data: {dynamicBreadcrumb: true},
        resolve: {breadcrumb$: UserNameResolver}
      },
    ]
  }
];
