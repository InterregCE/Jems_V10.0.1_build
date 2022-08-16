import {NgModule} from '@angular/core';
import {SharedModule} from '@common/shared-module';
import {RouterModule} from '@angular/router';
import {PaymentsPageComponent} from "./payments-page/payments-page.component";
import {paymentsRoutes} from "./payments-routing.module";
import {PaymentsToProjectPageComponent} from "./payments-to-projects-page/payments-to-project-page.component";
import {AdvancedPaymentsPageComponent} from "./advanced-payments-page/advanced-payments-page.component";

@NgModule({
  declarations: [
    PaymentsPageComponent,
    PaymentsToProjectPageComponent,
    AdvancedPaymentsPageComponent
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(paymentsRoutes),
  ],
  providers: [

  ]
})
export class PaymentsModule {
}
