import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {routes} from './programme-routing.module';
import {SharedModule} from '../common/shared-module';
import {ProgrammePageComponent} from './programme-page/containers/programme-page/programme-page.component';
import {ProgrammeDataComponent} from './programme-page/components/programme-data/programme-data.component';
import {ProgrammePrioritiesComponent} from './programme-page/containers/programme-priorities/programme-priorities.component';
import {ProgrammePriorityItemsComponent} from './programme-page/components/programme-priority-items/programme-priority-items.component';
import {ProgrammePrioritySubmissionComponent} from './programme-page/components/programme-priority-submission/programme-priority-submission.component';
import {ProgrammePolicyCheckboxComponent} from './programme-page/components/programme-priority-submission/programme-policy-checkbox/programme-policy-checkbox.component';
import {ProgrammePriorityComponent} from './programme-page/containers/programme-priority/programme-priority.component';
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
import {ProgrammeBasicFundsComponent} from './programme-page/components/programme-basic-funds/programme-basic-funds.component';
import {ProgrammeStrategiesPageComponent} from './programme-page/containers/programme-strategies-page/programme-strategies-page.component';
import {ProgrammeStrategiesComponent} from './programme-page/components/programme-strategies/programme-strategies.component';
import {ProgrammePageSidenavService} from './programme-page/services/programme-page-sidenav.service';
import {ProgrammeLanguagesComponent} from './programme-page/components/programme-languages/programme-languages.component';
import {ProgrammeLanguagesPageComponent} from './programme-page/containers/programme-languages-page/programme-languages-page.component';
import {ProgrammeLegalStatusComponent} from './programme-page/containers/programme-legal-status/programme-legal-status.component';
import {ProgrammeLegalStatusListComponent} from './programme-page/components/programme-legal-status-list/programme-legal-status-list.component';
import { ProgrammeSimplifiedCostOptionsComponent } from './programme-page/containers/programme-simplified-cost-options/programme-simplified-cost-options.component';
import {LumpSumsStore} from './programme-page/services/lump-sums-store.service';
import { ProgrammeLumpSumsListComponent } from './programme-page/components/programme-lump-sums-list/programme-lump-sums-list.component';
import { ProgrammeLumpSumsSubmissionPageComponent } from './programme-page/containers/programme-lump-sums-submission-page/programme-lump-sums-submission-page.component';
import { ProgrammeLumpSumDetailComponent } from './programme-page/components/programme-lump-sum-detail/programme-lump-sum-detail.component';

@NgModule({
  declarations: [
    ProgrammePageComponent,
    ProgrammeDataComponent,
    ProgrammePrioritiesComponent,
    ProgrammePriorityItemsComponent,
    ProgrammePrioritySubmissionComponent,
    ProgrammePolicyCheckboxComponent,
    ProgrammePolicyCheckboxComponent,
    ProgrammePriorityComponent,
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
    ProgrammeBasicFundsComponent,
    ProgrammeLanguagesPageComponent,
    ProgrammeLanguagesComponent,
    ProgrammeStrategiesPageComponent,
    ProgrammeStrategiesComponent,
    ProgrammeLegalStatusComponent,
    ProgrammeLegalStatusListComponent,
    ProgrammeSimplifiedCostOptionsComponent,
    ProgrammeLumpSumsListComponent,
    ProgrammeLumpSumsSubmissionPageComponent,
    ProgrammeLumpSumDetailComponent
  ],
  providers: [
    IndicatorsStore,
    LumpSumsStore,
    ProgrammePageSidenavService
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes)
  ]
})
export class ProgrammeModule {
}
