import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProjectApplicationComponent} from './components/project-application/project-application.component';
import {ProjectApplicationDetailComponent} from './components/project-application/project-application-detail/project-application-detail.component';
import {AuthenticationGuard} from './security/authentication-guard.service';
import {LoginComponent} from './components/user/login/login.component';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: ProjectApplicationComponent,
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'project/:projectId',
    component: ProjectApplicationDetailComponent,
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'login',
    component: LoginComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
