import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {routes} from './call-routing.module';
import {CallPageComponent} from './containers/call-page/call-page.component';
import {SharedModule} from '../common/shared-module';
import {CallDetailComponent} from './components/call-detail/call-detail.component';
import {CallConfigurationComponent} from './containers/call-configuration/call-configuration.component';
import {CallStore} from './services/call-store.service';
import {CallPriorityTreeComponent} from './components/call-priority-tree/call-priority-tree.component';
import {CallNameResolver} from './services/call-name.resolver';
import {CallStrategiesComponent} from './components/call-detail/call-strategies/call-strategies.component';
import {CallFundsComponent} from './components/call-detail/call-funds/call-funds.component';
import {CallFlatRatesComponent} from './components/call-detail/call-flat-rates/call-flat-rates.component';
import {CallFlatRatesToggleColumnComponent} from './components/call-detail/call-flat-rates/call-flat-rates-toggle-column/call-flat-rates-toggle-column.component';
import {CallBudgetSettingsPageComponent} from './containers/call-budget-settings-page/call-budget-settings-page.component';
import {CallPageSidenavService} from './services/call-page-sidenav.service';
import {CallLumpSumsComponent} from './components/call-detail/call-lump-sums/call-lump-sums.component';
import {CallUnitCostsComponent} from './components/call-detail/call-unit-costs/call-unit-costs.component';
import {ProgrammeEditableStateStore} from '../programme/programme-page/services/programme-editable-state-store.service';

@NgModule({
  declarations: [
    CallPageComponent,
    CallDetailComponent,
    CallConfigurationComponent,
    CallPriorityTreeComponent,
    CallStrategiesComponent,
    CallFundsComponent,
    CallFlatRatesComponent,
    CallFlatRatesToggleColumnComponent,
    CallBudgetSettingsPageComponent,
    CallLumpSumsComponent,
    CallUnitCostsComponent,
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes)
  ],
  providers: [
    CallStore,
    ProgrammeEditableStateStore,
    CallNameResolver,
    CallPageSidenavService
  ],
})
export class CallModule {
}
