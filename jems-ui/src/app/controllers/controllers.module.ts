import {NgModule} from '@angular/core';
import {SharedModule} from '@common/shared-module';
import {RouterModule} from '@angular/router';
import {InstitutionsPageComponent} from './institutions-page/institutions-page.component';
import {routes} from './controllers-routing.module';
import {ControllersPageSidenavService} from './controllers-page-sidenav.service';
import {
  ControllerInstitutionDetailComponent
} from './institutions-page/controller-institution-detail/controller-institution-detail.component';
import {
  ControllerInstitutionNutsComponent
} from './institutions-page/controller-institution-detail/controller-institution-nuts/controller-institution-nuts.component';
import {ProgrammeEditableStateStore} from '../programme/programme-page/services/programme-editable-state-store.service';
import {InstitutionsAssignmentsPageComponent} from './institution-assignments-page/institutions-assignments-page.component';

@NgModule({
  declarations: [
    InstitutionsPageComponent,
    ControllerInstitutionDetailComponent,
    ControllerInstitutionNutsComponent,
    InstitutionsAssignmentsPageComponent
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes),
  ],
  providers: [
    ProgrammeEditableStateStore,
    ControllersPageSidenavService
  ]
})
export class ControllersModule {
}
