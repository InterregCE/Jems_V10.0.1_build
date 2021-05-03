import {PermissionNode} from './permission-node';
import {UserRoleCreateDTO} from '@cat/api';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

export class Permission {
  public static readonly ADMINISTRATOR = 'administrator';
  public static readonly PROGRAMME_USER = 'programme user';
  public static readonly APPLICANT_USER = 'applicant user';

  public static readonly DEFAULT_PERMISSIONS: PermissionNode[] = [
    {
      name: 'topbar.main.dashboard',
      viewPermissions: [],
      editPermissions: []
    },
    {
      name: 'project.application.form.lifecycle.title',
      children: [
        {
          name: 'project.detail.button.submit',
          oneClickToggle: [PermissionsEnum.ProjectSubmission],
        },
      ],
    },
    {
      name: 'topbar.main.system',
      children: [
        {
          name: 'topbar.main.audit',
          oneClickToggle: ['AuditRetrieve' as PermissionsEnum],
        },
        {
          name: 'topbar.main.user.management',
          viewPermissions: [PermissionsEnum.UserRetrieve],
          editPermissions: [
            PermissionsEnum.UserCreate,
            PermissionsEnum.UserUpdate,
            PermissionsEnum.UserUpdateRole,
            PermissionsEnum.UserUpdatePassword
          ]
        },
        {
          name: 'topbar.main.user.management',
          children: [
            {
              name: 'topbar.main.user.management',
              viewPermissions: [PermissionsEnum.UserRetrieve],
              editPermissions: [
                PermissionsEnum.UserCreate,
                PermissionsEnum.UserUpdate,
              ]
            },
            {
              name: 'user.detail.changeRole.dialog.title',
              oneClickToggle: [PermissionsEnum.UserUpdateRole],
            },
            {
              name: 'user.detail.changePassword.dialog.title',
              oneClickToggle: [PermissionsEnum.UserUpdatePassword],
            },
          ],
        },
        {
          name: 'topbar.main.userRole.management',
          viewPermissions: [PermissionsEnum.RoleRetrieve],
          editPermissions: [
            PermissionsEnum.RoleCreate,
            PermissionsEnum.RoleUpdate
          ],
        },
      ]
    }
  ];
}
