import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {UserPageComponent} from './user-page/containers/user-page/user-page.component';
import {AuthenticationGuard} from '../security/authentication-guard.service';
import {Permission} from '../security/permissions/permission';
import {UserDetailComponent} from './user-page/containers/user-detail/user-detail.component';
import {RouteData} from '../common/utils/route-data';
import {PermissionGuard} from '../security/permission.guard';
import {UserNameBreadcrumbProvider} from './user-page/services/user-name-breadcrumb-provider.guard';
import {ReplaySubject} from 'rxjs';

const routes: Routes = [
  {
    path: 'user',
    data: new RouteData({
      breadcrumb: 'user.breadcrumb.create',
      permissionsOnly: [Permission.ADMINISTRATOR],
    }),
    children: [
      {
        path: '',
        component: UserPageComponent,
        canActivate: [AuthenticationGuard, PermissionGuard],
      },
      {
        path: ':userId',
        component: UserDetailComponent,
        canActivate: [AuthenticationGuard, UserNameBreadcrumbProvider],
        data: new RouteData({
          breadcrumb: UserNameBreadcrumbProvider.name,
          breadcrumb$: new ReplaySubject<string>(1)
        }),
      }
    ]
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule,
  ],
})
export class UserRoutingModule {
}
