import {PermissionMode, PermissionNode} from './permission-node';
import {UserRoleCreateDTO} from '@cat/api';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

export class Permission {
  public static readonly ADMINISTRATOR = 'administrator';
  public static readonly PROGRAMME_USER = 'programme user';
  public static readonly APPLICANT_USER = 'applicant user';

  public static readonly DEFAULT_PERMISSIONS: PermissionNode[] = [
    {
      name: 'topbar.main.dashboard',
      mode: PermissionMode.HIDDEN_VIEW_EDIT,
      viewPermissions: [],
      editPermissions: []
    },
    {
      name: 'project.application.form.title',
      children: [
        {
          name: 'project.application.form.lifecycle.title',
          children: [
            {
              name: 'project.detail.button.submit',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: [PermissionsEnum.ProjectSubmission],
            },
            {
              name: 'project.detail.button.return.applicant',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: ['ProjectReturn-to-be-done' as PermissionsEnum],
            },
          ],
        },
      ]
    },
    {
      name: 'topbar.main.system',
      children: [
        {
          name: 'topbar.main.user.management',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [
            PermissionsEnum.UserRetrieve,
            PermissionsEnum.RoleRetrieve,
          ],
          editPermissions: [
            PermissionsEnum.UserCreate,
            PermissionsEnum.UserUpdate,
            PermissionsEnum.UserUpdateRole,
            PermissionsEnum.UserUpdatePassword,
            PermissionsEnum.RoleCreate,
            PermissionsEnum.RoleUpdate,
          ]
        },
        {
          name: 'topbar.main.audit',
          mode: PermissionMode.HIDDEN_VIEW,
          viewPermissions: ['AuditRetrieve' as PermissionsEnum],
        },
      ]
    }
  ];
}
