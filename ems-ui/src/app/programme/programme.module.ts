import {NgModule} from '@angular/core';
import {ProgrammeRoutingModule} from './programme-routing.module';
import {SharedModule} from '../common/shared-module';
import {CoreModule} from '../common/core-module';
import {ProgrammePageComponent} from './programme-page/containers/programme-page/programme-page.component';
import {ProgrammeDataComponent} from './programme-page/components/programme-data/programme-data.component';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatSelectModule} from '@angular/material/select';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {ProgrammePrioritiesComponent} from './programme-page/containers/programme-priorities/programme-priorities.component';
import {ProgrammePriorityItemsComponent} from './programme-page/components/programme-priority-items/programme-priority-items.component';
import {ProgrammePrioritySubmissionComponent} from './programme-page/components/programme-priority-submission/programme-priority-submission.component';
import {ProgrammePolicyCheckboxComponent} from './programme-page/components/programme-priority-submission/programme-policy-checkbox/programme-policy-checkbox.component';
import { ProgrammePriorityComponent } from './programme-page/containers/programme-priority/programme-priority.component';
import {ProgrammeNavigationStateManagementService} from './programme-page/services/programme-navigation-state-management.service';

@NgModule({
  declarations: [
    ProgrammePageComponent,
    ProgrammeDataComponent,
    ProgrammePrioritiesComponent,
    ProgrammePriorityItemsComponent,
    ProgrammePrioritySubmissionComponent,
    ProgrammePolicyCheckboxComponent,
    ProgrammePolicyCheckboxComponent,
    ProgrammePriorityComponent
  ],
  providers: [
    ProgrammeNavigationStateManagementService
  ],
  imports: [
    SharedModule,
    ProgrammeRoutingModule,
    CoreModule,
    MatDatepickerModule,
    MatSelectModule,
    MatCheckboxModule
  ]
})
export class ProgrammeModule {
}
