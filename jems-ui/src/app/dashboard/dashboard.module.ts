import {NgModule} from '@angular/core';
import {SharedModule} from '../common/shared-module';
import {RouterModule} from '@angular/router';
import {routes} from './dashboard-routing.module';
import {ApplicantDashboardPageComponent} from './applicant-dashboard-page/applicant-dashboard-page.component';

@NgModule({
  declarations: [
    ApplicantDashboardPageComponent,
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes),
  ],
})
export class DashboardModule {
}
