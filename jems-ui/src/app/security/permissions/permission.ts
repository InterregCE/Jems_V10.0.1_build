import {PermissionMode, PermissionNode} from './permission-node';
import {UserRoleCreateDTO} from '@cat/api';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

export class Permission {
  public static readonly ADMINISTRATOR = 'administrator';
  public static readonly PROGRAMME_USER = 'programme user';
  public static readonly APPLICANT_USER = 'applicant user';

  public static readonly SYSTEM_MODULE_PERMISSIONS = [
    PermissionsEnum.AuditRetrieve,

    PermissionsEnum.RoleRetrieve,
    PermissionsEnum.RoleCreate,
    PermissionsEnum.RoleUpdate,

    PermissionsEnum.UserRetrieve,
    PermissionsEnum.UserCreate,
    PermissionsEnum.UserUpdate,
    PermissionsEnum.UserUpdateRole,
    PermissionsEnum.UserUpdatePassword,
  ];

  public static readonly PROGRAMME_SETUP_MODULE_PERMISSIONS = [
    PermissionsEnum.ProgrammeSetupRetrieve,
    PermissionsEnum.ProgrammeSetupUpdate,
  ];

  public static readonly DEFAULT_PERMISSIONS: PermissionNode[] = [
    {
      name: 'topbar.main.dashboard',
      mode: PermissionMode.HIDDEN_VIEW,
      viewPermissions: ['Dashboard-to-be-done' as PermissionsEnum],
      temporarilyDisabled: true,
      children: [
        {
          name: 'call.applications.title',
          mode: PermissionMode.HIDDEN_VIEW,
          viewPermissions: [
            PermissionsEnum.ProjectsWithOwnershipRetrieve,
          ],
        },
        {
          name: 'call.list.open.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [
            PermissionsEnum.CallPublishedRetrieve,
          ],
          editPermissions: [
            PermissionsEnum.ProjectCreate,
          ],
        },
      ],
    },
    {
      name: 'topbar.main.project',
      mode: PermissionMode.HIDDEN_VIEW,
      viewPermissions: [PermissionsEnum.ProjectRetrieve],
    },
    {
      name: 'Calls',
      mode: PermissionMode.HIDDEN_VIEW_EDIT,
      viewPermissions: [PermissionsEnum.CallRetrieve],
      editPermissions: [PermissionsEnum.CallUpdate],
    },
    {
      name: 'topbar.main.programme',
      mode: PermissionMode.HIDDEN_VIEW_EDIT,
      viewPermissions: [PermissionsEnum.ProgrammeSetupRetrieve],
      editPermissions: [PermissionsEnum.ProgrammeSetupUpdate],
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
          ],
        },
        {
          name: 'topbar.main.audit',
          mode: PermissionMode.HIDDEN_VIEW,
          viewPermissions: [PermissionsEnum.AuditRetrieve],
        },
      ]
    },
    {
      name: 'project.application.form.title',
      children: [
        {
          name: 'project.application.form.lifecycle.title',
          children: [
            {
              name: 'Check AF',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: ['Check AF-to-be-done' as PermissionsEnum],
              temporarilyDisabled: true,
            },
            {
              name: 'project.detail.button.submit',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: [PermissionsEnum.ProjectSubmission],
            },
            {
              name: 'project.detail.button.return.applicant',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: ['ProjectReturn-to-be-done' as PermissionsEnum],
              temporarilyDisabled: true,
            },
            {
              name: 'Revert decision',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: ['Revert decision-to-be-done' as PermissionsEnum],
              temporarilyDisabled: true,
            },
          ],
        },
        {
          name: 'project.assessment.header',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [
            PermissionsEnum.ProjectAssessmentView,
          ],
          editPermissions: [
            PermissionsEnum.ProjectAssessmentQualityEnter,
            PermissionsEnum.ProjectAssessmentEligibilityEnter,
            PermissionsEnum.ProjectStatusDecideEligible,
            PermissionsEnum.ProjectStatusDecideIneligible,
            PermissionsEnum.ProjectStatusDecideApproved,
            PermissionsEnum.ProjectStatusDecideApprovedWithConditions,
            PermissionsEnum.ProjectStatusDecideNotApproved,
          ],
        },
        {
          name: 'file.tab.header',
          children: [
            {
              name: 'file.tab.application',
              mode: PermissionMode.HIDDEN_VIEW_EDIT,
              viewPermissions: ['File application view-to-be-done' as PermissionsEnum],
              editPermissions: ['File application edit-to-be-done' as PermissionsEnum],
              temporarilyDisabled: true,
            },
            {
              name: 'file.tab.assessment',
              mode: PermissionMode.HIDDEN_VIEW_EDIT,
              viewPermissions: ['File assessment view-to-be-done' as PermissionsEnum],
              editPermissions: ['File assessment edit-to-be-done' as PermissionsEnum],
              temporarilyDisabled: true,
            },
          ],
        },
      ]
    },
  ];
}
