import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {routes} from './call-routing.module';
import {CallPageComponent} from './containers/call-page/call-page.component';
import {CallListComponent} from './components/call-list/call-list.component';
import {SharedModule} from '../common/shared-module';
import {CallDetailComponent} from './components/call-detail/call-detail.component';
import {CallConfigurationComponent} from './containers/call-configuration/call-configuration.component';
import {CallStore} from './services/call-store.service';
import {CallActionCellComponent} from './components/call-list/call-action-cell/call-action-cell.component';
import {CallPriorityTreeComponent} from './components/call-priority-tree/call-priority-tree.component';
import {CallNameResolver} from './services/call-name.resolver';
import {CallStrategiesComponent} from './components/call-detail/call-strategies/call-strategies.component';
import {CallFundsComponent} from './components/call-detail/call-funds/call-funds.component';

@NgModule({
  declarations: [
    CallPageComponent,
    CallListComponent,
    CallDetailComponent,
    CallConfigurationComponent,
    CallActionCellComponent,
    CallPriorityTreeComponent,
    CallStrategiesComponent,
    CallFundsComponent,
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes)
  ],
  providers: [
    CallStore,
    CallNameResolver,
  ],
})
export class CallModule {
}
