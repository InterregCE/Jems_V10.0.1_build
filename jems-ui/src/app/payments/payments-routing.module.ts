import {Routes} from '@angular/router';
import {UserRoleCreateDTO} from '@cat/api';
import {PaymentsPageComponent} from './payments-page/payments-page.component';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

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
    component: PaymentsPageComponent,
    children: []
  }
];
