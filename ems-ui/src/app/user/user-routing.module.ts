import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {UserPageComponent} from './user-page/containers/user-page/user-page.component';
import {AuthenticationGuard} from '../security/authentication-guard.service';

const routes: Routes = [
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
