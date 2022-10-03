import {NgModule} from '@angular/core';
import {SharedModule} from '@common/shared-module';
import {RouterModule} from '@angular/router';
import {PaymentsPageComponent} from './payments-page/payments-page.component';
import {paymentsRoutes} from './payments-routing.module';
import {PaymentsToProjectPageComponent} from './payments-to-projects-page/payments-to-project-page.component';
import {AdvancedPaymentsPageComponent} from './advanced-payments-page/advanced-payments-page.component';
import {
  PaymentsToProjectDetailPageComponent
} from './payments-to-projects-page/payments-to-project-detail-page/payments-to-project-detail-page.component';
import {
  PaymentsToProjectDetailBreadcrumbResolver
} from './payments-to-projects-page/payments-to-project-detail.resolver';

@NgModule({
  declarations: [
    PaymentsPageComponent,
    PaymentsToProjectPageComponent,
    AdvancedPaymentsPageComponent,
    PaymentsToProjectDetailPageComponent
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(paymentsRoutes),
  ],
  providers: [
    PaymentsToProjectDetailBreadcrumbResolver
  ]
})
export class PaymentsModule {
}
