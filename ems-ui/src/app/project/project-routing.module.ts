import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ProjectApplicationComponent} from './project-application/containers/project-application-page/project-application.component';
import {ProjectApplicationDetailComponent} from './project-application/components/project-application-detail/project-application-detail.component';
import {AuthenticationGuard} from '../security/authentication-guard.service';

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
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProjectRoutingModule { }
