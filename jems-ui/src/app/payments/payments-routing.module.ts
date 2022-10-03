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

export const paymentsRoutes: Routes = [
  {
    path: '',
    data: {
      breadcrumb: 'payments.breadcrumb',
      permissionsOnly: [
        PermissionsEnum.PaymentsRetrieve,
        PermissionsEnum.PaymentsUpdate,
      ],
    },
    children: [
      {
        path: '',
        component: PaymentsPageComponent,
      },
      {
        path: ':paymentId',
        component: PaymentsToProjectDetailPageComponent,
        data: {dynamicBreadcrumb: true},
        resolve: {breadcrumb$: PaymentsToProjectDetailBreadcrumbResolver},
      }
    ]
  }
];
