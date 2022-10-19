import {NgModule} from '@angular/core';
import {SharedModule} from '@common/shared-module';
import {RouterModule} from '@angular/router';
import {PaymentsPageComponent} from './payments-page/payments-page.component';
import {paymentsRoutes} from './payments-routing.module';
import {PaymentsToProjectPageComponent} from './payments-to-projects-page/payments-to-project-page.component';
import {AdvancePaymentsPageComponent} from './advance-payments-page/advance-payments-page.component';
import {
  PaymentsToProjectDetailPageComponent
} from './payments-to-projects-page/payments-to-project-detail-page/payments-to-project-detail-page.component';
import {
  PaymentsToProjectDetailBreadcrumbResolver
} from './payments-to-projects-page/payments-to-project-detail.resolver';
import {
  PaymentsToProjectAttachmentsComponent
} from './payments-to-projects-page/payments-to-project-detail-page/payments-to-project-attachments/payments-to-project-attachments.component';
import {
  AdvancePaymentsDetailPageComponent
} from './advance-payments-page/advance-payments-detail-page/advance-payments-detail-page.component';
import {NgxMatSelectSearchModule} from 'ngx-mat-select-search';



@NgModule({
  declarations: [
    PaymentsPageComponent,
    PaymentsToProjectPageComponent,
    AdvancePaymentsPageComponent,
    PaymentsToProjectDetailPageComponent,
    PaymentsToProjectAttachmentsComponent,
    AdvancePaymentsDetailPageComponent
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(paymentsRoutes),
    NgxMatSelectSearchModule
  ],
  providers: [
    PaymentsToProjectDetailBreadcrumbResolver
  ]
})
export class PaymentsModule {
}
