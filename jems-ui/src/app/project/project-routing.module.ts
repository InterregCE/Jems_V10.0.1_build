import {Routes} from '@angular/router';
import {ProjectApplicationComponent} from './project-application/containers/project-application-page/project-application.component';
import {ProjectApplicationQualityCheckComponent} from './project-detail-page/project-application-quality-check/project-application-quality-check.component';
import {ProjectApplicationEligibilityCheckComponent} from './project-detail-page/project-application-eligibility-check/project-application-eligibility-check.component';
import {ProjectApplicationFundingPageComponent} from './project-detail-page/project-application-funding-page/project-application-funding-page.component';
import {ProjectApplicationEligibilityDecisionPageComponent} from './project-detail-page/project-application-eligibility-decision-page/project-application-eligibility-decision-page.component';
import {ProjectPartnerDetailPageComponent} from './partner/project-partner-detail-page/project-partner-detail-page.component';
import {ProjectAcronymResolver} from './project-application/containers/project-application-detail/services/project-acronym.resolver';
import {PermissionGuard} from '../security/permission.guard';
import {ProjectApplyToCallComponent} from './project-application/containers/project-application-page/project-apply-to-call.component';
import {ProjectApplicationPartnerIdentityComponent} from '@project/project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-partner-identity/project-application-partner-identity.component';
import {ProjectApplicationFormAssociatedOrgDetailComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-associated-org-detail/project-application-form-associated-org-detail.component';
import {ProjectApplicationFormIdentificationPageComponent} from './project-application/containers/project-application-form-page/project-application-form-identification-page/project-application-form-identification-page.component';
import {ProjectApplicationFormPartnerSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-partner-section.component';
import {ProjectApplicationFormAssociatedOrgPageComponent} from './project-application/containers/project-application-form-page/project-application-form-associated-org-page/project-application-form-associated-org-page.component';
import {ProjectApplicationFormOverallObjectiveSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-overall-objective-section/project-application-form-overall-objective-section.component';
import {ProjectApplicationFormProjectRelevanceAndContextSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-project-relevance-and-context-section/project-application-form-project-relevance-and-context-section.component';
import {ProjectApplicationFormProjectPartnershipSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-project-partnership-section/project-application-form-project-partnership-section.component';
import {ProjectApplicationFormFuturePlansSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-future-plans-section/project-application-form-future-plans-section.component';
import {ProjectApplicationFormManagementSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-management-section/project-application-form-management-section.component';
import {BudgetPageComponent} from './budget/budget-page/budget-page.component';
import {ProjectWorkPackageDetailPageComponent} from './work-package/project-work-package-page/work-package-detail-page/project-work-package-detail-page.component';
import {ProjectWorkPackageInvestmentDetailPageComponent} from './work-package/project-work-package-page/work-package-detail-page/project-work-package-investments-tab/project-work-package-investment-detail-page/project-work-package-investment-detail-page.component';
import {ProjectResultsPageComponent} from './results/project-results-page/project-results-page.component';
import {ProjectLumpSumsPageComponent} from './lump-sums/project-lump-sums-page/project-lump-sums-page.component';
import {BudgetPagePerPartnerComponent} from './budget/budget-page-per-partner/budget-page-per-partner.component';
import {ProjectTimeplanPageComponent} from './timeplan/project-timeplan-page/project-timeplan-page.component';
import {ProjectDetailPageComponent} from './project-detail-page/project-detail-page.component';
import {UserRoleDTO} from '@cat/api';
import {ProjectWorkPackagePageComponent} from './work-package/project-work-package-page/project-work-package-page.component';
import {ProjectApplicationFormRegionSelectionComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-region-selection/project-application-form-region-selection.component';
import {ProjectApplicationFormPartnerEditComponent} from '@project/project-application/components/project-application-form/project-application-form-partner-edit/project-application-form-partner-edit.component';
import {ProjectPartnerBudgetTabComponent} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget-tab.component';
import {ProjectApplicationFormPartnerContactComponent} from '@project/project-application/components/project-application-form/project-application-form-partner-contact/project-application-form-partner-contact.component';
import {ProjectApplicationFormPartnerContributionComponent} from './project-application/components/project-application-form/project-application-form-partner-contribution/project-application-form-partner-contribution.component';
import {ProjectPartnerCoFinancingTabComponent} from '@project/partner/project-partner-detail-page/project-partner-co-financing-tab/project-partner-co-financing-tab.component';
import {ProjectPartnerStateAidTabComponent} from '@project/partner/project-partner-detail-page/project-partner-state-aid-tab/project-partner-state-aid-tab.component';
import {ProjectWorkPackageObjectivesTabComponent} from '@project/work-package/project-work-package-page/work-package-detail-page/project-work-package-objectives-tab/project-work-package-objectives-tab.component';
import {ProjectWorkPackageInvestmentsTabComponent} from '@project/work-package/project-work-package-page/work-package-detail-page/project-work-package-investments-tab/project-work-package-investments-tab.component';
import {ProjectWorkPackageActivitiesTabComponent} from '@project/work-package/project-work-package-page/work-package-detail-page/project-work-package-activities-tab/project-work-package-activities-tab.component';
import {ProjectWorkPackageOutputsTabComponent} from '@project/work-package/project-work-package-page/work-package-detail-page/project-work-package-outputs-tab/project-work-package-outputs-tab.component';
import {ApplicationAnnexesComponent} from '@project/project-application/application-annexes/application-annexes.component';
import {CheckAndSubmitComponent} from '@project/project-application/check-and-submit/check-and-submit.component';
import {AssessmentAndDecisionComponent} from '@project/project-application/assessment-and-decision/assessment-and-decision.component';
import {PartnerBreadcrumbResolver} from '@project/project-application/containers/project-application-detail/services/partner-breadcrumb-resolver.service';
import {WorkPackageBreadcrumbResolver} from '@project/project-application/containers/project-application-detail/services/work-package-breadcrumb-resolver.service';
import {InvestmentBreadcrumbResolver} from '@project/project-application/containers/project-application-detail/services/investment-breadcrumb.resolver';
import {BudgetPerPeriodPageComponent} from '@project/budget/budget-page-per-period/budget-per-period-page.component';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {ProjectOverviewTablesPageComponent} from './project-overview-tables-page/project-overview-tables-page.component';
import {ExportComponent} from '@project/project-application/export/export.component';
import {ModificationPageComponent} from './project-application/modification-page/modification-page.component';
import {ProjectUnitCostsPageComponent} from '@project/unit-costs/project-unit-costs-page/project-unit-costs-page.component';
import {PrivilegesPageComponent} from './project-application/privileges-page/privileges-page.component';
import {ContractMonitoringComponent} from '@project/project-application/contract-monitoring/contract-monitoring.component';
import {PartnerReportComponent} from '@project/project-application/report/partner-report.component';
import { PartnerReportDetailPageComponent } from './project-application/report/partner-report-detail-page/partner-report-detail-page.component';
import {
  PartnerReportIdentificationTabComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-identification-tab/partner-report-identification-tab.component';

import {
  ReportDetailPageBreadcrumbResolver
} from '@project/project-application/report/partner-report-detail-page/report-detail-page-breadcrumb-resolver.service';
import {
  ReportPageBreadcrumbResolver
} from '@project/project-application/report/report-page-breadcrumb-resolver.service';
import {
  PartnerReportExpendituresTabComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-expenditures-tab/partner-report-expenditures-tab.component';
import { PartnerReportWorkPlanProgressTabComponent } from './project-application/report/partner-report-detail-page/partner-report-work-plan-progress-tab/partner-report-work-plan-progress-tab.component';
import { PartnerReportSubmitTabComponent } from './project-application/report/partner-report-detail-page/partner-report-submit-tab/partner-report-submit-tab.component';
import {ProjectPartnerCoFinancingSpfTabComponent} from '@project/partner/project-partner-detail-page/project-partner-co-financing-spf-tab/project-partner-co-financing-spf-tab.component';
import {
  PartnerReportContributionTabComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-contribution-tab/partner-report-contribution-tab.component';
import {
  PartnerReportProcurementsTabComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurements-tab.component';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {
  AssessmentAndDecisionChecklistPageComponent
} from '@project/project-application/assessment-and-decision/assessment-and-decision-checklist-page/assessment-and-decision-checklist-page.component';
import { PartnerReportAnnexesTabComponent } from './project-application/report/partner-report-detail-page/partner-report-annexes-tab/partner-report-annexes-tab.component';

export const routes: Routes = [
  {
    path: '',
    data: {breadcrumb: 'project.breadcrumb.list'},
    children: [
      {
        path: '',
        component: ProjectApplicationComponent,
        canActivate: [PermissionGuard],
        data: {permissionsOnly: [PermissionsEnum.ProjectRetrieve]},
      },
      {
        path: 'applyTo/:callId',
        component: ProjectApplyToCallComponent,
        data: {breadcrumb: 'call.breadcrumb.apply'}
      },
      {
        path: 'detail/:projectId',
        data: {dynamicBreadcrumb: true, queryParamsHandling: 'merge'},
        resolve: {breadcrumb$: ProjectAcronymResolver},
        children: [
          {
            path: '',
            component: ProjectDetailPageComponent,
          },
          {
            path: 'reporting/:partnerId/reports',
            data: {dynamicBreadcrumb: true},
            resolve: {breadcrumb$: ReportPageBreadcrumbResolver},
            children: [
              {
                path: '',
                component: PartnerReportComponent,
              },
              {
                path: ':reportId',
                redirectTo: ':reportId/identification',
              },
              {
                path: ':reportId',
                data: {dynamicBreadcrumb: true},
                resolve: {breadcrumb$: ReportDetailPageBreadcrumbResolver},
                children: [
                  {
                    path: '',
                    component: PartnerReportDetailPageComponent,
                    children: [
                      {
                        path: 'identification',
                        component: PartnerReportIdentificationTabComponent,
                      },
                      {
                        path: 'workplan',
                        component: PartnerReportWorkPlanProgressTabComponent,
                      },
                      {
                        path: 'expenditures',
                        component: PartnerReportExpendituresTabComponent
                      },
                      {
                        path: 'procurements',
                        component: PartnerReportProcurementsTabComponent,
                      },
                      {
                        path: 'contribution',
                        component: PartnerReportContributionTabComponent,
                      },
                      {
                        path: 'annexes',
                        component: PartnerReportAnnexesTabComponent,
                      },
                      {
                        path: 'submission',
                        component: PartnerReportSubmitTabComponent,
                      }
                    ],
                  }
                ]
              }
            ]
          },
          {
            path: 'contractMonitoring',
            component: ContractMonitoringComponent,
            data: {breadcrumb: 'project.breadcrumb.applicationForm.contract.monitoring'},
          },
          {
            path: 'annexes',
            component: ApplicationAnnexesComponent,
            data: {breadcrumb: 'project.breadcrumb.applicationForm.application.annexes'},
          },
          {
            path: 'checkAndSubmit',
            component: CheckAndSubmitComponent,
            data: {breadcrumb: 'project.breadcrumb.applicationForm.check.and.submit'},
          },
          {
            path: 'assessmentAndDecision',
            data: {breadcrumb: 'project.breadcrumb.applicationForm.assessment.and.decision'},
            children: [
              {
                path: '',
                component: AssessmentAndDecisionComponent,
                canActivate: [PermissionGuard],
              },
              {
                path: 'checklist/:checklistId',
                component: AssessmentAndDecisionChecklistPageComponent,
                data: {breadcrumb: 'checklists.instance.title'},
              },
              {
                path: 'eligibilityDecision/:step',
                component: ProjectApplicationEligibilityDecisionPageComponent,
                data: {breadcrumb: 'project.breadcrumb.eligibilityDecision'},
              },
              {
                path: 'qualityCheck/:step',
                component: ProjectApplicationQualityCheckComponent,
                data: {breadcrumb: 'project.breadcrumb.qualityCheck'},
              },
              {
                path: 'eligibilityCheck/:step',
                component: ProjectApplicationEligibilityCheckComponent,
                data: {breadcrumb: 'project.breadcrumb.eligibilityCheck'},
              },
              {
                path: 'fundingDecision/:step',
                component: ProjectApplicationFundingPageComponent,
                data: {breadcrumb: 'project.breadcrumb.fundingDecision'},
              },
            ],
          },
          {
            path: 'modification',
            component: ModificationPageComponent,
            data: {breadcrumb: 'project.breadcrumb.applicationForm.modification'},
          },
          {
            path: 'export',
            component: ExportComponent,
            data: {breadcrumb: 'project.breadcrumb.applicationForm.export'},
          },
          {
            path: 'privileges',
            component: PrivilegesPageComponent,
            data: {breadcrumb: 'project.breadcrumb.applicationForm.privileges'},
          },
          {
            path: 'applicationFormIdentification',
            data: {breadcrumb: 'project.breadcrumb.applicationForm.identification'},
            component: ProjectApplicationFormIdentificationPageComponent,
          },
          {
            path: 'applicationFormOverviewTables',
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.overview.tables',
              visibleOnly: [APPLICATION_FORM.SECTION_A.PROJECT_OVERVIEW_TABLES]
            },
            component: ProjectOverviewTablesPageComponent,
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
                path: ':partnerId',
                redirectTo: ':partnerId/identity',
              },
              {
                path: ':partnerId',
                component: ProjectPartnerDetailPageComponent,
                data: {dynamicBreadcrumb: true},
                resolve: {breadcrumb$: PartnerBreadcrumbResolver},
                children: [
                  {
                    path: 'identity',
                    component: ProjectApplicationFormPartnerEditComponent,
                  },
                  {
                    path: 'region',
                    component: ProjectApplicationFormRegionSelectionComponent,
                  },
                  {
                    path: 'contact',
                    component: ProjectApplicationFormPartnerContactComponent,
                  },
                  {
                    path: 'motivation',
                    component: ProjectApplicationFormPartnerContributionComponent,
                  },
                  {
                    path: 'budget',
                    component: ProjectPartnerBudgetTabComponent,
                  },
                  {
                    path: 'coFinancing',
                    component: ProjectPartnerCoFinancingTabComponent,
                  },
                  {
                    path: 'spfCoFinancing',
                    component: ProjectPartnerCoFinancingSpfTabComponent,
                  },
                  {
                    path: 'stateAid',
                    component: ProjectPartnerStateAidTabComponent,
                  }
                ]
              },
            ]
          },
          {
            path: 'applicationFormAssociatedOrganization',
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.associated.org',
              visibleOnly: [APPLICATION_FORM.SECTION_B.PARTNER_ASSOCIATED_ORGANIZATIONS]
            },
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
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.overallObjective',
              visibleOnly: [APPLICATION_FORM.SECTION_C.PROJECT_OVERALL_OBJECTIVE]
            },
          },
          {
            path: 'applicationFormRelevanceAndContext',
            component: ProjectApplicationFormProjectRelevanceAndContextSectionComponent,
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.relevanceAndContext',
              visibleOnly: [APPLICATION_FORM.SECTION_C.PROJECT_RELEVANCE_AND_CONTEXT]
            },
          },
          {
            path: 'applicationFormPartnership',
            component: ProjectApplicationFormProjectPartnershipSectionComponent,
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.partnership',
              visibleOnly: [APPLICATION_FORM.SECTION_C.PROJECT_PARTNERSHIP]
            },
          },
          {
            path: 'applicationFormWorkPackage',
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.workPackage',
              visibleOnly: [APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN]
            },
            children: [
              {
                path: '',
                component: ProjectWorkPackagePageComponent
              },
              {
                path: ':workPackageId',
                redirectTo: ':workPackageId/objectives',
              },
              {
                path: ':workPackageId',
                data: {dynamicBreadcrumb: true},
                resolve: {breadcrumb$: WorkPackageBreadcrumbResolver},
                children: [
                  {
                    path: '',
                    component: ProjectWorkPackageDetailPageComponent,
                    children: [
                      {
                        path: 'objectives',
                        component: ProjectWorkPackageObjectivesTabComponent
                      },
                      {
                        path: 'investments',
                        component: ProjectWorkPackageInvestmentsTabComponent,
                      },
                      {
                        path: 'activities',
                        component: ProjectWorkPackageActivitiesTabComponent
                      },
                      {
                        path: 'outputs',
                        component: ProjectWorkPackageOutputsTabComponent
                      }
                    ]
                  },
                  {
                    path: 'investments',
                    data: {breadcrumb: 'project.breadcrumb.workPackageInvestment.overview'},
                    children: [
                      {
                        path: 'create',
                        component: ProjectWorkPackageInvestmentDetailPageComponent,
                        data: {breadcrumb: 'project.breadcrumb.workPackageInvestment.create'},
                      },
                      {
                        path: ':workPackageInvestmentId',
                        component: ProjectWorkPackageInvestmentDetailPageComponent,
                        data: {dynamicBreadcrumb: true},
                        resolve: {breadcrumb$: InvestmentBreadcrumbResolver},
                      },
                    ]
                  }
                ]
              },

            ]
          },
          {
            path: 'applicationFormResults',
            component: ProjectResultsPageComponent,
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.results',
              visibleOnly: [APPLICATION_FORM.SECTION_C.PROJECT_RESULT]
            },
          },
          {
            path: 'applicationTimePlan',
            component: ProjectTimeplanPageComponent,
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.timePlan',
              visibleOnly: [
                APPLICATION_FORM.SECTION_C.PROJECT_RESULT,
                APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN
              ]
            },
          },
          {
            path: 'applicationFormFuturePlans',
            component: ProjectApplicationFormFuturePlansSectionComponent,
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.futurePlans',
              visibleOnly: [APPLICATION_FORM.SECTION_C.PROJECT_LONG_TERM_PLANS]
            },
          },
          {
            path: 'applicationFormManagement',
            component: ProjectApplicationFormManagementSectionComponent,
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.management',
              visibleOnly: [APPLICATION_FORM.SECTION_C.PROJECT_MANAGEMENT]
            },
          },
          {
            path: 'applicationFormBudgetPerPartner',
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.budgetPerPartner',
              visibleOnly: [APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING]
            },
            component: BudgetPagePerPartnerComponent,
          },
          {
            path: 'applicationFormBudget',
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.budget',
              visibleOnly: [
                APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING,
                APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS
              ]
            },
            component: BudgetPageComponent,
          },
          {
            path: 'applicationFormBudgetPerPeriod',
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.budget',
              visibleOnly: [
                APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING,
                APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS
              ]
            },
            component: BudgetPerPeriodPageComponent,
          },
          {
            path: 'applicationFormLumpSums',
            data: {
              breadcrumb: 'project.breadcrumb.applicationForm.lump.sums',
              visibleOnly: [APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING]
            },
            component: ProjectLumpSumsPageComponent,
          },
          {
            path: 'applicationFormUnitCosts',
            data: {breadcrumb: 'project.breadcrumb.applicationForm.unit.costs'},
            component: ProjectUnitCostsPageComponent,
          },
        ]
      },
    ]
  },
];
