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
import {
    AdvancePaymentsAttachmentsComponent
} from './advance-payments-page/advance-payments-detail-page/advance-payments-attachments/advance-payments-attachments.component';
import {PaymentsPageSidenavService} from './payments-page-sidenav.service';
import {PaymentsToEcPageComponent} from './payments-to-ec/payments-to-ec-page.component';
import {
  PaymentToEcDetailPageComponent
} from './payments-to-ec/payments-to-ec-detail-page/payment-to-ec-detail-page.component';
import {
  PaymentToEcSummaryTabComponent
} from './payments-to-ec/payments-to-ec-detail-page/summary-tab/payment-to-ec-summary-tab.component';
import { PaymentToEcSummaryAttachmentsComponent } from './payments-to-ec/payments-to-ec-detail-page/summary-tab/attachments/payment-to-ec-summary-attachments.component';

@NgModule({
    declarations: [
        PaymentsPageComponent,
        PaymentsToProjectPageComponent,
        AdvancePaymentsPageComponent,
        PaymentsToEcPageComponent,
        PaymentToEcDetailPageComponent,
        PaymentToEcSummaryTabComponent,
        PaymentsToProjectDetailPageComponent,
        PaymentsToProjectAttachmentsComponent,
        AdvancePaymentsDetailPageComponent,
        AdvancePaymentsAttachmentsComponent,
        PaymentToEcSummaryAttachmentsComponent
    ],
    imports: [
        SharedModule,
        RouterModule.forChild(paymentsRoutes),
    ],
    providers: [
        PaymentsToProjectDetailBreadcrumbResolver,
        PaymentsPageSidenavService
    ]
})
export class PaymentsModule {
}
