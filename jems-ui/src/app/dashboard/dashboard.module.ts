import {NgModule} from '@angular/core';
import {SharedModule} from '@common/shared-module';
import {RouterModule} from '@angular/router';
import {routes} from './dashboard-routing.module';
import {DashboardPageComponent} from './dashboard-page/dashboard-page.component';

@NgModule({
  declarations: [
    DashboardPageComponent,
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes),
  ],
})
export class DashboardModule {
}
