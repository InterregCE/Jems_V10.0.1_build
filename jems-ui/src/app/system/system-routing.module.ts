import {Routes} from '@angular/router';
import {UserPageComponent} from './user-page/user-page.component';
import {UserNameResolver} from './user-page/user-detail-page/user-name.resolver';
import {PermissionGuard} from '../security/permission.guard';
import {Permission} from '../security/permissions/permission';
import {AuditLogComponent} from './audit-log/audit-log.component';
import {UserDetailPageComponent} from './user-page/user-detail-page/user-detail-page.component';
import {UserRoleDTO} from '@cat/api';
import {UserPageRoleComponent} from './user-page-role/user-page-role.component';
import {UserRoleDetailPageComponent} from './user-page-role/user-role-detail-page/user-role-detail-page.component';
import {UserRoleNameResolver} from './user-page-role/user-role-detail-page/user-role-name.resolver';

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
      {
        path: 'userRole',
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'userRole.breadcrumb.list',
          permissionsOnly: [
            UserRoleDTO.PermissionsEnum.RoleRetrieve,
            UserRoleDTO.PermissionsEnum.RoleCreate,
            UserRoleDTO.PermissionsEnum.RoleUpdate,
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
            canActivate: [PermissionGuard],
            data: {
              dynamicBreadcrumb: true,
              permissionsOnly: [
                UserRoleDTO.PermissionsEnum.RoleRetrieve,
              ]
            },
            resolve: {breadcrumb$: UserRoleNameResolver}
          },
        ]
      }
    ]
  }
];
