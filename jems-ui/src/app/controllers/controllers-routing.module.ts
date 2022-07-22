import {Routes} from '@angular/router';
import {InstitutionsPageComponent} from './institutions-page/institutions-page.component';
import {PermissionGuard} from '../security/permission.guard';
import {UserRoleCreateDTO} from '@cat/api';
import {
  ControllerInstitutionDetailComponent
} from './institutions-page/controller-institution-detail/controller-institution-detail.component';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

export const routes: Routes = [
  {
    path: '',
    data: {
      breadcrumb: 'controllers.breadcrumb',
      permissionsOnly: [
        PermissionsEnum.InstitutionsRetrieve,
        PermissionsEnum.InstitutionsUpdate,
        PermissionsEnum.InstitutionsUnlimited
      ],
    },
    children: [
      {
        path: '',
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'controllers.institution.breadcrumb',
          permissionsOnly: [
            PermissionsEnum.InstitutionsRetrieve,
            PermissionsEnum.InstitutionsUpdate,
            //PermissionsEnum.InstitutionsLimited
          ],
        },
        component: InstitutionsPageComponent,
      },
      {
        path: 'create',
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'controller.breadcrumb.create',
          permissionsOnly: [PermissionsEnum.InstitutionsUpdate],
        },
        component: ControllerInstitutionDetailComponent
      },
      {
        path: ':controllerInstitutionId',
        component: ControllerInstitutionDetailComponent,
        data: {
          breadcrumb: 'controller.breadcrumb.update',
          permissionsOnly: [PermissionsEnum.InstitutionsUpdate],
        },
        // data: {dynamicBreadcrumb: true, permissionsOnly: [PermissionsEnum.InstitutionsUpdate]},
        // resolve: {breadcrumb$: PartnerBreadcrumbResolver},
        children: [
          {
            path: '',
            component: ControllerInstitutionDetailComponent,
          },
        ]
      }
    ]
  }
];
