import {NgModule} from '@angular/core';
import {DatePipe} from '@angular/common';
import {routes} from './project-routing.module';
import {ProjectApplicationSubmissionComponent} from './project-application/components/project-application-submission/project-application-submission.component';
import {ProjectApplicationComponent} from './project-application/containers/project-application-page/project-application.component';
import {SharedModule} from '../common/shared-module';
import {ProjectApplicationFilesListComponent} from './project-application/components/project-application-detail/project-application-files-list/project-application-files-list.component';
import {ProjectApplicationInformationComponent} from './project-application/components/project-application-detail/project-application-information/project-application-information.component';
import {ProjectApplicationFileUploadComponent} from './project-application/components/project-application-detail/project-application-file-upload/project-application-file-upload.component';
import {ProjectApplicationAssessmentsComponent} from './project-application/components/project-application-detail/project-application-assessments/project-application-assessments.component';
import {ProjectApplicationFilesComponent} from './project-application/containers/project-application-detail/project-application-files/project-application-files.component';
import {DescriptionCellComponent} from './project-application/components/project-application-detail/project-application-files-list/cell-renderers/description-cell/description-cell.component';
import {ProjectApplicationEligibilityCheckComponent} from './project-detail-page/project-application-eligibility-check/project-application-eligibility-check.component';
import {ProjectApplicationQualityCheckComponent} from './project-detail-page/project-application-quality-check/project-application-quality-check.component';
import {ProjectStore} from './project-application/containers/project-application-detail/services/project-store.service';
import {ActionsCellComponent} from './project-application/components/project-application-detail/project-application-files-list/cell-renderers/actions-cell/actions-cell.component';
import {ProjectApplicationDecisionsComponent} from './project-application/components/project-application-detail/project-application-decisions/project-application-decisions.component';
import {ProjectApplicationActionsComponent} from './project-detail-page/project-application-actions/project-application-actions.component';
import {ProjectApplicationFundingPageComponent} from './project-detail-page/project-application-funding-page/project-application-funding-page.component';
import {ProjectApplicationFundingDecisionComponent} from './project-detail-page/project-application-funding-page/project-application-funding-decision/project-application-funding-decision.component';
import {ProjectApplicationEligibilityDecisionPageComponent} from './project-detail-page/project-application-eligibility-decision-page/project-application-eligibility-decision-page.component';
import {ProjectApplicationFormComponent} from './project-application/components/project-application-form/project-application-form.component';
import {ProjectApplicationFormWorkPackagesListComponent} from './project-application/components/project-application-form/project-application-form-work-packages-list/project-application-form-work-packages-list.component';
import {ProjectApplicationFormWorkPackageSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-work-package-section/project-application-form-work-package-section.component';
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
import {ProjectAcronymResolver} from './project-application/containers/project-application-detail/services/project-acronym.resolver';
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
import {ProjectPartnerStore} from './project-application/containers/project-application-form-page/services/project-partner-store.service';
import {ProjectApplicationPartnerIdentityComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-partner-identity/project-application-partner-identity.component';
import {ProjectApplicationFormAssociatedOrganizationsListComponent} from './project-application/components/project-application-form/project-application-form-associated-organizations-list/project-application-form-associated-organizations-list.component';
import {ProjectApplicationFormAssociatedOrgDetailComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-associated-org-detail/project-application-form-associated-org-detail.component';
import {ProjectApplicationFormAssociatedOrganizationEditComponent} from './project-application/components/project-application-form/project-application-form-associated-organization-edit/project-application-form-associated-organization-edit.component';
import {ProjectAssociatedOrganizationStore} from './project-application/containers/project-application-form-page/services/project-associated-organization-store.service';
import {ProjectPartnerBudgetOptionsComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget-options/project-partner-budget-options.component';
import {ContributionToggleColumnComponent} from './project-application/components/project-application-form/project-application-form-management-detail/contribution-toggle-column/contribution-toggle-column.component';
import {ProjectApplicationFormIdentificationPageComponent} from './project-application/containers/project-application-form-page/project-application-form-identification-page/project-application-form-identification-page.component';
import {ProjectApplicationFormAssociatedOrgPageComponent} from './project-application/containers/project-application-form-page/project-application-form-associated-org-page/project-application-form-associated-org-page.component';
import {ProjectApplicationFormAddressComponent} from './project-application/components/project-application-form/project-application-form-address/project-application-form-address.component';
import {ProjectPartnerCoFinancingTabComponent} from './partner/project-partner-detail-page/project-partner-co-financing-tab/project-partner-co-financing-tab.component';
import {ProjectPartnerDetailPageComponent} from './partner/project-partner-detail-page/project-partner-detail-page.component';
import {BudgetPageComponent} from './budget/budget-page/budget-page.component';
import {ProjectWorkPackageObjectivesTabComponent} from './work-package/work-package-detail-page/project-work-package-objectives-tab/project-work-package-objectives-tab.component';
import {ProjectWorkPackageDetailPageComponent} from './work-package/work-package-detail-page/project-work-package-detail-page.component';
import {ProjectWorkPackageActivitiesTabComponent} from './work-package/work-package-detail-page/project-work-package-activities-tab/project-work-package-activities-tab.component';
import {TravelAndAccommodationCostsBudgetTableComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/travel-and-accommodation-costs-budget-table/travel-and-accommodation-costs-budget-table.component';
import {ProjectPeriodsSelectComponent} from './components/project-periods-select/project-periods-select.component';
import {ProjectWorkPackageInvestmentsTabComponent} from './work-package/work-package-detail-page/project-work-package-investments-tab/project-work-package-investments-tab.component';
import {ProjectWorkPackageInvestmentDetailPageComponent} from './work-package/work-package-detail-page/project-work-package-investments-tab/project-work-package-investment-detail-page/project-work-package-investment-detail-page.component';
import {StaffCostsBudgetTableComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/staff-costs-budget-table/staff-costs-budget-table.component';
import {BudgetFlatRateTableComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/budget-flat-rate-table/budget-flat-rate-table.component';
import {GeneralBudgetTableComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/general-budget-table/general-budget-table.component';
import {ProjectWorkPackagePageStore} from './work-package/work-package-detail-page/project-work-package-page-store.service';
import {ProjectPartnerDetailPageStore} from './partner/project-partner-detail-page/project-partner-detail-page.store';
import {ProjectResultsPageComponent} from './results/project-results-page/project-results-page.component';
import {UnitCostsBudgetTableComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/unit-costs-budget-table/unit-costs-budget-table.component';
import {ProjectWorkPackageOutputsTabComponent} from './work-package/work-package-detail-page/project-work-package-outputs-tab/project-work-package-outputs-tab.component';
import {ProjectLumpSumsPageComponent} from './lump-sums/project-lump-sums-page/project-lump-sums-page.component';
import {ProjectLumpSumsPageStore} from './lump-sums/project-lump-sums-page/project-lump-sums-page.store';
import {BudgetPagePerPartnerComponent} from './budget/budget-page-per-partner/budget-page-per-partner.component';
import {ProjectTimeplanPageComponent} from './timeplan/project-timeplan-page/project-timeplan-page.component';
import {FilterUnitCostsPipe} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/filter-unit-costs.pipe';
import {ProjectDetailPageComponent} from './project-detail-page/project-detail-page.component';

@NgModule({
  declarations: [
    DescriptionCellComponent,
    ProjectApplicationComponent,
    ProjectApplyToCallComponent,
    ProjectApplicationSubmissionComponent,
    ProjectDetailPageComponent,
    ProjectApplicationFilesListComponent,
    ProjectApplicationInformationComponent,
    ProjectApplicationFileUploadComponent,
    ProjectApplicationAssessmentsComponent,
    ProjectApplicationFilesComponent,
    ProjectApplicationDecisionsComponent,
    ProjectApplicationActionsComponent,
    ProjectApplicationQualityCheckComponent,
    ProjectApplicationEligibilityCheckComponent,
    ActionsCellComponent,
    ProjectApplicationFundingPageComponent,
    ProjectApplicationFundingDecisionComponent,
    ProjectApplicationEligibilityDecisionPageComponent,
    ProjectApplicationFormComponent,
    ProjectApplicationFormPartnerSectionComponent,
    ProjectApplicationFormPartnerListComponent,
    ProjectPartnerDetailPageComponent,
    ProjectApplicationFormPartnerEditComponent,
    ProjectApplicationFormWorkPackagesListComponent,
    ProjectWorkPackageObjectivesTabComponent,
    ProjectWorkPackageDetailPageComponent,
    ProjectWorkPackageActivitiesTabComponent,
    ProjectWorkPackageOutputsTabComponent,
    ProjectApplicationFormWorkPackageSectionComponent,
    ProjectApplicationFormManagementSectionComponent,
    ProjectApplicationFormFuturePlansSectionComponent,
    ProjectApplicationFormManagementDetailComponent,
    ProjectApplicationFormFuturePlansDetailComponent,
    ContributionRadioColumnComponent,
    ContributionToggleColumnComponent,
    ProjectApplicationFormWorkPackageSectionComponent,
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
    ProjectApplicationPartnerIdentityComponent,
    ProjectApplicationFormAssociatedOrganizationEditComponent,
    ProjectApplicationFormIdentificationPageComponent,
    ProjectApplicationFormAssociatedOrgPageComponent,
    ProjectApplicationFormAddressComponent,
    BudgetPageComponent,
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
    ProjectDetailPageComponent,
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
    ProjectStore,
    ProjectApplicationFormSidenavService,
    ProjectApplicationFormStore,
    ProjectAcronymResolver,
    ProjectPartnerStore,
    ProjectAssociatedOrganizationStore,
    ProjectWorkPackagePageStore,
    ProjectPartnerDetailPageStore,
    ProjectLumpSumsPageStore
  ]
})
export class ProjectModule {
}
