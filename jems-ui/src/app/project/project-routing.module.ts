import {Routes} from '@angular/router';
import {ProjectApplicationComponent} from './project-application/containers/project-application-page/project-application.component';
import {ProjectApplicationDetailComponent} from './project-application/containers/project-application-detail/project-application-detail.component';
import {ProjectApplicationQualityCheckComponent} from './project-application/components/project-application-detail/project-application-quality-check/project-application-quality-check.component';
import {ProjectApplicationEligibilityCheckComponent} from './project-application/components/project-application-detail/project-application-eligibility-check/project-application-eligibility-check.component';
import {ProjectApplicationFundingPageComponent} from './project-application/containers/project-application-detail/project-application-funding-page/project-application-funding-page.component';
import {ProjectApplicationEligibilityDecisionPageComponent} from './project-application/containers/project-application-detail/project-application-eligibility-decision-page/project-application-eligibility-decision-page.component';
import {ProjectPartnerDetailPageComponent} from './partner/project-partner-detail-page/project-partner-detail-page.component';
import {ProjectAcronymResolver} from './project-application/containers/project-application-detail/services/project-acronym.resolver';
import {PermissionGuard} from '../security/permission.guard';
import {Permission} from '../security/permissions/permission';
import {ProjectApplyToCallComponent} from './project-application/containers/project-application-page/project-apply-to-call.component';
import {ProjectApplicationPartnerIdentityComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-partner-identity/project-application-partner-identity.component';
import {ProjectApplicationFormAssociatedOrgDetailComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-associated-org-detail/project-application-form-associated-org-detail.component';
import {ProjectApplicationFormIdentificationPageComponent} from './project-application/containers/project-application-form-page/project-application-form-identification-page/project-application-form-identification-page.component';
import {ProjectApplicationFormPartnerSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-partner-section.component';
import {ProjectApplicationFormAssociatedOrgPageComponent} from './project-application/containers/project-application-form-page/project-application-form-associated-org-page/project-application-form-associated-org-page.component';
import {ProjectApplicationFormOverallObjectiveSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-overall-objective-section/project-application-form-overall-objective-section.component';
import {ProjectApplicationFormProjectRelevanceAndContextSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-project-relevance-and-context-section/project-application-form-project-relevance-and-context-section.component';
import {ProjectApplicationFormProjectPartnershipSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-project-partnership-section/project-application-form-project-partnership-section.component';
import {ProjectApplicationFormWorkPackageSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-work-package-section/project-application-form-work-package-section.component';
import {ProjectApplicationFormFuturePlansSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-future-plans-section/project-application-form-future-plans-section.component';
import {ProjectApplicationFormManagementSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-management-section/project-application-form-management-section.component';
import {BudgetPageComponent} from './budget/budget-page/budget-page.component';
import {ProjectWorkPackageDetailPageComponent} from './work-package/work-package-detail-page/project-work-package-detail-page.component';
import {ProjectWorkPackageInvestmentDetailPageComponent} from './work-package/work-package-detail-page/project-work-package-investments-tab/project-work-package-investment-detail-page/project-work-package-investment-detail-page.component';

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
        data: {dynamicBreadcrumb: true},
        resolve: {breadcrumb$: ProjectAcronymResolver},
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
            path: 'applicationFormIdentification',
            data: {breadcrumb: 'project.breadcrumb.applicationForm.identification'},
            component: ProjectApplicationFormIdentificationPageComponent,
          },
          {
            path: 'applicationFormPartner',
            data: {breadcrumb: 'project.breadcrumb.applicationForm.partner'},
            children: [
              {
                path: '',
                component: ProjectApplicationFormPartnerSectionComponent,
              },
              {
                path: 'create',
                component: ProjectApplicationPartnerIdentityComponent,
                data: {breadcrumb: 'project.breadcrumb.partnerCreate'},
              },
              {
                path: 'detail/:partnerId',
                component: ProjectPartnerDetailPageComponent,
                data: {breadcrumb: 'project.breadcrumb.partnerName'},
              },
            ]
          },
          {
            path: 'applicationFormAssociatedOrganization',
            data: {breadcrumb: 'project.breadcrumb.applicationForm.associated.org'},
            children: [
              {
                path: '',
                component: ProjectApplicationFormAssociatedOrgPageComponent,
              },
              {
                path: 'create',
                component: ProjectApplicationFormAssociatedOrgDetailComponent,
                data: {breadcrumb: 'project.breadcrumb.associatedOrganizationCreate'},
              },
              {
                path: 'detail/:associatedOrganizationId',
                component: ProjectApplicationFormAssociatedOrgDetailComponent,
                data: {breadcrumb: 'project.breadcrumb.associatedOrganizationName'},
              },
            ]
          },
          {
            path: 'applicationFormOverallObjective',
            component: ProjectApplicationFormOverallObjectiveSectionComponent,
            data: {breadcrumb: 'project.breadcrumb.applicationForm.overallObjective'},
          },
          {
            path: 'applicationFormRelevanceAndContext',
            component: ProjectApplicationFormProjectRelevanceAndContextSectionComponent,
            data: {breadcrumb: 'project.breadcrumb.applicationForm.relevanceAndContext'},
          },
          {
            path: 'applicationFormPartnership',
            component: ProjectApplicationFormProjectPartnershipSectionComponent,
            data: {breadcrumb: 'project.breadcrumb.applicationForm.partnership'},
          },
          {
            path: 'applicationFormWorkPackage',
            data: {breadcrumb: 'project.breadcrumb.applicationForm.workPackage'},
            children: [
              {
                path: '',
                component: ProjectApplicationFormWorkPackageSectionComponent
              },
              {
                path: 'create',
                component: ProjectWorkPackageDetailPageComponent,
                data: {breadcrumb: 'project.breadcrumb.workPackageCreate'},
              },
              {
                path: 'detail/:workPackageId',
                data: {breadcrumb: 'project.breadcrumb.workPackageName'},
                children: [
                  {
                    path: '',
                    component: ProjectWorkPackageDetailPageComponent
                  },
                  {
                    path: 'investment/create',
                    component: ProjectWorkPackageInvestmentDetailPageComponent,
                    data: {breadcrumb: 'project.breadcrumb.workPackageInvestment.create'},
                  },
                  {
                    path: 'investment/detail/:workPackageInvestmentId',
                    component: ProjectWorkPackageInvestmentDetailPageComponent,
                    data: {breadcrumb: 'project.breadcrumb.workPackageInvestment.name'},
                  }
                ]
              },

            ]
          },
          {
            path: 'applicationFormFuturePlans',
            component: ProjectApplicationFormFuturePlansSectionComponent,
            data: {breadcrumb: 'project.breadcrumb.applicationForm.futurePlans'},
          },
          {
            path: 'applicationFormManagement',
            component: ProjectApplicationFormManagementSectionComponent,
            data: {breadcrumb: 'project.breadcrumb.applicationForm.management'},
          },
          {
            path: 'applicationFormBudget',
            data: {breadcrumb: 'project.breadcrumb.applicationForm.budget'},
            component: BudgetPageComponent,
          },
        ]
      },
    ]
  },
];
