import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProjectApplicationComponent} from './project-application/containers/project-application-page/project-application.component';
import {ProjectApplicationDetailComponent} from './project-application/containers/project-application-detail/project-application-detail.component';
import {AuthenticationGuard} from '../security/authentication-guard.service';
import {ProjectApplicationQualityCheckComponent} from './project-application/components/project-application-detail/project-application-quality-check/project-application-quality-check.component';
import {ProjectApplicationEligibilityCheckComponent} from './project-application/components/project-application-detail/project-application-eligibility-check/project-application-eligibility-check.component';
import {ProjectApplicationFundingPageComponent} from './project-application/containers/project-application-detail/project-application-funding-page/project-application-funding-page.component';
import {ProjectApplicationEligibilityDecisionPageComponent} from './project-application/containers/project-application-detail/project-application-eligibility-decision-page/project-application-eligibility-decision-page.component';
import {WorkPackageDetailsComponent} from './project-application/containers/project-application-form-page/project-application-form-work-package-section/work-package-details/work-package-details.component';
import {ProjectApplicationFormPageComponent} from './project-application/containers/project-application-form-page/project-application-form-page.component';
import {ProjectApplicationFormPartnerDetailComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-partner-detail/project-application-form-partner-detail.component';
import {Breadcrumb} from '@common/components/breadcrumb/breadcrumb';
import {RouteData} from '../common/utils/route-data';
import {Permission} from '../security/permissions/permission';
import {PermissionGuard} from '../security/permission.guard';
import {ProjectAcronymBreadcrumbProvider} from './project-application/containers/project-application-detail/services/project-acronym-breadcrumb-provider.guard';
import {ReplaySubject} from 'rxjs';

/**
 * TODO Use the PermissionGuard to limit access to routes where it makes sense
 * and cleanup the pages (remove *ngxPermission..)
 */
const routes: Routes = [
  {
    path: 'project',
    data: new RouteData({
      breadcrumb: 'project.breadcrumb.list',
      permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER]
    }),
    children: [
      {
        path: '',
        component: ProjectApplicationComponent,
        canActivate: [AuthenticationGuard, PermissionGuard],
      },
      {
        path: ':projectId',
        data: new RouteData({
          breadcrumb: ProjectAcronymBreadcrumbProvider.name,
          breadcrumb$: new ReplaySubject<string>(1)
        }),
        canActivate: [AuthenticationGuard, ProjectAcronymBreadcrumbProvider],
        children: [
          {
            path: '',
            component: ProjectApplicationDetailComponent,
          },
          {
            path: 'eligibilityDecision',
            component: ProjectApplicationEligibilityDecisionPageComponent,
            data: new RouteData({
              breadcrumb: 'project.breadcrumb.eligibilityDecision',
            }),
            canActivate: [AuthenticationGuard]
          },
          {
            path: 'qualityCheck',
            component: ProjectApplicationQualityCheckComponent,
            data: new RouteData({
              breadcrumb: 'project.breadcrumb.qualityCheck',
            }),
            canActivate: [AuthenticationGuard]
          },
          {
            path: 'eligibilityCheck',
            component: ProjectApplicationEligibilityCheckComponent,
            data: new RouteData({
              breadcrumb: 'project.breadcrumb.eligibilityCheck',
            }),
            canActivate: [AuthenticationGuard]
          },
          {
            path: 'fundingDecision',
            component: ProjectApplicationFundingPageComponent,
            data: new RouteData({
              breadcrumb: 'project.breadcrumb.fundingDecision',
              permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER]
            }),
            canActivate: [AuthenticationGuard, PermissionGuard]
          },
          {
            path: 'applicationForm',
            data: new RouteData({
              breadcrumb: 'project.breadcrumb.applicationForm',
            }),
            children: [
              {
                path: '',
                component: ProjectApplicationFormPageComponent,
                canActivate: [AuthenticationGuard],
              },
              {
                path: 'partner',
                data: new RouteData({
                  breadcrumb: Breadcrumb.DO_NOT_SHOW,
                }),
                children: [
                  {
                    path: 'create',
                    component: ProjectApplicationFormPartnerDetailComponent,
                    data: new RouteData({
                      breadcrumb: 'project.breadcrumb.partnerCreate',
                    }),
                    canActivate: [AuthenticationGuard],
                  },
                  {
                    path: ':partnerId',
                    component: ProjectApplicationFormPartnerDetailComponent,
                    data: new RouteData({
                      breadcrumb: 'project.breadcrumb.partnerName',
                    }),
                    canActivate: [AuthenticationGuard]
                  },
                ]
              },
              {
                path: 'workPackage',
                data: new RouteData({
                  breadcrumb: Breadcrumb.DO_NOT_SHOW,
                }),
                children: [
                  {
                    path: 'create',
                    component: WorkPackageDetailsComponent,
                    data: new RouteData({
                      breadcrumb: 'project.breadcrumb.workPackageCreate',
                    }),
                    canActivate: [AuthenticationGuard],
                  },
                  {
                    path: ':workPackageId',
                    component: WorkPackageDetailsComponent,
                    data: new RouteData({
                      breadcrumb: 'project.breadcrumb.workPackageName',
                    }),
                    canActivate: [AuthenticationGuard]
                  }
                ]
              },
            ]
          },
        ]
      },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProjectRoutingModule {
}
