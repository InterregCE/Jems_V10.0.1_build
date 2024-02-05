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
import {
  PaymentToEcSummaryAttachmentsComponent
} from './payments-to-ec/payments-to-ec-detail-page/summary-tab/attachments/payment-to-ec-summary-attachments.component';
import {
  PaymentToEcRegularProjectsTabComponent
} from './payments-to-ec/payments-to-ec-detail-page/ftls-tab/payment-to-ec-regular-projects-tab.component';
import {
  PaymentToEcCumulativeTableComponent
} from './payments-to-ec/payments-to-ec-detail-page/payment-to-ec-cumulative-table/payment-to-ec-cumulative-table.component';
import {
  RegularPaymentsNotFlagged9495Component
} from './payments-to-ec/payments-to-ec-detail-page/ftls-tab/regular-payments-not-flagged-9495/regular-payments-not-flagged-9495.component';
import {
  PaymentToEcSelectTableComponent
} from './payments-to-ec/payments-to-ec-detail-page/ftls-tab/payment-to-ec-select-table/payment-to-ec-select-table.component';
import {
  PaymentToEcCorrectionTabComponent
} from './payments-to-ec/payments-to-ec-detail-page/payment-to-ec-correction-tab/payment-to-ec-correction-tab.component';
import {
  PaymentToEcCorrectionSelectTableComponent
} from './payments-to-ec/payments-to-ec-detail-page/payment-to-ec-correction-select-table/payment-to-ec-correction-select-table.component';
import {PaymentsAuditPageComponent} from './payments-audit/payments-audit-page.component';
import {
  PaymentToEcAuditAttachmentsComponent
} from './payments-audit/attachments/payment-to-ec-audit-attachments.component';
import {
  PaymentToEcFinalizeTabComponent
} from './payments-to-ec/payments-to-ec-detail-page/payment-to-ec-finalize-tab/payment-to-ec-finalize-tab.component';
import {AccountsPageComponent} from './accounts-page/accounts-page.component';
import {AccountDetailComponent} from './accounts-page/account-detail/account-detail.component';
import {
  AccountsSummaryTabComponent
} from './accounts-page/account-detail/accounts-summary-tab/accounts-summary-tab.component';
import {
  AccountsFinalizeTabComponent
} from './accounts-page/account-detail/accounts-finalize-tab/accounts-finalize-tab.component';
import {
  ProjectsFlaggedArt9495TabComponent
} from './payments-to-ec/payments-to-ec-detail-page/projects-flagged-art-9495-tab/projects-flagged-art-9495-tab.component';
import {
  AccountsCorrectionSelectTableComponent
} from './accounts-page/account-detail/accounts-correction-select-table/accounts-correction-select-table.component';
import {
  AccountsCorrectionCumulativeTableComponent
} from './accounts-page/account-detail/accounts-correction-cumulative-table/accounts-correction-cumulative-table.component';
import {AccountsCorrectionTabComponent} from './accounts-page/account-detail/accounts-correction-tab/accounts-correction-tab.component';

@NgModule({
  declarations: [
    PaymentsPageComponent,
    PaymentsToProjectPageComponent,
    AdvancePaymentsPageComponent,
    PaymentsToEcPageComponent,
    PaymentToEcDetailPageComponent,
    PaymentToEcSummaryTabComponent,
    PaymentToEcRegularProjectsTabComponent,
    PaymentsToProjectDetailPageComponent,
    PaymentsToProjectAttachmentsComponent,
    AdvancePaymentsDetailPageComponent,
    AdvancePaymentsAttachmentsComponent,
    PaymentToEcSummaryAttachmentsComponent,
    PaymentToEcCumulativeTableComponent,
    RegularPaymentsNotFlagged9495Component,
    PaymentToEcSelectTableComponent,
    PaymentToEcCorrectionTabComponent,
    PaymentToEcCorrectionSelectTableComponent,
    PaymentsAuditPageComponent,
    PaymentToEcAuditAttachmentsComponent,
    PaymentToEcFinalizeTabComponent,
    AccountsPageComponent,
    AccountDetailComponent,
    AccountsSummaryTabComponent,
    AccountsCorrectionTabComponent,
    AccountsCorrectionSelectTableComponent,
    AccountsCorrectionCumulativeTableComponent,
    AccountsFinalizeTabComponent,
    ProjectsFlaggedArt9495TabComponent
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
