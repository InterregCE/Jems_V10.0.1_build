import {Routes} from '@angular/router';
import {CallPageComponent} from './containers/call-page/call-page.component';
import {CallConfigurationComponent} from './containers/call-configuration/call-configuration.component';
import {Permission} from '../security/permissions/permission';
import {RouteData} from '../common/utils/route-data';
import {PermissionGuard} from '../security/permission.guard';
import {CallNameResolver} from './services/call-name.resolver';
import {CallBudgetSettingsPageComponent} from './containers/call-budget-settings-page/call-budget-settings-page.component';

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
      },
      {
        path: 'create',
        component: CallConfigurationComponent,
        data: new RouteData({
          breadcrumb: 'call.breadcrumb.create',
        }),
        canActivate: [PermissionGuard],
      },
      {
        path: 'detail/:callId',
        data: {
          dynamicBreadcrumb: true,
        },
        resolve: {breadcrumb$: CallNameResolver},
        canActivate: [PermissionGuard],
        children: [
          {
            path: '',
            component: CallConfigurationComponent,
          },
          {
            path: 'budgetSettings',
            data: {
              breadcrumb: 'call.detail.budget.settings',
              permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
            },
            canActivate: [PermissionGuard],
            component: CallBudgetSettingsPageComponent,
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
