import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {UserPageComponent} from './user-page/user-page.component';
import {AuthenticationGuard} from '../../security/authentication-guard.service';
import {LoginComponent} from './login/login.component';

const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'login/expired',
    component: LoginComponent,
  },
  {
    path: 'users',
    component: UserPageComponent,
    canActivate: [AuthenticationGuard]
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
