import {PermissionMode, PermissionNode, PermissionState} from './permission-node';
import {UserRoleCreateDTO} from '@cat/api';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

export class Permission {
  public static readonly PROJECT_APPLICATION_FORM_TITLE_NAME = 'project.application.form.title';

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
    PermissionsEnum.ProgrammeDataExportRetrieve
  ];

  public static readonly CONTROLLERS_PERMISSIONS = [
    PermissionsEnum.InstitutionsRetrieve,
    PermissionsEnum.InstitutionsUpdate,
    PermissionsEnum.InstitutionsUnlimited
  ];

  public static readonly CONTROLLERS_ASSIGNMENT_PERMISSIONS = [
    PermissionsEnum.InstitutionsAssignmentRetrieve,
    PermissionsEnum.InstitutionsAssignmentUpdate,
  ];

  public static readonly PAYMENTS_PERMISSIONS = [
    PermissionsEnum.PaymentsRetrieve,
    PermissionsEnum.PaymentsUpdate,
    PermissionsEnum.AdvancePaymentsRetrieve,
    PermissionsEnum.AdvancePaymentsUpdate
  ];

  public static readonly MONITORING_PERMISSIONS = [
    PermissionsEnum.ProjectFormRetrieve,
    PermissionsEnum.ProjectFileApplicationRetrieve,
    PermissionsEnum.ProjectCheckApplicationForm,
    PermissionsEnum.ProjectAssessmentView,
    PermissionsEnum.ProjectStatusDecisionRevert,
    PermissionsEnum.ProjectStatusReturnToApplicant,
    PermissionsEnum.ProjectStartStepTwo,
    PermissionsEnum.ProjectFileAssessmentRetrieve,
    PermissionsEnum.ProjectContractingView,
    PermissionsEnum.ProjectSetToContracted,
    PermissionsEnum.ProjectReportingView,
    PermissionsEnum.ProjectReportingEdit,
    PermissionsEnum.ProjectModificationView,
    PermissionsEnum.ProjectOpenModification,
    PermissionsEnum.ProjectModificationFileAssessmentRetrieve
  ];

  public static readonly DEFAULT_USER_CREATE_AND_COLLABORATE_PERMISSIONS: PermissionNode[] = [
    {
      name: 'project.application.reporting.title',
      children: [
        {
          name: 'project.application.project.report.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.EDIT,
          editTooltip: 'permission.create.project.reporting'
        },
        {
          name: 'project.application.project.reports.title.create.report',
          mode: PermissionMode.TOGGLE_EDIT,
          editPermissions: [PermissionsEnum.ProjectCreatorReportingProjectCreate],
          infoMessage: 'permission.inspect.reporting.project.create'
        },
        {
          name: 'project.application.partner.reports.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.EDIT,
          editTooltip: 'permission.create.reporting'
        }
      ]
    },
    {
      name: 'project.application.contracting.title',
      children: [
        {
          name: 'project.application.contract.monitoring.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.HIDDEN,
          hideTooltip: 'permission.create.contracting'
        },
        {
          name: 'project.application.contract.contracts.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.EDIT,
          editTooltip: 'permission.inspect.contracting.contracts'
        },
        {
          name: 'project.application.contract.management.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.EDIT,
          editTooltip: 'permission.inspect.contracting.management'
        },
        {
          name: 'project.application.contract.reporting.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectCreatorContractingReportingView],
          editPermissions: [PermissionsEnum.ProjectCreatorContractingReportingEdit],
          disabled: false,
          editTooltip: 'permission.inspect.contracting.reporting'
        },
        {
          name: 'project.application.contract.partner.section.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.EDIT,
          children: [
            {
              name: 'project.application.contract.partner.section.title',
              mode: PermissionMode.HIDDEN_VIEW_EDIT,
              viewPermissions: [],
              editPermissions: [],
              disabled: true,
              state: PermissionState.EDIT,
              editTooltip: 'permission.inspect.contracting.partner.section.applicant'
            },
            {
              name: 'project.application.contract.partner.state.aid.section.title',
              mode: PermissionMode.HIDDEN_VIEW_EDIT,
              viewPermissions: [],
              editPermissions: [],
              disabled: true,
              state: PermissionState.VIEW,
            }
          ]
        }
      ]
    },
    {
      name: Permission.PROJECT_APPLICATION_FORM_TITLE_NAME,
      children: [
        {
          name: Permission.PROJECT_APPLICATION_FORM_TITLE_NAME,
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
        {
          name: 'project.modification.header',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [],
          editPermissions: [],
          disabled: true,
          state: PermissionState.HIDDEN,
          hideTooltip: 'permission.create.modification'
        },
        {
          name: 'project.privileges.header',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectCreatorCollaboratorsRetrieve],
          editPermissions: [PermissionsEnum.ProjectCreatorCollaboratorsUpdate],
        },
        {
          name: 'project.application.form.section.shared.folder',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectCreatorSharedFolderView],
          editPermissions: [PermissionsEnum.ProjectCreatorSharedFolderEdit],
        }
      ]
    }
  ];

  public static readonly DEFAULT_USER_INSPECT_PERMISSIONS: PermissionNode[] = [
    {
      name: 'project.application.reporting.title',
      children: [
        {
          name: 'project.application.project.report.title',
          children: [
            {
              name: 'project.application.project.report.title',
              mode: PermissionMode.HIDDEN_VIEW_EDIT,
              viewPermissions: [PermissionsEnum.ProjectReportingProjectView],
              editPermissions: [PermissionsEnum.ProjectReportingProjectEdit],
            },
            {
              name: 'project.application.project.verification.work.title',
              mode: PermissionMode.HIDDEN_VIEW_EDIT,
              viewPermissions: [PermissionsEnum.ProjectReportingVerificationProjectView],
              editPermissions: [PermissionsEnum.ProjectReportingVerificationProjectEdit],
              editTooltip: 'permission.inspect.reporting.project.verify'
            },
            {
              name: 'project.application.project.verification.work.finalize.title',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: [PermissionsEnum.ProjectReportingVerificationFinalize],
              infoMessage: 'permission.inspect.reporting.project.finalize',
            },
          ]
        },
        {
          name: 'project.application.partner.reports.title',
          children: [
            {
              name: 'project.application.partner.reports.title',
              mode: PermissionMode.HIDDEN_VIEW_EDIT,
              viewPermissions: [PermissionsEnum.ProjectReportingView],
              editPermissions: [PermissionsEnum.ProjectReportingEdit],
              editTooltip: 'permission.inspect.reporting'
            },
            {
              name: 'project.application.partner.reports.reopen',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: [PermissionsEnum.ProjectReportingReOpen]
            },
            {
              name: 'project.application.partner.reports.checklist.after.control',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: [PermissionsEnum.ProjectReportingChecklistAfterControl],
              infoMessage: 'project.application.partner.reports.checklist.after.control.tooltip'
            },
            {
              name: 'project.application.partner.reports.control.reopen',
              mode: PermissionMode.TOGGLE_EDIT,
              editPermissions: [PermissionsEnum.ProjectPartnerControlReportingReOpen]
            }
          ]
        }
      ]
    },
    {
      name: 'project.application.contracting.title',
      children: [
        {
          name: 'project.application.contract.monitoring.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectContractingView],
          editPermissions: [PermissionsEnum.ProjectSetToContracted],
          editTooltip: 'permission.inspect.contracting'
        },
        {
          name: 'project.application.contract.contracts.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectContractsView],
          editPermissions: [PermissionsEnum.ProjectContractsEdit],
          editTooltip: 'permission.inspect.contracting.contracts'
        },
        {
          name: 'project.application.contract.management.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectContractingManagementView],
          editPermissions: [PermissionsEnum.ProjectContractingManagementEdit],
          editTooltip: 'permission.inspect.contracting.management'
        },
        {
          name: 'project.application.contract.reporting.title',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectContractingReportingView],
          editPermissions: [PermissionsEnum.ProjectContractingReportingEdit],
          editTooltip: 'permission.inspect.contracting.reporting'
        },
        {
          name: 'project.application.contract.partner.section.title',
          children: [
              {
                name: 'project.application.contract.partner.section.title',
                mode: PermissionMode.HIDDEN_VIEW_EDIT,
                viewPermissions: [PermissionsEnum.ProjectContractingPartnerView],
                editPermissions: [PermissionsEnum.ProjectContractingPartnerEdit],
                editTooltip: 'permission.inspect.contracting.partner.section',
              },
              {
                  name: 'project.application.contract.partner.state.aid.section.title',
                  mode: PermissionMode.HIDDEN_VIEW_EDIT,
                  viewPermissions: [],
                  editPermissions: [],
                  disabled: true,
                  state: PermissionState.EDIT,
              }
          ]
        }
      ]
    },
    {
      name: Permission.PROJECT_APPLICATION_FORM_TITLE_NAME,
      children: [
        {
          name: Permission.PROJECT_APPLICATION_FORM_TITLE_NAME,
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
            {
              name: 'project.application.form.section.assessment.and.decision.checklists',
              children: [
                {
                  name: 'permission.assessment.instantiate',
                  mode: PermissionMode.TOGGLE_EDIT,
                  editPermissions: [PermissionsEnum.ProjectAssessmentChecklistUpdate],
                },
                {
                  name: 'permission.assessment.consolidate',
                  mode: PermissionMode.TOGGLE_EDIT,
                  editPermissions: [PermissionsEnum.ProjectAssessmentChecklistConsolidate],
                  infoMessage: 'permission.assessment.consolidate.info'
                },
                {
                  name: 'permission.assessment.selection',
                  mode: PermissionMode.HIDDEN_VIEW_EDIT,
                  viewPermissions: [PermissionsEnum.ProjectAssessmentChecklistSelectedRetrieve],
                  editPermissions: [PermissionsEnum.ProjectAssessmentChecklistSelectedUpdate]
                }
              ]
            }
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
        {
          name: 'project.privileges.header',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectMonitorCollaboratorsRetrieve],
          editPermissions: [PermissionsEnum.ProjectMonitorCollaboratorsUpdate],
        },
        {
          name: 'project.application.form.section.shared.folder',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProjectMonitorSharedFolderView],
          editPermissions: [PermissionsEnum.ProjectMonitorSharedFolderEdit],
        }
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
          name: 'call.detail.notifications.config.title',
          mode: PermissionMode.HIDDEN_VIEW,
          viewPermissions: [
            PermissionsEnum.NotificationsRetrieve,
          ],
        },
        {
          name: 'user.partner.reports',
          mode: PermissionMode.HIDDEN_VIEW,
          viewPermissions: [
            PermissionsEnum.PartnerReportsRetrieve,
          ],
          viewTooltip: 'permission.top.bar.partner.reports'
        },
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
        }
      ],
    },
    {
      name: 'topbar.main.payments',
      mode: PermissionMode.HIDDEN_VIEW,
      icon: 'payments',
      children: [
        {
          name: 'permission.payments.projects',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.PaymentsRetrieve],
          editPermissions: [PermissionsEnum.PaymentsUpdate],
          viewTooltip: 'permission.payments.view.tooltip',
          editTooltip: 'permission.payments.edit.tooltip',
        },
        {
          name: 'permission.advance.payments',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.AdvancePaymentsRetrieve],
          editPermissions: [PermissionsEnum.AdvancePaymentsUpdate],
          hideTooltip: 'permission.advance.payments.hide.tooltip',
          viewTooltip: 'permission.advance.payments.view.tooltip',
          editTooltip: 'permission.advance.payments.edit.tooltip',
        },
      ],
    },
    {
      name: 'topbar.main.project',
      mode: PermissionMode.HIDDEN_VIEW_EDIT,
      viewPermissions: [PermissionsEnum.ProjectRetrieve],
      editPermissions: [PermissionsEnum.ProjectRetrieveEditUserAssignments],
      icon: 'description',
      viewTooltip: 'permission.top.bar.applications.view',
      editTooltip: 'permission.top.bar.applications.edit'
    },
    {
      name: 'topbar.main.call',
      mode: PermissionMode.HIDDEN_VIEW_EDIT,
      viewPermissions: [PermissionsEnum.CallRetrieve],
      editPermissions: [PermissionsEnum.CallUpdate],
      icon: 'campaign',
      editTooltip: 'permission.top.bar.calls'
    },
    {
      name: 'topbar.main.programme',
      icon: 'business',
      children: [
        {
          name: 'topbar.main.programme.setup',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.ProgrammeSetupRetrieve],
          editPermissions: [PermissionsEnum.ProgrammeSetupUpdate],
          editTooltip: 'permission.top.bar.programme.data',
        },
        {
          name: 'topbar.main.programme.data.export',
          mode: PermissionMode.HIDDEN_VIEW,
          viewPermissions: [PermissionsEnum.ProgrammeDataExportRetrieve],
        },
      ]
    },
    {
      name: 'topbar.main.controllers',
      icon: 'rule_folder',
      children: [
        {
          name: 'topbar.main.institutions',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.InstitutionsRetrieve],
          editPermissions: [PermissionsEnum.InstitutionsUpdate],
          editTooltip: 'permission.top.bar.institutions.data.edit',
        },
        {
          name: 'permission.top.bar.institutions.all.toggle',
          mode: PermissionMode.TOGGLE_EDIT,
          editPermissions: [PermissionsEnum.InstitutionsUnlimited],
          infoMessage: 'permission.top.bar.institutions.all.info'
        },
        {
          name: 'topbar.main.institutions.assignment',
          mode: PermissionMode.HIDDEN_VIEW_EDIT,
          viewPermissions: [PermissionsEnum.InstitutionsAssignmentRetrieve],
          editPermissions: [PermissionsEnum.InstitutionsAssignmentUpdate],
          editTooltip: 'permission.top.bar.institutions.assignment.data',
        },
        {
          name: 'permission.top.bar.assignments.all.toggle',
          mode: PermissionMode.TOGGLE_EDIT,
          editPermissions: [PermissionsEnum.AssignmentsUnlimited],
          infoMessage: 'permission.top.bar.assignments.all.info'
        }
      ]
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
