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
    PermissionsEnum.UserUpdatePassword,
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
          state: PermissionState.EDIT,
          editTooltip: 'permission.create.application.form'
        },
        {
          name: 'file.tab.application',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.EDIT,
          editTooltip: 'permission.create.application.annexes'
        },
        {
          name: 'project.application.check.submit',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.EDIT,
          editTooltip: 'permission.create.check.and.submit'
        },
        {
          name: 'project.assessment.and.decision.header',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.HIDDEN,
          hideTooltip: 'permission.create.assessment.and.decision'
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
          editTooltip: 'permission.inspect.application.form'
        },
        {
          name: 'file.tab.application',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectFileApplicationRetrieve],
          editPermissions: [PermissionsEnum.ProjectFileApplicationUpdate],
          editTooltip: 'permission.inspect.application.annexes'
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
              editTooltip: 'permission.inspect.assessment.and.decision.panel'
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
              editTooltip: 'permission.inspect.annexes'
            },
          ],
        },
        {
          name: 'project.modification.header',
          children: [
            {
              name: 'project.modification.panel',
              mode: PermissionMode.HIDDEN_VIEW_EDIT,
              viewPermissions: [PermissionsEnum.ProjectModificationView],
              editPermissions: [
                PermissionsEnum.ProjectStatusDecideModificationApproved,
                PermissionsEnum.ProjectStatusDecideModificationNotApproved
              ],
              editTooltip: 'permission.inspect.modification.panel'
            },
            {
              name: 'project.application.form.section.modification.open.button',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: [PermissionsEnum.ProjectOpenModification],
            },
            {
              name: 'file.tab.modification',
              mode: PermissionMode.HIDDEN_VIEW_EDIT,
              viewPermissions: [PermissionsEnum.ProjectModificationFileAssessmentRetrieve],
              editPermissions: [PermissionsEnum.ProjectModificationFileAssessmentUpdate],
              editTooltip: 'permission.inspect.modification.files'
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
      mode: PermissionMode.HIDDEN_VIEW_EDIT,
      viewPermissions: [PermissionsEnum.ProjectRetrieve],
      editPermissions: [PermissionsEnum.ProjectRetrieveEditUserAssignments],
      icon: 'description',
      editTooltip: 'permission.top.bar.applications.edit'
    },
    {
      name: 'Calls',
      mode: PermissionMode.HIDDEN_VIEW_EDIT,
      viewPermissions: [PermissionsEnum.CallRetrieve],
      editPermissions: [PermissionsEnum.CallUpdate],
      icon: 'campaign',
      editTooltip: 'permission.top.bar.calls'
    },
    {
      name: 'topbar.main.programme',
      mode: PermissionMode.HIDDEN_VIEW_EDIT,
      viewPermissions: [PermissionsEnum.ProgrammeSetupRetrieve],
      editPermissions: [PermissionsEnum.ProgrammeSetupUpdate],
      icon: 'business',
      editTooltip: 'permission.top.bar.programme.data'
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
            PermissionsEnum.UserUpdatePassword,
            PermissionsEnum.RoleCreate,
            PermissionsEnum.RoleUpdate,
          ],
          editTooltip: 'permission.top.bar.user.management'
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
