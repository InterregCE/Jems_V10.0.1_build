import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {UserPageComponent} from './user-page/containers/user-page/user-page.component';
import {AuthenticationGuard} from '../security/authentication-guard.service';
import {NgxPermissionsGuard} from 'ngx-permissions';
import {Permission} from '../security/permissions/permission';

const routes: Routes = [
  {
    path: 'users',
    component: UserPageComponent,
    canActivate: [AuthenticationGuard, NgxPermissionsGuard],
    data: {
      permissions: {
        only: [Permission.ADMINISTRATOR],
        redirectTo: '/'
      }
    }
  },
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
