import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {routes} from './call-routing.module';
import {CallPageComponent} from './containers/call-page/call-page.component';
import {SharedModule} from '@common/shared-module';
import {CallPriorityTreeComponent} from './components/call-priority-tree/call-priority-tree.component';
import {CallNameResolver} from './services/call-name.resolver';
import {CallStrategiesComponent} from './call-detail-page/call-strategies/call-strategies.component';
import {CallFundsComponent} from './call-detail-page/call-funds/call-funds.component';
import {CallFlatRatesComponent} from './call-budget-settings-page/call-flat-rates/call-flat-rates.component';
import {CallFlatRatesToggleColumnComponent} from './call-budget-settings-page/call-flat-rates/call-flat-rates-toggle-column/call-flat-rates-toggle-column.component';
import {CallBudgetSettingsPageComponent} from './call-budget-settings-page/call-budget-settings-page.component';
import {CallPageSidenavService} from './services/call-page-sidenav.service';
import {CallLumpSumsComponent} from './call-budget-settings-page/call-lump-sums/call-lump-sums.component';
import {CallUnitCostsComponent} from './call-budget-settings-page/call-unit-costs/call-unit-costs.component';
import {ProgrammeEditableStateStore} from '../programme/programme-page/services/programme-editable-state-store.service';
import {CallDetailPageComponent} from './call-detail-page/call-detail-page.component';
import {ApplicationFormConfigurationPageComponent} from './application-form-configuration-page/application-form-configuration-page.component';
import {CallStateAidsComponent} from './call-detail-page/call-state-aids/call-state-aids.component';
import {CallAllowedRealCostsComponent} from './call-budget-settings-page/call-allow-real-costs/call-allow-real-costs.component';
import {PreSubmissionCheckSettingsPageComponent} from './pre-submission-check-settings-page/pre-submission-check-settings-page.component';

@NgModule({
  declarations: [
    CallPageComponent,
    CallPriorityTreeComponent,
    CallStrategiesComponent,
    CallFundsComponent,
    CallFlatRatesComponent,
    CallFlatRatesToggleColumnComponent,
    CallBudgetSettingsPageComponent,
    CallLumpSumsComponent,
    CallUnitCostsComponent,
    CallDetailPageComponent,
    ApplicationFormConfigurationPageComponent,
    CallStateAidsComponent,
    CallAllowedRealCostsComponent,
    PreSubmissionCheckSettingsPageComponent
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes)
  ],
  providers: [
    ProgrammeEditableStateStore,
    CallNameResolver,
    CallPageSidenavService
  ],
})
export class CallModule {
}
