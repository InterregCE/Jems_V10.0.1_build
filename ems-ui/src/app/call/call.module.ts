import {NgModule} from '@angular/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {NgxMatMomentModule} from '@angular-material-components/moment-adapter';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {RouterModule} from '@angular/router';
import {NGX_MAT_DATE_FORMATS, NgxMatDatetimePickerModule} from '@angular-material-components/datetime-picker';

import {routes} from './call-routing.module';
import {CallPageComponent} from './containers/call-page/call-page.component';
import {CallListComponent} from './components/call-list/call-list.component';
import {SharedModule} from '../common/shared-module';
import {CallDetailComponent} from './components/call-detail/call-detail.component';
import {CoreModule} from '../common/core-module';
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
    CoreModule,
    RouterModule.forChild(routes),
    MatDatepickerModule,
    NgxMatDatetimePickerModule,
    NgxMatMomentModule,
    MatCheckboxModule
  ],
  providers: [
    CallStore,
    MatDatepickerModule,
    NgxMatDatetimePickerModule,
    CallNameResolver,
    {
      provide: NGX_MAT_DATE_FORMATS, useValue: {
        parse: {
          dateInput: ['YYYY-MM-DDTHH:MM:00Z', 'l, LT']
        },
        display: {
          dateInput: 'l, LT',
          monthYearLabel: 'MMM YYYY',
          dateA11yLabel: 'LL',
          monthYearA11yLabel: 'MMMM YYYY'
        },
        useUtc: true
      }
    }
  ],
})
export class CallModule {
}
