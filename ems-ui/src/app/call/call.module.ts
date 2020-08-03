import {NgModule} from '@angular/core';
import {CallRoutingModule} from './call-routing.module';
import {CallPageComponent} from './containers/call-page/call-page.component';
import {CallListComponent} from './components/call-list/call-list.component';
import {SharedModule} from '../common/shared-module';
import {CallDetailComponent} from './components/call-detail/call-detail.component';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {CoreModule} from '../common/core-module';
import {CallConfigurationComponent} from './containers/call-configuration/call-configuration.component';
import {NGX_MAT_DATE_FORMATS, NgxMatDatetimePickerModule} from '@angular-material-components/datetime-picker';
import {NgxMatMomentModule} from '@angular-material-components/moment-adapter';
import {CallStore} from './services/call-store.service';

@NgModule({
  declarations: [
    CallPageComponent,
    CallListComponent,
    CallDetailComponent,
    CallConfigurationComponent
  ],
  imports: [
    CallRoutingModule,
    SharedModule,
    MatDatepickerModule,
    NgxMatDatetimePickerModule,
    NgxMatMomentModule,
    CoreModule
  ],
  providers: [
    CallStore,
    MatDatepickerModule,
    NgxMatDatetimePickerModule,
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
