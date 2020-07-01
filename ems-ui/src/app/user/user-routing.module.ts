import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {UserPageComponent} from './user-page/containers/user-page/user-page.component';
import {AuthenticationGuard} from '../security/authentication-guard.service';
import {NgxPermissionsGuard} from 'ngx-permissions';
import {Permission} from '../security/permissions/permission';
import {UserDetailComponent} from './user-page/containers/user-detail/user-detail.component';

const userGuard = {
  permissions: {
    only: [Permission.ADMINISTRATOR],
    redirectTo: '/'
  }
};

const routes: Routes = [
  {
    path: 'user',
    component: UserPageComponent,
    canActivate: [AuthenticationGuard, NgxPermissionsGuard],
    data: userGuard
  },
  {
    path: 'user/:userId',
    component: UserDetailComponent,
    canActivate: [AuthenticationGuard],
    data: userGuard
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
