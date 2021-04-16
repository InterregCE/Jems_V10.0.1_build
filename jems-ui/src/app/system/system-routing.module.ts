import {Routes} from '@angular/router';
import {UserPageComponent} from './user-page/user-page.component';
import {UserNameResolver} from './user-page/user-detail-page/user-name.resolver';
import {PermissionGuard} from '../security/permission.guard';
import {Permission} from '../security/permissions/permission';
import {AuditLogComponent} from './audit-log/audit-log.component';
import {UserDetailPageComponent} from './user-page/user-detail-page/user-detail-page.component';

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
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'user.breadcrumb.create',
          permissionsOnly: [Permission.ADMINISTRATOR],
        },
        children: [
          {
            path: '',
            component: UserPageComponent,
          },
          {
            path: 'detail/create',
            component: UserDetailPageComponent,
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'user.create.header',
              permissionsOnly: [Permission.ADMINISTRATOR],
            },
          },
          {
            path: 'detail/:userId',
            component: UserDetailPageComponent,
            data: {dynamicBreadcrumb: true},
            resolve: {breadcrumb$: UserNameResolver}
          },
        ]
      },
    ]
  }
];
