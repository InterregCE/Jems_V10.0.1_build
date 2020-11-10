import {NgModule} from '@angular/core';
import {DatePipe} from '@angular/common';
import {routes} from './project-routing.module';
import {ProjectApplicationDetailComponent} from './project-application/containers/project-application-detail/project-application-detail.component';
import {ProjectApplicationSubmissionComponent} from './project-application/components/project-application-submission/project-application-submission.component';
import {ProjectApplicationComponent} from './project-application/containers/project-application-page/project-application.component';
import {SharedModule} from '../common/shared-module';
import {ProjectApplicationDataComponent} from './project-application/containers/project-application-detail/project-application-data/project-application-data.component';
import {ProjectApplicationFilesListComponent} from './project-application/components/project-application-detail/project-application-files-list/project-application-files-list.component';
import {ProjectApplicationInformationComponent} from './project-application/components/project-application-detail/project-application-information/project-application-information.component';
import {ProjectApplicationFileUploadComponent} from './project-application/components/project-application-detail/project-application-file-upload/project-application-file-upload.component';
import {ProjectApplicationAssessmentsComponent} from './project-application/components/project-application-detail/project-application-assessments/project-application-assessments.component';
import {ProjectApplicationFilesComponent} from './project-application/containers/project-application-detail/project-application-files/project-application-files.component';
import {DescriptionCellComponent} from './project-application/components/project-application-detail/project-application-files-list/cell-renderers/description-cell/description-cell.component';
import {ProjectApplicationEligibilityDecisionComponent} from './project-application/components/project-application-detail/project-application-eligibility-decision/project-application-eligibility-decision.component';
import {ProjectApplicationEligibilityCheckComponent} from './project-application/components/project-application-detail/project-application-eligibility-check/project-application-eligibility-check.component';
import {ProjectApplicationQualityCheckComponent} from './project-application/components/project-application-detail/project-application-quality-check/project-application-quality-check.component';
import {ProjectStore} from './project-application/containers/project-application-detail/services/project-store.service';
import {ActionsCellComponent} from './project-application/components/project-application-detail/project-application-files-list/cell-renderers/actions-cell/actions-cell.component';
import {ProjectApplicationDecisionsComponent} from './project-application/components/project-application-detail/project-application-decisions/project-application-decisions.component';
import {ProjectApplicationActionsComponent} from './project-application/components/project-application-detail/project-application-actions/project-application-actions.component';
import {ProjectApplicationFundingPageComponent} from './project-application/containers/project-application-detail/project-application-funding-page/project-application-funding-page.component';
import {ProjectApplicationFundingDecisionComponent} from './project-application/components/project-application-detail/project-application-funding-decision/project-application-funding-decision.component';
import {ProjectApplicationEligibilityDecisionPageComponent} from './project-application/containers/project-application-detail/project-application-eligibility-decision-page/project-application-eligibility-decision-page.component';
import {ProjectApplicationFormComponent} from './project-application/components/project-application-form/project-application-form.component';
import {ProjectApplicationFormPolicyRadioButtonComponent} from './project-application/components/project-application-form/project-application-form-policy-radio-button/project-application-form-policy-radio-button.component';
import {ProjectApplicationFormWorkPackagesListComponent} from './project-application/components/project-application-form/project-application-form-work-packages-list/project-application-form-work-packages-list.component';
import {ProjectApplicationFormWorkPackageDetailComponent} from './project-application/components/project-application-form/project-application-form-work-package-detail/project-application-form-work-package-detail.component';
import {WorkPackageDetailsComponent} from './project-application/containers/project-application-form-page/project-application-form-work-package-section/work-package-details/work-package-details.component';
import {ProjectApplicationFormWorkPackageSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-work-package-section/project-application-form-work-package-section.component';
import {ProjectApplicationFormPartnerSectionComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-partner-section.component';
import {ProjectApplicationFormPartnerListComponent} from './project-application/components/project-application-form/project-application-form-partner-list/project-application-form-partner-list.component';
import {ProjectApplicationFormPartnerDetailComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-partner-detail/project-application-form-partner-detail.component';
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
import {WorkPackageDeleteActionCellComponent} from './project-application/components/project-application-form/project-application-form-work-packages-list/work-package-delete-action-cell/work-package-delete-action-cell.component';
import {ProjectApplicationPartnerBudgetPageComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-partner-budget-page/project-application-partner-budget-page.component';
import {ProjectApplicationPartnerCoFinancingPageComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-partner-co-financing-page/project-application-partner-co-financing-page.component';
import {ProjectApplicationFormPartnerBudgetComponent} from './project-application/components/project-application-form/project-application-form-partner-budget/project-application-form-partner-budget.component';
import {BudgetTableComponent} from './project-application/components/project-application-form/project-application-form-partner-budget/budget-table/budget-table.component';
import {AgGridTemplateRendererComponent} from './project-application/components/project-application-form/project-application-form-partner-budget/budget-table/ag-grid-template-renderer/ag-grid-template-renderer.component';
import {AgGridModule} from 'ag-grid-angular';
import {ProjectApplicationFormStore} from './project-application/containers/project-application-form-page/services/project-application-form-store.service';
import {ProjectPartnerStore} from './project-application/containers/project-application-form-page/services/project-partner-store.service';
import {ProjectApplicationPartnerIdentityComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-partner-identity/project-application-partner-identity.component';
import {ProjectApplicationFormAssociatedOrganizationsListComponent} from './project-application/components/project-application-form/project-application-form-associated-organizations-list/project-application-form-associated-organizations-list.component';
import {ProjectApplicationFormAssociatedOrgDetailComponent} from './project-application/containers/project-application-form-page/project-application-form-partner-section/project-application-form-associated-org-detail/project-application-form-associated-org-detail.component';
import {ProjectApplicationFormAssociatedOrganizationEditComponent} from './project-application/components/project-application-form/project-application-form-associated-organization-edit/project-application-form-associated-organization-edit.component';
import {ProjectAssociatedOrganizationStore} from './project-application/containers/project-application-form-page/services/project-associated-organization-store.service';
import {ProjectApplicationFormPartnerBudgetOptionsComponent} from './project-application/components/project-application-form/project-application-form-partner-budget-options/project-application-form-partner-budget-options.component';
import {OfficeAndAdministrationTableComponent} from './project-application/components/project-application-form/project-application-form-partner-budget/office-and-administration-table/office-and-administration-table.component';
import {ContributionToggleColumnComponent} from './project-application/components/project-application-form/project-application-form-management-detail/contribution-toggle-column/contribution-toggle-column.component';
import {ProjectApplicationFormPartnerCoFinancingComponent} from './project-application/components/project-application-form/project-application-form-partner-co-financing/project-application-form-partner-co-financing.component';
import {StaffCostsFlatRateTableComponent} from './project-application/components/project-application-form/project-application-form-partner-budget/staff-costs-flat-rate-table/staff-costs-flat-rate-table.component';
import {ProjectApplicationFormIdentificationPageComponent} from './project-application/containers/project-application-form-page/project-application-form-identification-page/project-application-form-identification-page.component';
import {ProjectApplicationFormAssociatedOrgPageComponent} from './project-application/containers/project-application-form-page/project-application-form-associated-org-page/project-application-form-associated-org-page.component';
import {ProjectApplicationFormAddressComponent} from './project-application/components/project-application-form/project-application-form-address/project-application-form-address.component';

@NgModule({
  declarations: [
    DescriptionCellComponent,
    ProjectApplicationComponent,
    ProjectApplyToCallComponent,
    ProjectApplicationSubmissionComponent,
    ProjectApplicationDetailComponent,
    ProjectApplicationDataComponent,
    ProjectApplicationFilesListComponent,
    ProjectApplicationInformationComponent,
    ProjectApplicationFileUploadComponent,
    ProjectApplicationAssessmentsComponent,
    ProjectApplicationFilesComponent,
    ProjectApplicationDecisionsComponent,
    ProjectApplicationActionsComponent,
    ProjectApplicationEligibilityDecisionComponent,
    ProjectApplicationQualityCheckComponent,
    ProjectApplicationEligibilityCheckComponent,
    ActionsCellComponent,
    ProjectApplicationFundingPageComponent,
    ProjectApplicationFundingDecisionComponent,
    ProjectApplicationEligibilityDecisionPageComponent,
    ProjectApplicationFormComponent,
    ProjectApplicationFormPolicyRadioButtonComponent,
    ProjectApplicationFormPartnerSectionComponent,
    ProjectApplicationFormPartnerListComponent,
    ProjectApplicationFormPartnerDetailComponent,
    ProjectApplicationFormPartnerEditComponent,
    ProjectApplicationFormWorkPackagesListComponent,
    ProjectApplicationFormWorkPackageDetailComponent,
    WorkPackageDetailsComponent,
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
    WorkPackageDeleteActionCellComponent,
    ProjectApplicationPartnerBudgetPageComponent,
    ProjectApplicationPartnerCoFinancingPageComponent,
    ProjectApplicationFormPartnerBudgetComponent,
    ProjectApplicationFormPartnerCoFinancingComponent,
    ProjectApplicationFormPartnerBudgetOptionsComponent,
    OfficeAndAdministrationTableComponent,
    BudgetTableComponent,
    AgGridTemplateRendererComponent,
    ProjectApplicationPartnerIdentityComponent,
    ProjectApplicationFormAssociatedOrganizationEditComponent,
    ProjectApplicationFormIdentificationPageComponent,
    ProjectApplicationFormAssociatedOrgPageComponent,
    StaffCostsFlatRateTableComponent,
    ProjectApplicationFormAddressComponent
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes),
    AgGridModule,
    AgGridModule.withComponents([AgGridTemplateRendererComponent]),
  ],
  exports: [
    ProjectApplicationDetailComponent
  ],
  providers: [
    DatePipe,
    ProjectStore,
    ProjectApplicationFormSidenavService,
    ProjectApplicationFormStore,
    ProjectAcronymResolver,
    ProjectPartnerStore,
    ProjectAssociatedOrganizationStore,
  ]
})
export class ProjectModule {
}
