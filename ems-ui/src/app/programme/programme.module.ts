import {NgModule} from '@angular/core';
import {ProgrammeRoutingModule} from './programme-routing.module';
import {SharedModule} from '../common/shared-module';
import {CoreModule} from '../common/core-module';
import {ProgrammePageComponent} from './programme-page/containers/programme-page/programme-page.component';
import {ProgrammeDataComponent} from './programme-page/components/programme-data/programme-data.component';
import {MatDatepickerModule} from '@angular/material/datepicker';

@NgModule({
  declarations: [
    ProgrammePageComponent,
    ProgrammeDataComponent],
  imports: [
    SharedModule,
    ProgrammeRoutingModule,
    CoreModule,
    MatDatepickerModule
  ]
})
export class ProgrammeModule {
}
