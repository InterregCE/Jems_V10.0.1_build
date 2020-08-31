import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProjectApplicationComponent} from './project-application/containers/project-application-page/project-application.component';
import {ProjectApplicationDetailComponent} from './project-application/containers/project-application-detail/project-application-detail.component';
import {AuthenticationGuard} from '../security/authentication-guard.service';
import {ProjectApplicationQualityCheckComponent} from './project-application/components/project-application-detail/project-application-quality-check/project-application-quality-check.component';
import {ProjectApplicationEligibilityCheckComponent} from './project-application/components/project-application-detail/project-application-eligibility-check/project-application-eligibility-check.component';
import {ProjectApplicationFundingPageComponent} from './project-application/containers/project-application-detail/project-application-funding-page/project-application-funding-page.component';
import {ProjectApplicationEligibilityDecisionPageComponent} from './project-application/containers/project-application-detail/project-application-eligibility-decision-page/project-application-eligibility-decision-page.component';
import {ProjectApplicationFormPageComponent} from './project-application/containers/project-application-form-page/project-application-form-page.component';
import {ProjectApplicationFormPartnerDetailComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-partner-detail/project-application-form-partner-detail.component';

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
    component: ProjectApplicationEligibilityDecisionPageComponent,
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
  },
  {
    path: 'project/:projectId/fundingDecision',
    component: ProjectApplicationFundingPageComponent,
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'project/:projectId/applicationForm',
    component: ProjectApplicationFormPageComponent,
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'project/:projectId/partner',
    component: ProjectApplicationFormPartnerDetailComponent,
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'project/:projectId/partner/:partnerId',
    component: ProjectApplicationFormPartnerDetailComponent,
    canActivate: [AuthenticationGuard]
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProjectRoutingModule {
}
