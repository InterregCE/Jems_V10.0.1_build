import {RouterModule, Routes} from '@angular/router';
import {AuthenticationGuard} from '../security/authentication-guard.service';
import {NgModule} from '@angular/core';
import {CallPageComponent} from './containers/call-page/call-page.component';
import {CallConfigurationComponent} from './containers/call-configuration/call-configuration.component';
import {ProjectApplicationComponent} from '../project/project-application/containers/project-application-page/project-application.component';
import {Permission} from '../security/permissions/permission';
import {RouteData} from '../common/utils/route-data';
import {PermissionGuard} from '../security/permission.guard';
import {CallNameBreadcrumbProvider} from './services/call-name-breadcrumb-provider.guard';
import {ReplaySubject} from 'rxjs';

const routes: Routes = [
  {
    path: 'calls',
    data: new RouteData({
      breadcrumb: 'call.breadcrumb.list.of.calls'
    }),
    children: [
      {
        path: '',
        component: CallPageComponent,
        canActivate: [AuthenticationGuard],
      },
      {
        path: 'create',
        component: CallConfigurationComponent,
        data: new RouteData({
          breadcrumb: 'call.breadcrumb.create',
          permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
        }),
        canActivate: [AuthenticationGuard, PermissionGuard],
      },
      {
        path: ':callId',
        data: new RouteData({
          breadcrumb: CallNameBreadcrumbProvider.name,
          breadcrumb$: new ReplaySubject<string>(1),
          permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
        }),
        children: [
          {
            path: '',
            component: CallConfigurationComponent,
            canActivate: [AuthenticationGuard, PermissionGuard, CallNameBreadcrumbProvider]
          },
          {
            path: 'apply',
            component: ProjectApplicationComponent,
            data: new RouteData({
              breadcrumb: 'call.breadcrumb.apply',
              permissionsOnly: [Permission.APPLICANT_USER],
            }),
            canActivate: [AuthenticationGuard, PermissionGuard],
          }
        ]
      },
    ]
  },
]

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule,
  ],
})
export class CallRoutingModule {
}
