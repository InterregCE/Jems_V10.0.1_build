import {Routes} from '@angular/router';
import {InstitutionsPageComponent} from './institutions-page/institutions-page.component';
import {PermissionGuard} from '../security/permission.guard';
import {UserRoleCreateDTO} from '@cat/api';
import {
  ControllerInstitutionDetailComponent
} from './institutions-page/controller-institution-detail/controller-institution-detail.component';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {InstitutionsAssignmentsPageComponent} from "./institution-assignments-page/institutions-assignments-page.component";

export const routes: Routes = [
  {
    path: '',
    data: {
      breadcrumb: 'controllers.breadcrumb',
      permissionsOnly: [
        PermissionsEnum.InstitutionsRetrieve,
        PermissionsEnum.InstitutionsUpdate,
        PermissionsEnum.InstitutionsUnlimited,
        PermissionsEnum.InstitutionsAssignmentRetrieve
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
            PermissionsEnum.InstitutionsAssignmentRetrieve
          ],
        },
        component: InstitutionsPageComponent,
      },
      {
        path: 'assignment',
        component: InstitutionsAssignmentsPageComponent,
        data: {
          breadcrumb: 'topbar.main.institutions.assignment',
          permissionsOnly: [PermissionsEnum.InstitutionsAssignmentRetrieve],
        },
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
