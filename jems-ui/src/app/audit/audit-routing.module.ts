import {Routes} from '@angular/router';
import {AuditLogComponent} from './audit-log/audit-log.component';
import {Permission} from '../security/permissions/permission';
import {PermissionGuard} from '../security/permission.guard';

export const routes: Routes = [
  {
    path: '',
    canActivate: [PermissionGuard],
    data: {
      permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
      breadcrumb: 'audit.list.title'
    },
    children: [
      {
        path: '',
        component: AuditLogComponent,
      },
    ]
  }
];
