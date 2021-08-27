import {PermissionMode, PermissionNode, PermissionState} from './permission-node';
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
  ];

  public static readonly PROGRAMME_SETUP_MODULE_PERMISSIONS = [
    PermissionsEnum.ProgrammeSetupRetrieve,
    PermissionsEnum.ProgrammeSetupUpdate,
  ];

  public static readonly DEFAULT_USER_CREATE_AND_COLLABORATE_PERMISSIONS: PermissionNode[] = [
    {
      name: 'project.application.form.title',
      children: [
        {
          name: 'project.application.form.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.EDIT
        },
        {
          name: 'file.tab.application',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.EDIT
        },
        {
          name: 'project.application.check.submit',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.EDIT
        },
        {
          name: 'project.assessment.and.decision.header',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.HIDDEN
        },
      ]
    }
  ];

  public static readonly DEFAULT_USER_INSPECT_PERMISSIONS: PermissionNode[] = [
    {
      name: 'project.application.form.title',
      children: [
        {
          name: 'project.application.form.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectFormRetrieve],
          editPermissions: [PermissionsEnum.ProjectFormUpdate],
        },
        {
          name: 'file.tab.application',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectFileApplicationRetrieve],
          editPermissions: [PermissionsEnum.ProjectFileApplicationUpdate],
        },
        {
          name: 'project.application.check.submit',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectCheckApplicationForm],
          editPermissions: [PermissionsEnum.ProjectSubmission],
          viewTooltip: 'project.application.check.submit.view.tooltip',
          editTooltip: 'project.application.check.submit.edit.tooltip'
        },
        {
          name: 'project.assessment.and.decision.header',
          children: [
            {
              name: 'project.assessment.and.decision.panel',
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
              name: 'project.application.revert.status.dialog.title',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: [PermissionsEnum.ProjectStatusDecisionRevert],
            },
            {
              name: 'project.detail.button.return.applicant',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: [PermissionsEnum.ProjectStatusReturnToApplicant],
            },
            {
              name: 'project.application.start.step.two.button.label',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: [PermissionsEnum.ProjectStartStepTwo],
            },
            {
              name: 'file.tab.assessment',
              mode: PermissionMode.HIDDEN_VIEW_EDIT,
              viewPermissions: [PermissionsEnum.ProjectFileAssessmentRetrieve],
              editPermissions: [PermissionsEnum.ProjectFileAssessmentUpdate],
            },
          ],
        },
      ]
    }
  ];

  public static readonly TOP_NAVIGATION_PERMISSIONS: PermissionNode[] = [
    {
      name: 'topbar.main.dashboard',
      mode: PermissionMode.HIDDEN_VIEW,
      viewPermissions: ['Dashboard-to-be-done' as PermissionsEnum],
      disabled: true,
      icon: 'dashboard',
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
          mode: PermissionMode.HIDDEN_VIEW,
          viewPermissions: [
            PermissionsEnum.CallPublishedRetrieve,
          ],
          viewTooltip: 'call.list.open.title.view.tooltip'
        },
      ],
    },
    {
      name: 'topbar.main.project',
      mode: PermissionMode.HIDDEN_VIEW,
      viewPermissions: [PermissionsEnum.ProjectRetrieve],
      icon: 'description',
    },
    {
      name: 'Calls',
      mode: PermissionMode.HIDDEN_VIEW_EDIT,
      viewPermissions: [PermissionsEnum.CallRetrieve],
      editPermissions: [PermissionsEnum.CallUpdate],
      icon: 'campaign',
    },
    {
      name: 'topbar.main.programme',
      mode: PermissionMode.HIDDEN_VIEW_EDIT,
      viewPermissions: [PermissionsEnum.ProgrammeSetupRetrieve],
      editPermissions: [PermissionsEnum.ProgrammeSetupUpdate],
      icon: 'business',
    },
    {
      name: 'topbar.main.system',
      icon: 'settings',
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
    }
  ];
}
