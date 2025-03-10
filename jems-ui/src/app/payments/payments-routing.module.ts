import {RouterModule, Routes} from '@angular/router';
import {UserRoleCreateDTO} from '@cat/api';
import {PaymentsPageComponent} from './payments-page/payments-page.component';
import {PaymentsToProjectDetailPageComponent} from './payments-to-projects-page/payments-to-project-detail-page/payments-to-project-detail-page.component';
import {PaymentsToProjectDetailBreadcrumbResolver} from './payments-to-projects-page/payments-to-project-detail.resolver';
import {AdvancePaymentsDetailPageComponent} from './advance-payments-page/advance-payments-detail-page/advance-payments-detail-page.component';
import {PaymentsToEcPageComponent} from './payments-to-ec/payments-to-ec-page.component';
import {AdvancePaymentsPageComponent} from './advance-payments-page/advance-payments-page.component';
import {PaymentsToProjectPageComponent} from './payments-to-projects-page/payments-to-project-page.component';
import {PaymentToEcDetailPageComponent} from './payments-to-ec/payments-to-ec-detail-page/payment-to-ec-detail-page.component';
import {PermissionGuard} from '../security/permission.guard';
import {PaymentToEcSummaryTabComponent} from './payments-to-ec/payments-to-ec-detail-page/summary-tab/payment-to-ec-summary-tab.component';
import {
  PaymentToEcCorrectionTabComponent
} from './payments-to-ec/payments-to-ec-detail-page/payment-to-ec-correction-tab/payment-to-ec-correction-tab.component';
import {PaymentsAuditPageComponent} from './payments-audit/payments-audit-page.component';
import {PaymentToEcFinalizeTabComponent} from './payments-to-ec/payments-to-ec-detail-page/payment-to-ec-finalize-tab/payment-to-ec-finalize-tab.component';
import {AccountsPageComponent} from './accounts-page/accounts-page.component';
import {AccountsSummaryTabComponent} from './accounts-page/account-detail/accounts-summary-tab/accounts-summary-tab.component';
import {AccountDetailComponent} from './accounts-page/account-detail/account-detail.component';
import {AccountsCorrectionTabComponent} from './accounts-page/account-detail/accounts-correction-tab/accounts-correction-tab.component';
import {AccountsWithdrawnTabComponent} from './accounts-page/account-detail/accounts-withdrawn-tab/accounts-withdrawn-tab.component';
import {AccountsFinalizeTabComponent} from './accounts-page/account-detail/accounts-finalize-tab/accounts-finalize-tab.component';
import {
  ProjectsFlaggedArt9495TabComponent
} from './payments-to-ec/payments-to-ec-detail-page/projects-flagged-art-9495-tab/projects-flagged-art-9495-tab.component';
import {AccountsReconciliationTabComponent} from './accounts-page/account-detail/accounts-reconciliation-tab/accounts-reconciliation-tab.component';
import {ConfirmLeaveGuard} from '../security/confirm-leave.guard';
import {NgModule} from '@angular/core';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {
  PaymentToEcRegularProjectsTabComponent
} from './payments-to-ec/payments-to-ec-detail-page/projects-not-flagged-art-9495-tab/payment-to-ec-regular-projects-tab.component';

const paymentsRoutes: Routes = [
  {
    path: '',
    data: {
      breadcrumb: 'payments.breadcrumb',
      permissionsOnly: [
        PermissionsEnum.PaymentsRetrieve,
        PermissionsEnum.PaymentsUpdate,
        PermissionsEnum.AdvancePaymentsRetrieve,
        PermissionsEnum.AdvancePaymentsUpdate,
        PermissionsEnum.PaymentsToEcRetrieve,
        PermissionsEnum.PaymentsToEcUpdate,
        PermissionsEnum.PaymentsAccountRetrieve,
        PermissionsEnum.PaymentsAccountUpdate,
        PermissionsEnum.PaymentsAuditRetrieve,
        PermissionsEnum.PaymentsAuditUpdate
      ],
    },
    children: [
      {
        path: '',
        component: PaymentsPageComponent,
        canActivate: [PermissionGuard],
        data: {
          permissionsOnly: [
            PermissionsEnum.PaymentsRetrieve,
            PermissionsEnum.PaymentsUpdate,
            PermissionsEnum.AdvancePaymentsRetrieve,
            PermissionsEnum.AdvancePaymentsUpdate,
            PermissionsEnum.PaymentsToEcRetrieve,
            PermissionsEnum.PaymentsToEcUpdate,
            PermissionsEnum.PaymentsAccountRetrieve,
            PermissionsEnum.PaymentsAccountUpdate,
            PermissionsEnum.PaymentsAuditRetrieve,
            PermissionsEnum.PaymentsAuditUpdate,
          ],
        },
      },
      {
        path: 'paymentsToProjects',
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'payments.projects.header',
          permissionsOnly: [
            PermissionsEnum.PaymentsRetrieve,
            PermissionsEnum.PaymentsUpdate,
          ],
        },
        component: PaymentsToProjectPageComponent,
      },
      {
        path: 'advancePayments',
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'advance.payments.breadcrumb',
          permissionsOnly: [
            PermissionsEnum.AdvancePaymentsRetrieve,
            PermissionsEnum.AdvancePaymentsUpdate,
          ],
        },
        component: AdvancePaymentsPageComponent,
      },
      {
        path: 'paymentApplicationsToEc',
        canActivate: [PermissionGuard],
        component: PaymentsToEcPageComponent,
        data: {
          breadcrumb: 'payments.to.ec.breadcrumb',
          permissionsOnly: [
            PermissionsEnum.PaymentsToEcRetrieve,
            PermissionsEnum.PaymentsToEcUpdate,
          ],
        }
      },
      {
        path: 'paymentsToProjects/:paymentId',
        canActivate: [PermissionGuard],
        component: PaymentsToProjectDetailPageComponent,
        data: {
          dynamicBreadcrumb: true,
          permissionsOnly: [
            PermissionsEnum.PaymentsRetrieve,
            PermissionsEnum.PaymentsUpdate,
          ],
        },
        resolve: {
          breadcrumb$: PaymentsToProjectDetailBreadcrumbResolver,
        },
      },
      {
        path: 'advancePayments/create',
        canActivate: [PermissionGuard],
        component: AdvancePaymentsDetailPageComponent,
        data: {
          breadcrumb: 'advance.payments.breadcrumb',
          permissionsOnly: [
            PermissionsEnum.AdvancePaymentsRetrieve,
            PermissionsEnum.AdvancePaymentsUpdate,
          ],
        },
      },
      {
        path: 'advancePayments/:advancePaymentId',
        canActivate: [PermissionGuard],
        component: AdvancePaymentsDetailPageComponent,
        data: {
          breadcrumb: 'advance.payments.breadcrumb',
          permissionsOnly: [
            PermissionsEnum.AdvancePaymentsRetrieve,
            PermissionsEnum.AdvancePaymentsUpdate,
          ],
        },
      },
      {
        path: 'paymentApplicationsToEc/create',
        component: PaymentToEcDetailPageComponent,
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'payments.to.ec.breadcrumb',
          permissionsOnly: [
            PermissionsEnum.PaymentsToEcRetrieve,
            PermissionsEnum.PaymentsToEcUpdate,
          ],
        },
        children: [
          {
            path: '',
            redirectTo: 'summary',
          },
          {
            path: 'summary',
            component: PaymentToEcSummaryTabComponent,
          },
        ]
      },
      {
        path: 'paymentApplicationsToEc/:paymentToEcId',
        component: PaymentToEcDetailPageComponent,
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'payments.to.ec.breadcrumb',
          permissionsOnly: [
            PermissionsEnum.PaymentsToEcRetrieve,
            PermissionsEnum.PaymentsToEcUpdate,
          ],
        },
        children: [
          {
            path: '',
            redirectTo: 'summary',
          },
          {
            path: 'summary',
            component: PaymentToEcSummaryTabComponent,
          },
          {
            path: 'regular',
            component: PaymentToEcRegularProjectsTabComponent,
          },
          {
            path: 'flaggedArt9495',
            component: ProjectsFlaggedArt9495TabComponent,
          },
          {
            path: 'corrections',
            component: PaymentToEcCorrectionTabComponent,
          },
          {
            path: 'finalize',
            component: PaymentToEcFinalizeTabComponent,
          },
        ]
      },
      {
        path: 'accounts',
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'payments.accounts.breadcrumb',
          permissionsOnly: [
            PermissionsEnum.PaymentsAccountRetrieve,
            PermissionsEnum.PaymentsAccountUpdate,
          ],
        },
        children: [
          {
            path: '',
            component: AccountsPageComponent
          },
          {
            path: ':id',
            component: AccountDetailComponent,
            children: [
              {
                path: '',
                redirectTo: 'summary',
              },
              {
                path: 'summary',
                component: AccountsSummaryTabComponent,
              },
              {
                path: 'corrections',
                component: AccountsCorrectionTabComponent,
              },
              {
                path: 'withdrawn',
                component: AccountsWithdrawnTabComponent,
              },
              {
                path: 'reconciliation',
                component: AccountsReconciliationTabComponent,
              },
              {
                path: 'finalize',
                component: AccountsFinalizeTabComponent,
              },
            ],
          }
        ]
      },
      {
        path: 'audit',
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'payments.audit.breadcrumb',
          permissionsOnly: [
            PermissionsEnum.PaymentsAuditRetrieve,
            PermissionsEnum.PaymentsAuditUpdate,
          ],
        },
        component: PaymentsAuditPageComponent,
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(paymentsRoutes)],
  exports: [RouterModule]
})
export class PaymentsRoutingModule {
  constructor(private confirmLeaveGuard: ConfirmLeaveGuard) {
    this.confirmLeaveGuard.applyGuardToLeafRoutes(paymentsRoutes);
  }
}
