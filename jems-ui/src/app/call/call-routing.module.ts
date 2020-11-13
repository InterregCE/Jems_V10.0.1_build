import {Routes} from '@angular/router';
import {CallPageComponent} from './containers/call-page/call-page.component';
import {CallConfigurationComponent} from './containers/call-configuration/call-configuration.component';
import {Permission} from '../security/permissions/permission';
import {RouteData} from '../common/utils/route-data';
import {PermissionGuard} from '../security/permission.guard';
import {CallNameResolver} from './services/call-name.resolver';
import {CallFlatRatesPageComponent} from './containers/call-flat-rates-page/call-flat-rates-page.component';
import {ProjectApplicationFormPartnerSectionComponent} from '../project/project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-partner-section.component';

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
          permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
        }),
        canActivate: [PermissionGuard],
      },
      {
        path: 'detail/:callId',
        data: {
          dynamicBreadcrumb: true,
          permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
        },
        resolve: {breadcrumb$: CallNameResolver},
        canActivate: [PermissionGuard],
        children: [
          {
            path: '',
            component: CallConfigurationComponent,
          },
          {
            path: 'flatRates',
            data: {
              breadcrumb: 'call.detail.flat.rates',
              permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
            },
            canActivate: [PermissionGuard],
            component: CallFlatRatesPageComponent,
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
