import {NgModule} from '@angular/core';
import {DatePipe} from '@angular/common';
import {routes} from './project-routing.module';
import {ProjectApplicationComponent} from './project-application/containers/project-application-page/project-application.component';
import {SharedModule} from '@common/shared-module';
import {ProjectApplicationInformationComponent} from './project-application/components/project-application-detail/project-application-information/project-application-information.component';
import {ProjectApplicationAssessmentsComponent} from './project-detail-page/project-application-assessments/project-application-assessments.component';
import {DescriptionCellComponent} from './common/components/file-management/project-application-files-table/description-cell/description-cell.component';
import {ProjectApplicationEligibilityCheckComponent} from './project-detail-page/project-application-eligibility-check/project-application-eligibility-check.component';
import {ProjectApplicationQualityCheckComponent} from './project-detail-page/project-application-quality-check/project-application-quality-check.component';
import {ActionsCellComponent} from './common/components/file-management/project-application-files-table/actions-cell/actions-cell.component';
import {ProjectApplicationDecisionsComponent} from './project-detail-page/project-application-decisions/project-application-decisions.component';
import {ProjectApplicationFundingPageComponent} from './project-detail-page/project-application-funding-page/project-application-funding-page.component';
import {ProjectApplicationFundingDecisionComponent} from './project-detail-page/project-application-funding-page/project-application-funding-decision/project-application-funding-decision.component';
import {ProjectApplicationEligibilityDecisionPageComponent} from './project-detail-page/project-application-eligibility-decision-page/project-application-eligibility-decision-page.component';
import {ProjectApplicationFormComponent} from './project-application/components/project-application-form/project-application-form.component';
import {ProjectApplicationFormPartnerSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-partner-section.component';
import {ProjectApplicationFormPartnerListComponent} from './project-application/components/project-application-form/project-application-form-partner-list/project-application-form-partner-list.component';
import {ProjectApplicationFormPartnerEditComponent} from './project-application/components/project-application-form/project-application-form-partner-edit/project-application-form-partner-edit.component';
import {ProjectApplicationFormSidenavService} from './project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectApplicationFormManagementSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-management-section/project-application-form-management-section.component';
import {ProjectApplicationFormFuturePlansSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-future-plans-section/project-application-form-future-plans-section.component';
import {ProjectApplicationFormManagementDetailComponent} from './project-application/components/project-application-form/project-application-form-management-detail/project-application-form-management-detail.component';
import {ProjectApplicationFormFuturePlansDetailComponent} from './project-application/components/project-application-form/project-application-form-future-plans-detail/project-application-form-future-plans-detail.component';
import {ContributionRadioColumnComponent} from './project-application/components/project-application-form/project-application-form-management-detail/contribution-radio-column/contribution-radio-column.component';
import {ProjectApplicationFormPartnerContactComponent} from './project-application/components/project-application-form/project-application-form-partner-contact/project-application-form-partner-contact.component';
import {RouterModule} from '@angular/router';
import {ProjectApplyToCallComponent} from './project-application/containers/project-application-page/project-apply-to-call.component';
import {ProjectApplicationFormOverallObjectiveSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-overall-objective-section/project-application-form-overall-objective-section.component';
import {ProjectApplicationFormOverallObjectiveDetailComponent} from './project-application/components/project-application-form/project-application-form-overall-objective-detail/project-application-form-overall-objective-detail.component';
import {ProjectApplicationFormProjectPartnershipSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-project-partnership-section/project-application-form-project-partnership-section.component';
import {ProjectApplicationFormProjectPartnershipDetailComponent} from './project-application/components/project-application-form/project-application-form-project-partnership-detail/project-application-form-project-partnership-detail.component';
import {ProjectApplicationFormProjectRelevanceAndContextSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-project-relevance-and-context-section/project-application-form-project-relevance-and-context-section.component';
import {ProjectApplicationFormProjectRelevanceAndContextDetailComponent} from './project-application/components/project-application-form/project-application-form-project-relevance-and-context-detail/project-application-form-project-relevance-and-context-detail.component';
import {BenefitsTableComponent} from './project-application/components/project-application-form/project-application-form-project-relevance-and-context-detail/tables/benefits-table/benefits-table.component';
import {StrategyTableComponent} from './project-application/components/project-application-form/project-application-form-project-relevance-and-context-detail/tables/strategy-table/strategy-table.component';
import {SynergyTableComponent} from './project-application/components/project-application-form/project-application-form-project-relevance-and-context-detail/tables/synergy-table/synergy-table.component';
import {ProjectApplicationFormPartnerContributionComponent} from './project-application/components/project-application-form/project-application-form-partner-contribution/project-application-form-partner-contribution.component';
import {ProjectApplicationFormPartnerAddressComponent} from './project-application/components/project-application-form/project-application-form-partner-address/project-application-form-partner-address.component';
import {ProjectApplicationFormRegionSelectionComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-region-selection/project-application-form-region-selection.component';
import {DeleteActionCellComponent} from './project-application/components/project-application-form/project-application-form-partner-list/delete-action-cell/delete-action-cell.component';
import {ProjectPartnerBudgetTabComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget-tab.component';
import {ProjectPartnerBudgetComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/project-partner-budget.component';
import {ProjectApplicationFormStore} from './project-application/containers/project-application-form-page/services/project-application-form-store.service';
import {ProjectApplicationPartnerIdentityComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-partner-identity/project-application-partner-identity.component';
import {ProjectApplicationFormAssociatedOrganizationsListComponent} from './project-application/components/project-application-form/project-application-form-associated-organizations-list/project-application-form-associated-organizations-list.component';
import {ProjectApplicationFormAssociatedOrgDetailComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-associated-org-detail/project-application-form-associated-org-detail.component';
import {ProjectPartnerBudgetOptionsComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget-options/project-partner-budget-options.component';
import {ContributionToggleColumnComponent} from './project-application/components/project-application-form/project-application-form-management-detail/contribution-toggle-column/contribution-toggle-column.component';
import {ProjectApplicationFormIdentificationPageComponent} from './project-application/containers/project-application-form-page/project-application-form-identification-page/project-application-form-identification-page.component';
import {ProjectApplicationFormAssociatedOrgPageComponent} from './project-application/containers/project-application-form-page/project-application-form-associated-org-page/project-application-form-associated-org-page.component';
import {ProjectApplicationFormAddressComponent} from './project-application/components/project-application-form/project-application-form-address/project-application-form-address.component';
import {ProjectPartnerCoFinancingTabComponent} from './partner/project-partner-detail-page/project-partner-co-financing-tab/project-partner-co-financing-tab.component';
import {ProjectPartnerDetailPageComponent} from './partner/project-partner-detail-page/project-partner-detail-page.component';
import {BudgetPageComponent} from './budget/budget-page/budget-page.component';
import {ProjectWorkPackageObjectivesTabComponent} from './work-package/project-work-package-page/work-package-detail-page/project-work-package-objectives-tab/project-work-package-objectives-tab.component';
import {ProjectWorkPackageDetailPageComponent} from './work-package/project-work-package-page/work-package-detail-page/project-work-package-detail-page.component';
import {ProjectWorkPackageActivitiesTabComponent} from './work-package/project-work-package-page/work-package-detail-page/project-work-package-activities-tab/project-work-package-activities-tab.component';
import {TravelAndAccommodationCostsBudgetTableComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/travel-and-accommodation-costs-budget-table/travel-and-accommodation-costs-budget-table.component';
import {ProjectPeriodsSelectComponent} from './common/components/project-periods-select/project-periods-select.component';
import {ProjectWorkPackageInvestmentsTabComponent} from './work-package/project-work-package-page/work-package-detail-page/project-work-package-investments-tab/project-work-package-investments-tab.component';
import {ProjectWorkPackageInvestmentDetailPageComponent} from './work-package/project-work-package-page/work-package-detail-page/project-work-package-investments-tab/project-work-package-investment-detail-page/project-work-package-investment-detail-page.component';
import {StaffCostsBudgetTableComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/staff-costs-budget-table/staff-costs-budget-table.component';
import {BudgetFlatRateTableComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/budget-flat-rate-table/budget-flat-rate-table.component';
import {GeneralBudgetTableComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/general-budget-table/general-budget-table.component';
import {WorkPackagePageStore} from './work-package/project-work-package-page/work-package-detail-page/work-package-page-store.service';
import {ProjectPartnerDetailPageStore} from './partner/project-partner-detail-page/project-partner-detail-page.store';
import {ProjectResultsPageComponent} from './results/project-results-page/project-results-page.component';
import {UnitCostsBudgetTableComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/unit-costs-budget-table/unit-costs-budget-table.component';
import {ProjectWorkPackageOutputsTabComponent} from './work-package/project-work-package-page/work-package-detail-page/project-work-package-outputs-tab/project-work-package-outputs-tab.component';
import {ProjectLumpSumsPageComponent} from './lump-sums/project-lump-sums-page/project-lump-sums-page.component';
import {ProjectLumpSumsPageStore} from './lump-sums/project-lump-sums-page/project-lump-sums-page.store';
import {BudgetPagePerPartnerComponent} from './budget/budget-page-per-partner/budget-page-per-partner.component';
import {ProjectTimeplanPageComponent} from './timeplan/project-timeplan-page/project-timeplan-page.component';
import {FilterUnitCostsPipe} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/filter-unit-costs.pipe';
import {ProjectDetailPageComponent} from './project-detail-page/project-detail-page.component';
import {ProjectApplicationPreConditionCheckResultComponent} from './project-detail-page/project-application-pre-condition-check-result/project-application-pre-condition-check-result.component';
import {ProjectAcronymResolver} from './project-application/containers/project-application-detail/services/project-acronym.resolver';
import {ProjectVersionStore} from './common/services/project-version-store.service';
import {ProjectPageTemplateComponent} from './project-page-template/project-page-template.component';
import {ProjectWorkPackagePageComponent} from './work-package/project-work-package-page/project-work-package-page.component';
import {FormFieldVisibilityStatusDirective} from './common/directives/form-field-visibility-status.directive';
import {ProjectPartnerStateAidTabComponent} from './partner/project-partner-detail-page/project-partner-state-aid-tab/project-partner-state-aid-tab.component';
import {ProjectApplicationFilesTreeComponent} from './common/components/file-management/project-application-files-tree/project-application-files-tree.component';
import {PeriodsTotalPipe} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/periods-total.pipe';
import {FileManagementComponent} from './common/components/file-management/file-management.component';
import {ProjectApplicationFilesTableComponent} from './common/components/file-management/project-application-files-table/project-application-files-table.component';
import {ProjectPartnerFilterPipe} from '@project/work-package/project-work-package-page/work-package-detail-page/project-work-package-activities-tab/project-partner-filter.pipe';
import {ApplicationAnnexesComponent} from './project-application/application-annexes/application-annexes.component';
import {CheckAndSubmitComponent} from './project-application/check-and-submit/check-and-submit.component';
import {AssessmentAndDecisionComponent} from './project-application/assessment-and-decision/assessment-and-decision.component';
import {ProjectWorkPackageActivitiesFilterPipe} from '@project/partner/project-partner-detail-page/project-partner-state-aid-tab/work-package-activities-filter.pipe';
import {ProjectWorkPackagePageStore} from '@project/work-package/project-work-package-page/project-work-package-page-store.service';
import {PartnerBreadcrumbResolver} from '@project/project-application/containers/project-application-detail/services/partner-breadcrumb-resolver.service';
import {WorkPackageBreadcrumbResolver} from '@project/project-application/containers/project-application-detail/services/work-package-breadcrumb-resolver.service';
import {ProjectWorkPackageInvestmentDetailPageStore} from '@project/work-package/project-work-package-page/work-package-detail-page/project-work-package-investments-tab/project-work-package-investment-detail-page/project-work-package-Investment-detail-page-store.service';
import {InvestmentBreadcrumbResolver} from '@project/project-application/containers/project-application-detail/services/investment-breadcrumb.resolver';
import {ProjectPartnerBudgetOverviewComponent} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget-overview/project-partner-budget-overview.component';
import {BudgetTableComponent} from '@project/budget/budget-page/budget-table/budget-table.component';
import {BudgetPagePartnerPerPeriodComponent} from './budget/budget-page-partner-per-period/budget-page-partner-per-period.component';

@NgModule({
  declarations: [
    DescriptionCellComponent,
    ProjectApplicationComponent,
    ProjectApplyToCallComponent,
    ProjectDetailPageComponent,
    ProjectApplicationInformationComponent,
    ProjectApplicationAssessmentsComponent,
    ProjectApplicationDecisionsComponent,
    ProjectApplicationQualityCheckComponent,
    ProjectApplicationEligibilityCheckComponent,
    ProjectApplicationPreConditionCheckResultComponent,
    ActionsCellComponent,
    ProjectApplicationFundingPageComponent,
    ProjectApplicationFundingDecisionComponent,
    ProjectApplicationEligibilityDecisionPageComponent,
    ProjectApplicationFormComponent,
    ProjectApplicationFormPartnerSectionComponent,
    ProjectApplicationFormPartnerListComponent,
    ProjectPartnerDetailPageComponent,
    ProjectApplicationFormPartnerEditComponent,
    ProjectWorkPackageObjectivesTabComponent,
    ProjectWorkPackageDetailPageComponent,
    ProjectWorkPackageActivitiesTabComponent,
    ProjectWorkPackageOutputsTabComponent,
    ProjectApplicationFormManagementSectionComponent,
    ProjectApplicationFormFuturePlansSectionComponent,
    ProjectApplicationFormManagementDetailComponent,
    ProjectApplicationFormFuturePlansDetailComponent,
    ContributionRadioColumnComponent,
    ContributionToggleColumnComponent,
    ProjectApplicationFormPartnerContactComponent,
    ProjectApplicationFormPartnerContributionComponent,
    ProjectApplicationFormPartnerAddressComponent,
    ProjectApplicationFormPartnerContactComponent,
    ProjectApplicationFormOverallObjectiveSectionComponent,
    ProjectApplicationFormOverallObjectiveDetailComponent,
    ProjectApplicationFormProjectPartnershipSectionComponent,
    ProjectApplicationFormProjectPartnershipDetailComponent,
    ProjectApplicationFormProjectRelevanceAndContextSectionComponent,
    ProjectApplicationFormProjectRelevanceAndContextDetailComponent,
    ProjectApplicationFormAssociatedOrganizationsListComponent,
    ProjectApplicationFormAssociatedOrgDetailComponent,
    BenefitsTableComponent,
    StrategyTableComponent,
    SynergyTableComponent,
    ProjectApplicationFormRegionSelectionComponent,
    DeleteActionCellComponent,
    ProjectPartnerBudgetTabComponent,
    ProjectPartnerBudgetComponent,
    ProjectPartnerCoFinancingTabComponent,
    ProjectPartnerBudgetOptionsComponent,
    ProjectPartnerBudgetOverviewComponent,
    ProjectApplicationPartnerIdentityComponent,
    ProjectApplicationFormIdentificationPageComponent,
    ProjectApplicationFormAssociatedOrgPageComponent,
    ProjectApplicationFormAddressComponent,
    BudgetPageComponent,
    BudgetTableComponent,
    BudgetFlatRateTableComponent,
    GeneralBudgetTableComponent,
    StaffCostsBudgetTableComponent,
    TravelAndAccommodationCostsBudgetTableComponent,
    UnitCostsBudgetTableComponent,
    ProjectWorkPackageInvestmentsTabComponent,
    ProjectWorkPackageInvestmentDetailPageComponent,
    ProjectPeriodsSelectComponent,
    ProjectResultsPageComponent,
    ProjectLumpSumsPageComponent,
    BudgetPagePerPartnerComponent,
    ProjectTimeplanPageComponent,
    FilterUnitCostsPipe,
    PeriodsTotalPipe,
    ProjectDetailPageComponent,
    ProjectApplicationPreConditionCheckResultComponent,
    ProjectPageTemplateComponent,
    ProjectWorkPackagePageComponent,
    FormFieldVisibilityStatusDirective,
    ProjectPartnerStateAidTabComponent,
    FileManagementComponent,
    ProjectApplicationFilesTreeComponent,
    ProjectApplicationFilesTableComponent,
    ProjectPartnerFilterPipe,
    ApplicationAnnexesComponent,
    CheckAndSubmitComponent,
    AssessmentAndDecisionComponent,
    ProjectWorkPackageActivitiesFilterPipe,
    BudgetPagePartnerPerPeriodComponent,
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes),
  ],
  exports: [
    ProjectDetailPageComponent
  ],
  providers: [
    DatePipe,
    ProjectVersionStore,
    ProjectApplicationFormSidenavService,
    ProjectApplicationFormStore,
    ProjectAcronymResolver,
    PartnerBreadcrumbResolver,
    InvestmentBreadcrumbResolver,
    WorkPackageBreadcrumbResolver,
    WorkPackagePageStore,
    ProjectWorkPackageInvestmentDetailPageStore,
    ProjectWorkPackagePageStore,
    ProjectPartnerDetailPageStore,
    ProjectLumpSumsPageStore
  ]
})
export class ProjectModule {
}
