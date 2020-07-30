import {NgModule} from '@angular/core';
import {CallRoutingModule} from './call-routing.module';
import {CallPageComponent} from './containers/call-page/call-page.component';
import {CallListComponent} from './components/call-list/call-list.component';
import {SharedModule} from '../common/shared-module';
import {CallDetailComponent} from './components/call-detail/call-detail.component';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {CoreModule} from '../common/core-module';
import {CallConfigurationComponent} from './containers/call-configuration/call-configuration.component';
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS, MatMomentDateModule} from '@angular/material-moment-adapter';
import {MAT_DATE_LOCALE} from '@angular/material/core';

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
    MatMomentDateModule,
    CoreModule
  ],
  providers: [
    MatDatepickerModule,
    {provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: {useUtc: true}},
    {provide: MAT_DATE_LOCALE, useValue: 'en-GB'}],
})
export class CallModule {
}
