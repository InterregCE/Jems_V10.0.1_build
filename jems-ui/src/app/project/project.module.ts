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
import {ProjectLumpSumsStore} from './lump-sums/project-lump-sums-page/project-lump-sums-store.service';
import {BudgetPagePerPartnerComponent} from './budget/budget-page-per-partner/budget-page-per-partner.component';
import {ProjectTimeplanPageComponent} from './timeplan/project-timeplan-page/project-timeplan-page.component';
import {FilterUnitCostsPipe} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/filter-unit-costs.pipe';
import {ProjectDetailPageComponent} from './project-detail-page/project-detail-page.component';
import {ProjectApplicationPreConditionCheckResultComponent} from './project-detail-page/project-application-pre-condition-check-result/project-application-pre-condition-check-result.component';
import {ProjectAcronymResolver} from './project-application/containers/project-application-detail/services/project-acronym.resolver';
import {ProjectPageTemplateComponent} from './project-page-template/project-page-template.component';
import {ProjectWorkPackagePageComponent} from './work-package/project-work-package-page/project-work-package-page.component';
import {FormFieldVisibilityStatusDirective} from './common/directives/form-field-visibility-status.directive';
import {ProjectPartnerStateAidTabComponent} from './partner/project-partner-detail-page/project-partner-state-aid-tab/project-partner-state-aid-tab.component';
import {CategoryTreeComponent} from './common/components/category-tree/category-tree.component';
import {PeriodsTotalPipe} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/periods-total.pipe';
import {FileManagementComponent} from './common/components/file-management/file-management.component';
import {ProjectApplicationFilesTableComponent} from './common/components/file-management/project-application-files-table/project-application-files-table.component';
import {ProjectPartnerFilterPipe} from './work-package/project-work-package-page/work-package-detail-page/project-work-package-activities-tab/project-partner-filter.pipe';
import {ApplicationAnnexesComponent} from './project-application/application-annexes/application-annexes.component';
import {CheckAndSubmitComponent} from './project-application/check-and-submit/check-and-submit.component';
import {AssessmentAndDecisionComponent} from './project-application/assessment-and-decision/assessment-and-decision.component';
import {ProjectWorkPackageActivitiesFilterPipe} from './partner/project-partner-detail-page/project-partner-state-aid-tab/work-package-activities-filter.pipe';
import {ProjectWorkPackagePageStore} from './work-package/project-work-package-page/project-work-package-page-store.service';
import {PartnerBreadcrumbResolver} from './project-application/containers/project-application-detail/services/partner-breadcrumb-resolver.service';
import {WorkPackageBreadcrumbResolver} from './project-application/containers/project-application-detail/services/work-package-breadcrumb-resolver.service';
import {ProjectWorkPackageInvestmentDetailPageStore} from './work-package/project-work-package-page/work-package-detail-page/project-work-package-investments-tab/project-work-package-investment-detail-page/project-work-package-Investment-detail-page-store.service';
import {InvestmentBreadcrumbResolver} from './project-application/containers/project-application-detail/services/investment-breadcrumb.resolver';
import {ProjectPartnerBudgetOverviewComponent} from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget-overview/project-partner-budget-overview.component';
import {BudgetTableComponent} from './budget/budget-page/budget-table/budget-table.component';
import {ProjectApplicationFormA4Component} from './project-overview-tables-page/project-application-form-a4/project-application-form-a4.component';
import {ProjectOverviewTablesPageComponent} from './project-overview-tables-page/project-overview-tables-page.component';
import {ProjectBudgetOverviewComponent} from './project-overview-tables-page/project-budget-overview/project-budget-overview.component';
import {BudgetPageFundPerPeriodComponent} from './budget/budget-page-per-period/budget-page-fund-per-period/budget-page-fund-per-period.component';
import {BudgetPerPeriodPageComponent} from './budget/budget-page-per-period/budget-per-period-page.component';
import {ProjectBudgetPeriodPageStore} from './budget/budget-page-per-period/budget-period-page.store';
import {ProjectPartnerBudgetStore} from './budget/services/project-partner-budget.store';
import {ProjectPartnerCoFinancingStore} from './partner/project-partner-detail-page/project-partner-co-financing-tab/services/project-partner-co-financing.store';
import {ProjectPartnerStateAidsStore} from './partner/services/project-partner-state-aids.store';
import {BudgetPagePartnerPerPeriodComponent} from './budget/budget-page-per-period/budget-page-partner-per-period/budget-page-partner-per-period.component';
import { ExportComponent } from './project-application/export/export.component';
import {UsersFilterPipe} from '@common/components/project-application-list-user-assignments/user-filter.pipe';
import {ProjectApplicationListUserAssignmentsComponent} from '@common/components/project-application-list-user-assignments/project-application-list-user-assignments.component';
import {ProjectUnitCostsPageComponent} from './unit-costs/project-unit-costs-page/project-unit-costs-page.component';
import {ProjectUnitCostsStore} from './unit-costs/project-unit-costs-page/project-unit-costs-store.service';
import {ModificationPageComponent} from './project-application/modification-page/modification-page.component';
import {ModificationConfirmationComponent} from './project-application/modification-page/modification-confirmation/modification-confirmation.component';
import {PrivilegesPageComponent} from './project-application/privileges-page/privileges-page.component';
import { ContractMonitoringComponent } from './project-application/contract-monitoring/contract-monitoring.component';
import { LumpsumBudgetTableComponent } from './partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/lumpsum-budget-table/lumpsum-budget-table.component';
import {PartnerReportComponent} from './project-application/report/partner-report.component';
import { ApplicationFormPrivilegesExpansionPanelComponent } from './project-application/privileges-page/application-form-privileges-expansion-panel/application-form-privileges-expansion-panel.component';
import { PartnerTeamPrivilegesExpansionPanelComponent } from './project-application/privileges-page/partner-team-privileges-expansion-panel/partner-team-privileges-expansion-panel.component';
import { SpfRecipientsTableComponent } from './project-application/components/project-application-form/project-application-form-project-relevance-and-context-detail/tables/spf-recipients-table/spf-recipients-table.component';
import {
  SmallProjectFundTableComponent
} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/small-project-fund-table/small-project-fund-table.component';
import {
  SmallProjectFundBudgetComponent
} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/small-project-fund-budget/small-project-fund-budget.component';

import { PartnerReportDetailPageComponent } from './project-application/report/partner-report-detail-page/partner-report-detail-page.component';
import { PartnerReportIdentificationTabComponent } from './project-application/report/partner-report-detail-page/partner-report-identification-tab/partner-report-identification-tab.component';
import { PartnerReportWorkPlanProgressTabComponent } from './project-application/report/partner-report-detail-page/partner-report-work-plan-progress-tab/partner-report-work-plan-progress-tab.component';
import { PartnerReportSubmitTabComponent } from './project-application/report/partner-report-detail-page/partner-report-submit-tab/partner-report-submit-tab.component';
import {ProjectPartnerCoFinancingSpfTabComponent} from '@project/partner/project-partner-detail-page/project-partner-co-financing-spf-tab/project-partner-co-financing-spf-tab.component';
import {ProjectPartnerCoFinancingSpfStore} from '@project/partner/project-partner-detail-page/project-partner-co-financing-spf-tab/project-partner-co-financing-spf.store';
import {
  PartnerReportExpendituresTabComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-expenditures-tab/partner-report-expenditures-tab.component';
import {
  PartnerReportContributionTabComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-contribution-tab/partner-report-contribution-tab.component';
import {
  PartnerReportProcurementsTabComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurements-tab.component';
import {
  PartnerActionsCellComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-actions/partner-actions-cell.component';
import {AssessmentAndDecisionChecklistPageComponent} from './project-application/assessment-and-decision/assessment-and-decision-checklist-page/assessment-and-decision-checklist-page.component';
import { PartnerReportAnnexesTabComponent } from './project-application/report/partner-report-detail-page/partner-report-annexes-tab/partner-report-annexes-tab.component';
import { ReportAnnexesTableComponent } from './project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-annexes-table/report-annexes-table.component';
import { ProjectManagementComponent } from './project-application/project-management/project-management.component';
import {
  PartnerReportFinancialOverviewTabComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-financial-overview-tab/partner-report-financial-overview-tab.component';
import {
  PartnerBreakdownCostCategoryComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-financial-overview-tab/partner-breakdown-cost-category/partner-breakdown-cost-category.component';
import { ContractMonitoringExtensionComponent } from './project-application/contract-monitoring/contract-monitoring-extension/contract-monitoring-extension.component';
import {
  ContractingFilesComponent
} from '@project/project-application/contract-monitoring/contracting-files/contracting-files.component';
import {
  PartnerBreakdownCoFinancingComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-financial-overview-tab/partner-breakdown-co-financing/partner-breakdown-co-financing.component';
import {
  PartnerReportProcurementDetailComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-detail/partner-report-procurement-detail.component';
import {ContractReportingComponent} from '@project/project-application/contract-reporting/contract-reporting.component';
import {
  PartnerReportProcurementIdentificationComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-detail/partner-report-procurement-identification/partner-report-procurement-identification.component';
import {
  PartnerReportProcurementBeneficialComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-detail/partner-report-procurement-beneficial/partner-report-procurement-beneficial.component';
import {
  PartnerReportProcurementSubcontractComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-detail/partner-report-procurement-subcontract/partner-report-procurement-subcontract.component';
import {
  PartnerReportStatusComponent
} from '@project/project-application/report/partner-report-status/partner-report-status.component';
import {
  PartnerControlReportComponent
} from '@project/project-application/report/partner-control-report/partner-control-report.component';
import {
  PartnerControlReportIdentificationTabComponent
} from '@project/project-application/report/partner-control-report/partner-control-report-identification-tab/partner-control-report-identification-tab.component';
import {
  PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import { PartnerControlReportInformationTabComponent } from './project-application/report/partner-control-report/partner-control-report-information-tab/partner-control-report-information-tab.component';
import { PartnerControlReportControlChecklistsTabComponent } from '@project/project-application/report/partner-control-report/partner-control-report-control-checklists-tab/partner-control-report-control-checklists-tab.component';
import {
  PartnerControlReportControlChecklistPageComponent
} from '@project/project-application/report/partner-control-report/partner-control-report-control-checklists-tab/partner-control-report-control-checklist-page/partner-control-report-control-checklist-page.component';
import { ProjectProposedUnitCostsComponent } from './unit-costs/project-unit-costs-page/project-proposed-unit-costs/project-proposed-unit-costs.component';
import { ProjectProposedUnitCostDetailComponent } from './unit-costs/project-unit-costs-page/project-proposed-unit-costs/project-proposed-unit-cost-detail/project-proposed-unit-cost-detail.component';
import {
  ProjectProposedUnitCostBreadcrumbResolver
} from '@project/project-application/containers/project-application-detail/services/project-proposed-unit-cost.resolver';
import { ContractingContractComponent } from './project-application/contracting-contract/contracting-contract.component';
import { ContractFilesComponent } from './project-application/contracting-contract/contract-files/contract-files.component';
import {
  PartnerReportProcurementAttachmentComponent
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-detail/partner-report-procurement-attachment/partner-report-procurement-attachment.component';
import {TranslateByInputLanguagePipe} from '@common/pipe/translate-by-input-language.pipe';
import {
  PartnerControlReportDocumentTabComponent
} from '@project/project-application/report/partner-control-report/partner-control-report-document-tab/partner-control-report-document-tab.component';
import {ContractPartnerComponent} from '@project/project-application/contract-partner/contract-partner.component';
import {
  ContractPartnerBeneficialOwnerComponent
} from '@project/project-application/contract-partner/beneficial-owner/contract-partner-beneficial-owner.component';
import {AdaptTranslationKeyByCallTypePipe} from '@common/pipe/adapt-translation-by-call-type.pipe';

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
    ProjectPartnerCoFinancingSpfTabComponent,
    ProjectPartnerBudgetOptionsComponent,
    ProjectPartnerBudgetOverviewComponent,
    ProjectApplicationPartnerIdentityComponent,
    ProjectApplicationFormIdentificationPageComponent,
    ProjectApplicationFormA4Component,
    ProjectApplicationFormAssociatedOrgPageComponent,
    ProjectApplicationFormAddressComponent,
    BudgetPageComponent,
    BudgetTableComponent,
    BudgetFlatRateTableComponent,
    GeneralBudgetTableComponent,
    StaffCostsBudgetTableComponent,
    SmallProjectFundTableComponent,
    SmallProjectFundBudgetComponent,
    TravelAndAccommodationCostsBudgetTableComponent,
    UnitCostsBudgetTableComponent,
    ProjectWorkPackageInvestmentsTabComponent,
    ProjectWorkPackageInvestmentDetailPageComponent,
    ProjectPeriodsSelectComponent,
    ProjectResultsPageComponent,
    ProjectLumpSumsPageComponent,
    ProjectUnitCostsPageComponent,
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
    CategoryTreeComponent,
    ProjectApplicationFilesTableComponent,
    ProjectPartnerFilterPipe,
    ApplicationAnnexesComponent,
    CheckAndSubmitComponent,
    AssessmentAndDecisionComponent,
    ProjectWorkPackageActivitiesFilterPipe,
    BudgetPagePartnerPerPeriodComponent,
    ProjectOverviewTablesPageComponent,
    ProjectBudgetOverviewComponent,
    BudgetPageFundPerPeriodComponent,
    BudgetPerPeriodPageComponent,
    ExportComponent,
    UsersFilterPipe,
    ProjectApplicationListUserAssignmentsComponent,
    ModificationPageComponent,
    ModificationConfirmationComponent,
    PrivilegesPageComponent,
    ContractMonitoringComponent,
    ContractingFilesComponent,
    LumpsumBudgetTableComponent,
    PartnerReportComponent,
    ApplicationFormPrivilegesExpansionPanelComponent,
    PartnerTeamPrivilegesExpansionPanelComponent,
    PartnerReportDetailPageComponent,
    PartnerReportStatusComponent,
    PartnerReportIdentificationTabComponent,
    PartnerReportWorkPlanProgressTabComponent,
    SpfRecipientsTableComponent,
    PartnerReportSubmitTabComponent,
    PartnerReportProcurementsTabComponent,
    PartnerReportProcurementDetailComponent,
    PartnerReportProcurementIdentificationComponent,
    PartnerReportProcurementBeneficialComponent,
    PartnerReportProcurementSubcontractComponent,
    PartnerReportProcurementAttachmentComponent,
    PartnerReportContributionTabComponent,
    PartnerReportExpendituresTabComponent,
    PartnerReportFinancialOverviewTabComponent,
    PartnerBreakdownCostCategoryComponent,
    PartnerBreakdownCoFinancingComponent,
    PartnerActionsCellComponent,
    AssessmentAndDecisionChecklistPageComponent,
    PartnerReportAnnexesTabComponent,
    ReportAnnexesTableComponent,
    ProjectManagementComponent,
    ContractMonitoringExtensionComponent,
    ContractReportingComponent,
    PartnerControlReportComponent,
    PartnerControlReportIdentificationTabComponent,
    PartnerControlReportInformationTabComponent,
    PartnerControlReportControlChecklistsTabComponent,
    PartnerControlReportControlChecklistPageComponent,
    PartnerControlReportDocumentTabComponent,
    ProjectProposedUnitCostsComponent,
    ProjectProposedUnitCostDetailComponent,
    PartnerControlReportInformationTabComponent,
    ContractingContractComponent,
    ContractFilesComponent,
    ContractPartnerComponent,
    ContractPartnerBeneficialOwnerComponent,
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
    ProjectLumpSumsStore,
    ProjectUnitCostsStore,
    ProjectBudgetPeriodPageStore,
    ProjectPartnerBudgetStore,
    ProjectPartnerCoFinancingStore,
    ProjectPartnerCoFinancingSpfStore,
    ProjectPartnerStateAidsStore,
    PartnerControlReportStore,
    ProjectProposedUnitCostBreadcrumbResolver,
    TranslateByInputLanguagePipe,
    AdaptTranslationKeyByCallTypePipe,
  ]
})
export class ProjectModule {
}
