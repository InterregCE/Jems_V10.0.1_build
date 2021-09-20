import {Routes} from '@angular/router';
import {UserPageComponent} from './user-page/user-page.component';
import {UserNameResolver} from './user-page/user-detail-page/user-name.resolver';
import {PermissionGuard} from '../security/permission.guard';
import {AuditLogComponent} from './audit-log/audit-log.component';
import {UserDetailPageComponent} from './user-page/user-detail-page/user-detail-page.component';
import {UserRoleDTO} from '@cat/api';
import {UserPageRoleComponent} from './user-page-role/user-page-role.component';
import {UserRoleDetailPageComponent} from './user-page-role/user-role-detail-page/user-role-detail-page.component';
import {UserRoleNameResolver} from './user-page-role/user-role-detail-page/user-role-name.resolver';

export const routes: Routes = [
  {
    path: '',
    data: {
      breadcrumb: 'system.page.title',
    },
    children: [
      {
        path: 'audit',
        component: AuditLogComponent,
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'audit.list.title',
          permissionsOnly: [UserRoleDTO.PermissionsEnum.AuditRetrieve],
        },
      },
      {
        path: 'user',
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'user.breadcrumb.create',
          permissionsOnly: [UserRoleDTO.PermissionsEnum.UserRetrieve],
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
              permissionsOnly: [
                UserRoleDTO.PermissionsEnum.UserCreate,
              ],
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
      {
        path: 'role',
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'userRole.breadcrumb.list',
          permissionsOnly: [
            UserRoleDTO.PermissionsEnum.RoleRetrieve,
          ]
        },
        children: [
          {
            path: '',
            component: UserPageRoleComponent,
          },
          {
            path: 'create',
            component: UserRoleDetailPageComponent,
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'userRole.breadcrumb.create',
              permissionsOnly: [
                UserRoleDTO.PermissionsEnum.RoleCreate,
              ]
            },
          },
          {
            path: 'detail/:roleId',
            component: UserRoleDetailPageComponent,
            data: {dynamicBreadcrumb: true},
            resolve: {breadcrumb$: UserRoleNameResolver}
          },
        ]
      }
    ]
  }
];
