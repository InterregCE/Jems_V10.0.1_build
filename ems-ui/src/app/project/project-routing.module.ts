import {Routes} from '@angular/router';
import {ProjectApplicationComponent} from './project-application/containers/project-application-page/project-application.component';
import {ProjectApplicationDetailComponent} from './project-application/containers/project-application-detail/project-application-detail.component';
import {ProjectApplicationQualityCheckComponent} from './project-application/components/project-application-detail/project-application-quality-check/project-application-quality-check.component';
import {ProjectApplicationEligibilityCheckComponent} from './project-application/components/project-application-detail/project-application-eligibility-check/project-application-eligibility-check.component';
import {ProjectApplicationFundingPageComponent} from './project-application/containers/project-application-detail/project-application-funding-page/project-application-funding-page.component';
import {ProjectApplicationEligibilityDecisionPageComponent} from './project-application/containers/project-application-detail/project-application-eligibility-decision-page/project-application-eligibility-decision-page.component';
import {WorkPackageDetailsComponent} from './project-application/containers/project-application-form-page/project-application-form-work-package-section/work-package-details/work-package-details.component';
import {ProjectApplicationFormPageComponent} from './project-application/containers/project-application-form-page/project-application-form-page.component';
import {ProjectApplicationFormPartnerDetailComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-partner-detail/project-application-form-partner-detail.component';
import {ProjectAcronymResolver} from './project-application/containers/project-application-detail/services/project-acronym.resolver';
import {PermissionGuard} from '../security/permission.guard';
import {Permission} from '../security/permissions/permission';
import {ProjectApplyToCallComponent} from './project-application/containers/project-application-page/project-apply-to-call.component';

export const routes: Routes = [
  {
    path: '',
    data: {breadcrumb: 'project.breadcrumb.list'},
    children: [
      {
        path: '',
        component: ProjectApplicationComponent,
      },
      {
        path: 'applyTo/:callId',
        component: ProjectApplyToCallComponent,
        data: {breadcrumb: 'call.breadcrumb.apply'}
      },
      {
        path: 'detail/:projectId',
        data: {dynamicValue: true},
        resolve: {dynamicValue: ProjectAcronymResolver},
        children: [
          {
            path: '',
            component: ProjectApplicationDetailComponent,
          },
          {
            path: 'assessment',
            data: {
              skipBreadcrumb: true,
              permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
            },
            canActivate: [PermissionGuard],
            children: [
              {
                path: 'eligibilityDecision',
                component: ProjectApplicationEligibilityDecisionPageComponent,
                data: {
                  breadcrumb: 'project.breadcrumb.eligibilityDecision',
                  permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
                },
                canActivate: [PermissionGuard]
              },
              {
                path: 'qualityCheck',
                component: ProjectApplicationQualityCheckComponent,
                data: {
                  breadcrumb: 'project.breadcrumb.qualityCheck',
                  permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
                },
              },
              {
                path: 'eligibilityCheck',
                component: ProjectApplicationEligibilityCheckComponent,
                data: {
                  breadcrumb: 'project.breadcrumb.eligibilityCheck',
                  permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
                },
                canActivate: [PermissionGuard]
              },
              {
                path: 'fundingDecision',
                component: ProjectApplicationFundingPageComponent,
                data: {
                  breadcrumb: 'project.breadcrumb.fundingDecision',
                  permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
                },
                canActivate: [PermissionGuard]
              },
            ],
          },
          {
            path: 'applicationForm',
            data: {breadcrumb: 'project.breadcrumb.applicationForm'},
            children: [
              {
                path: '',
                component: ProjectApplicationFormPageComponent,
              },
              {
                path: 'partner',
                children: [
                  {
                    path: 'create',
                    component: ProjectApplicationFormPartnerDetailComponent,
                    data: {breadcrumb: 'project.breadcrumb.partnerCreate'},
                  },
                  {
                    path: 'detail/:partnerId',
                    component: ProjectApplicationFormPartnerDetailComponent,
                    data: {breadcrumb: 'project.breadcrumb.partnerName'},
                  },
                ]
              },
              {
                path: 'workPackage',
                children: [
                  {
                    path: 'create',
                    component: WorkPackageDetailsComponent,
                    data: {breadcrumb: 'project.breadcrumb.workPackageCreate'},
                  },
                  {
                    path: 'detail/:workPackageId',
                    component: WorkPackageDetailsComponent,
                    data: {breadcrumb: 'project.breadcrumb.workPackageName'},
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
