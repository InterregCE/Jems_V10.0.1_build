import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ProjectApplicationComponent} from './project-application/containers/project-application-page/project-application.component';
import {ProjectApplicationDetailComponent} from './project-application/containers/project-application-detail/project-application-detail.component';
import {AuthenticationGuard} from '../security/authentication-guard.service';
import {ProjectApplicationEligibilityDecisionComponent} from './project-application/components/project-application-detail/project-application-eligibility-decision/project-application-eligibility-decision.component';
import {ProjectApplicationQualityCheckComponent} from './project-application/components/project-application-detail/project-application-quality-check/project-application-quality-check.component';
import {ProjectApplicationEligibilityCheckComponent} from './project-application/components/project-application-detail/project-application-eligibility-check/project-application-eligibility-check.component';

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
    path: 'project/:projectId/eligibilityDecision',
    component: ProjectApplicationEligibilityDecisionComponent,
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'project/:projectId/qualityCheck',
    component: ProjectApplicationQualityCheckComponent,
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'project/:projectId/eligibilityCheck',
    component: ProjectApplicationEligibilityCheckComponent,
    canActivate: [AuthenticationGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProjectRoutingModule { }
