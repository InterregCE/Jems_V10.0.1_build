import {Routes} from '@angular/router';
import {UserRoleCreateDTO} from '@cat/api';
import {PaymentsPageComponent} from './payments-page/payments-page.component';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {
  PaymentsToProjectDetailPageComponent
} from './payments-to-projects-page/payments-to-project-detail-page/payments-to-project-detail-page.component';
import {
  PaymentsToProjectDetailBreadcrumbResolver
} from './payments-to-projects-page/payments-to-project-detail.resolver';
import {
  AdvancePaymentsDetailPageComponent
} from './advance-payments-page/advance-payments-detail-page/advance-payments-detail-page.component';
import {PaymentsToEcPageComponent} from './payments-to-ec/payments-to-ec-page.component';
import {AdvancePaymentsPageComponent} from './advance-payments-page/advance-payments-page.component';
import {PaymentsToProjectPageComponent} from './payments-to-projects-page/payments-to-project-page.component';
import {
  PaymentToEcDetailPageComponent
} from './payments-to-ec/payments-to-ec-detail-page/payment-to-ec-detail-page.component';
import {PermissionGuard} from '../security/permission.guard';

export const paymentsRoutes: Routes = [
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
        },
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
      },
    ]
  }
];
