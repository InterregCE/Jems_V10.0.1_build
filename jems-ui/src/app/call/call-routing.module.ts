import {Routes} from '@angular/router';
import {CallPageComponent} from './containers/call-page/call-page.component';
import {RouteData} from '@common/utils/route-data';
import {PermissionGuard} from '../security/permission.guard';
import {CallNameResolver} from './services/call-name.resolver';
import {CallBudgetSettingsPageComponent} from './call-budget-settings-page/call-budget-settings-page.component';
import {CallDetailPageComponent} from './call-detail-page/call-detail-page.component';
import {UserRoleDTO} from '@cat/api';
import {ApplicationFormConfigurationPageComponent} from './application-form-configuration-page/application-form-configuration-page.component';

export const routes: Routes = [
  {
    path: '',
    data: {
      breadcrumb: 'call.breadcrumb.list.of.calls'
    },
    children: [
      {
        path: '',
        component: CallPageComponent,
        canActivate: [PermissionGuard],
        data: {
          permissionsOnly: [UserRoleDTO.PermissionsEnum.CallRetrieve],
        }
      },
      {
        path: 'create',
        component: CallDetailPageComponent,
        data: new RouteData({
          breadcrumb: 'call.breadcrumb.create',
          permissionsOnly: [UserRoleDTO.PermissionsEnum.CallUpdate],
        }),
        canActivate: [PermissionGuard],
      },
      {
        path: 'detail/:callId',
        data: {
          dynamicBreadcrumb: true,
          permissionsOnly: [
            UserRoleDTO.PermissionsEnum.CallRetrieve,
            UserRoleDTO.PermissionsEnum.CallPublishedRetrieve,
            UserRoleDTO.PermissionsEnum.ProjectCreate
          ],
        },
        resolve: {breadcrumb$: CallNameResolver},
        canActivate: [PermissionGuard],
        children: [
          {
            path: '',
            component: CallDetailPageComponent,
          },
          {
            path: 'budgetSettings',
            data: {
              breadcrumb: 'call.detail.budget.settings',
              permissionsOnly: [UserRoleDTO.PermissionsEnum.CallRetrieve],
            },
            canActivate: [PermissionGuard],
            component: CallBudgetSettingsPageComponent,
          },
          {
            path: 'applicationFormConfiguration',
            data: {
              breadcrumb: 'call.detail.application.form.config.title',
              permissionsOnly: [UserRoleDTO.PermissionsEnum.CallRetrieve],
            },
            canActivate: [PermissionGuard],
            component: ApplicationFormConfigurationPageComponent,
          },
        ],
      },
      {
        path: 'apply/:callId',
        pathMatch: 'full',
        redirectTo: '/app/project/applyTo/:callId',
      },
    ]
  },
];
