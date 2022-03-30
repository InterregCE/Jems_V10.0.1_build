import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {routes} from './programme-routing.module';
import {SharedModule} from '@common/shared-module';
import {ProgrammeAreaComponent} from './programme-page/containers/programme-area/programme-area.component';
import {ProgrammeNutsInfoComponent} from './programme-page/components/programme-nuts-info/programme-nuts-info.component';
import {ProgrammeRegionsTreeComponent} from './programme-page/components/programme-regions-tree/programme-regions-tree.component';
import {ProgrammeSelectedRegionsComponent} from './programme-page/components/programme-selected-regions/programme-selected-regions.component';
import {ProgrammeRegionsComponent} from './programme-page/components/programme-regions/programme-regions.component';
import {ProgrammeIndicatorsOverviewPageComponent} from './programme-page/containers/programme-indicators-overview-page/programme-indicators-overview-page.component';
import {ProgrammeOutputIndicatorsListComponent} from './programme-page/components/programme-output-indicators-list/programme-output-indicators-list.component';
import {ProgrammeResultIndicatorsListComponent} from './programme-page/components/programme-result-indicators-list/programme-result-indicators-list.component';
import {IndicatorsStore} from './programme-page/services/indicators-store.service';
import {ProgrammeOutputIndicatorDetailComponent} from './programme-page/components/programme-output-indicator-detail/programme-output-indicator-detail.component';
import {ProgrammeOutputIndicatorSubmissionPageComponent} from './programme-page/containers/programme-output-indicator-submission-page/programme-output-indicator-submission-page.component';
import {ProgrammeResultIndicatorSubmissionPageComponent} from './programme-page/containers/programme-result-indicator-submission-page/programme-result-indicator-submission-page.component';
import {ProgrammeResultIndicatorDetailComponent} from './programme-page/components/programme-result-indicator-detail/programme-result-indicator-detail.component';
import {ProgrammeFundsComponent} from './programme-funds/programme-funds.component';
import {ProgrammeStrategiesPageComponent} from './programme-page/containers/programme-strategies-page/programme-strategies-page.component';
import {ProgrammeStrategiesComponent} from './programme-page/components/programme-strategies/programme-strategies.component';
import {ProgrammePageSidenavService} from './programme-page/services/programme-page-sidenav.service';
import {ProgrammeLanguagesComponent} from './programme-page/components/programme-languages/programme-languages.component';
import {ProgrammeLanguagesPageComponent} from './programme-page/containers/programme-languages-page/programme-languages-page.component';
import {ProgrammeLegalStatusComponent} from './programme-page/containers/programme-legal-status/programme-legal-status.component';
import {ProgrammeLegalStatusListComponent} from './programme-page/components/programme-legal-status-list/programme-legal-status-list.component';
import {ProgrammeSimplifiedCostOptionsComponent} from './programme-page/containers/programme-simplified-cost-options/programme-simplified-cost-options.component';
import {LumpSumsStore} from './programme-page/services/lump-sums-store.service';
import {ProgrammeLumpSumsListComponent} from './programme-page/components/programme-lump-sums-list/programme-lump-sums-list.component';
import {ProgrammeLumpSumsSubmissionPageComponent} from './programme-page/containers/programme-lump-sums-submission-page/programme-lump-sums-submission-page.component';
import {ProgrammeLumpSumDetailComponent} from './programme-page/components/programme-lump-sum-detail/programme-lump-sum-detail.component';
import {ProgrammeUnitCostsListComponent} from './programme-page/components/programme-unit-costs-list/programme-unit-costs-list.component';
import {UnitCostStore} from './programme-page/services/unit-cost-store.service';
import {ProgrammeUnitCostDetailComponent} from './programme-page/components/programme-unit-cost-detail/programme-unit-cost-detail.component';
import {ProgrammeUnitCostsSubmissionPageComponent} from './programme-page/containers/programme-unit-costs-submission-page/programme-unit-costs-submission-page.component';
import {ProgrammePriorityDetailPageComponent} from './priorities/programme-priority-list-page/programme-priority-detail-page/programme-priority-detail-page.component';
import {ProgrammePriorityListPageComponent} from './priorities/programme-priority-list-page/programme-priority-list-page.component';
import {ProgrammeEditableStateStore} from './programme-page/services/programme-editable-state-store.service';
import {TranslationManagementPageComponent} from './translation-management-page/translation-management-page.component';
import {ProgrammeStateAidComponent} from './programme-page/containers/programme-state-aid/programme-state-aid.component';
import {ProgrammeStateAidListComponent} from './programme-page/components/programme-state-aid-list/programme-state-aid-list.component';
import {ProgrammeBasicDataComponent} from './programme-basic-data/programme-basic-data.component';
import {ProgrammeDataExportComponent} from './programme-data-export/programme-data-export.component';
import {ProgrammeChecklistListPageComponent} from './programme-checklist-list-page/programme-checklist-list-page.component';
import {ProgrammeChecklistDetailPageComponent } from './programme-checklist-list-page/programme-checklist-detail-page/programme-checklist-detail-page.component';
import {ProgrammeChecklistHeadlineComponent} from './programme-checklist-list-page/programme-checklist-detail-page/components/programme-checklist-headline/programme-checklist-headline.component';
import {ProgrammeChecklistOptionsToggleComponent} from './programme-checklist-list-page/programme-checklist-detail-page/components/programme-checklist-options-toggle/programme-checklist-options-toggle.component';

@NgModule({
  declarations: [
    ProgrammeAreaComponent,
    ProgrammeNutsInfoComponent,
    ProgrammeRegionsComponent,
    ProgrammeRegionsTreeComponent,
    ProgrammeSelectedRegionsComponent,
    ProgrammeNutsInfoComponent,
    ProgrammeIndicatorsOverviewPageComponent,
    ProgrammeOutputIndicatorsListComponent,
    ProgrammeResultIndicatorsListComponent,
    ProgrammeOutputIndicatorDetailComponent,
    ProgrammeOutputIndicatorSubmissionPageComponent,
    ProgrammeResultIndicatorSubmissionPageComponent,
    ProgrammeResultIndicatorDetailComponent,
    ProgrammeLanguagesPageComponent,
    ProgrammeLanguagesComponent,
    ProgrammeStrategiesPageComponent,
    ProgrammeStrategiesComponent,
    ProgrammeLegalStatusComponent,
    ProgrammeLegalStatusListComponent,
    ProgrammeSimplifiedCostOptionsComponent,
    ProgrammeLumpSumsListComponent,
    ProgrammeLumpSumsSubmissionPageComponent,
    ProgrammeLumpSumDetailComponent,
    ProgrammeUnitCostsListComponent,
    ProgrammeUnitCostDetailComponent,
    ProgrammeUnitCostsSubmissionPageComponent,
    ProgrammePriorityListPageComponent,
    ProgrammePriorityDetailPageComponent,
    TranslationManagementPageComponent,
    ProgrammeStateAidComponent,
    ProgrammeStateAidListComponent,
    ProgrammeBasicDataComponent,
    ProgrammeFundsComponent,
    ProgrammeDataExportComponent,
    ProgrammeChecklistListPageComponent,
    ProgrammeChecklistDetailPageComponent,
    ProgrammeChecklistHeadlineComponent,
    ProgrammeChecklistOptionsToggleComponent
  ],
  providers: [
    IndicatorsStore,
    LumpSumsStore,
    UnitCostStore,
    ProgrammeEditableStateStore,
    ProgrammePageSidenavService
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes)
  ]
})
export class ProgrammeModule {
}
