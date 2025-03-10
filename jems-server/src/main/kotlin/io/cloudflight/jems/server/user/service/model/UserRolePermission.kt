package io.cloudflight.jems.server.user.service.model

enum class UserRolePermission(val key: String, val projectRelated: Boolean = false) {

    // region Creator
    ProjectCreate("ProjectCreate"),

    ProjectCreatorCollaboratorsRetrieve("ProjectCreatorCollaboratorsRetrieve", false),
    ProjectCreatorCollaboratorsUpdate("ProjectCreatorCollaboratorsUpdate", false),

    ProjectCreatorContractingReportingView("ProjectCreatorContractingReportingView"),
    ProjectCreatorContractingReportingEdit("ProjectCreatorContractingReportingEdit"),
    ProjectContractingPartnerStateAidView("ProjectContractingPartnerStateAidView"),
    ProjectContractingPartnerStateAidEdit("ProjectContractingPartnerStateAidEdit"),

    ProjectCreatorSharedFolderView("ProjectCreatorSharedFolderView"),
    ProjectCreatorSharedFolderEdit("ProjectCreatorSharedFolderEdit"),

    ProjectCreatorReportingProjectCreate("ProjectCreatorReportingProjectCreate"),
    // endregion Creator

    // region Monitor
    ProjectReportingView("ProjectReportingView", true),
    ProjectReportingEdit("ProjectReportingEdit", true),
    ProjectReportingReOpen("ProjectReportingReOpen", true),
    ProjectPartnerControlReportingReOpen("ProjectPartnerControlReportingReOpen", true),

    ProjectReportingProjectView("ProjectReportingProjectView", true),
    ProjectReportingProjectEdit("ProjectReportingProjectEdit", true),
    ProjectReportingProjectReOpen("ProjectReportingProjectReOpen", true),
    ProjectReportingVerificationProjectView("ProjectReportingVerificationProjectView", true),
    ProjectReportingVerificationProjectEdit("ProjectReportingVerificationProjectEdit", true),
    ProjectReportingVerificationFinalize("ProjectReportingVerificationFinalize", true),
    ProjectReportingVerificationReOpen("ProjectReportingVerificationReOpen", true),
    ProjectReportingChecklistAfterControl("ProjectReportingChecklistAfterControl", true),

    ProjectContractingPartnerView("ProjectContractingPartnerView", true),
    ProjectContractingPartnerEdit("ProjectContractingPartnerEdit", true),

    ProjectContractingView("ProjectContractingView", true),
    ProjectSetToContracted("ProjectSetToContracted", true),

    ProjectContractingManagementView("ProjectContractingManagementView", true),
    ProjectContractingManagementEdit("ProjectContractingManagementEdit", true),
    ProjectContractingReportingView("ProjectContractingReportingView", true),
    ProjectContractingReportingEdit("ProjectContractingReportingEdit", true),
    ProjectContractsView("ProjectContractsView", true),
    ProjectContractsEdit("ProjectContractsEdit", true),

    ProjectFormRetrieve("ProjectFormRetrieve", true),
    ProjectFormUpdate("ProjectFormUpdate", true),

    ProjectFileApplicationRetrieve("ProjectFileApplicationRetrieve", true),
    ProjectFileApplicationUpdate("ProjectFileApplicationUpdate", true),

    ProjectCheckApplicationForm("ProjectCheckApplicationForm", true),
    ProjectSubmission("ProjectSubmission", true),

    ProjectAssessmentView("ProjectAssessmentView", true),
    ProjectAssessmentQualityEnter("ProjectAssessmentQualityEnter", true),
    ProjectAssessmentEligibilityEnter("ProjectAssessmentEligibilityEnter", true),
    ProjectStatusDecideEligible("ProjectStatusDecideEligible", true),
    ProjectStatusDecideIneligible("ProjectStatusDecideIneligible", true),
    ProjectStatusDecideApproved("ProjectStatusDecideApproved", true),
    ProjectStatusDecideApprovedWithConditions("ProjectStatusDecideApprovedWithConditions", true),
    ProjectStatusDecideNotApproved("ProjectStatusDecideNotApproved", true),

    ProjectStatusDecisionRevert("ProjectStatusDecisionRevert", true),
    ProjectStatusReturnToApplicant("ProjectStatusReturnToApplicant", true),
    ProjectStartStepTwo("ProjectStartStepTwo", true),

    ProjectFileAssessmentRetrieve("ProjectFileAssessmentRetrieve", true),
    ProjectFileAssessmentUpdate("ProjectFileAssessmentUpdate", true),

    ProjectAssessmentChecklistUpdate("ProjectAssessmentChecklistUpdate", true),
    ProjectAssessmentChecklistConsolidate("ProjectAssessmentChecklistConsolidate", true),
    ProjectAssessmentChecklistSelectedRetrieve("ProjectAssessmentChecklistSelectedRetrieve", true),
    ProjectAssessmentChecklistSelectedUpdate("ProjectAssessmentChecklistSelectedUpdate", false),

    ProjectModificationView("ProjectModificationView", true),
    ProjectStatusDecideModificationApproved("ProjectStatusDecideModificationApproved", true),
    ProjectStatusDecideModificationNotApproved("ProjectStatusDecideModificationNotApproved", true),

    ProjectOpenModification("ProjectOpenModification", true),

    ProjectModificationFileAssessmentRetrieve("ProjectModificationFileAssessmentRetrieve", true),
    ProjectModificationFileAssessmentUpdate("ProjectModificationFileAssessmentUpdate", true),

    ProjectMonitorCollaboratorsRetrieve("ProjectMonitorCollaboratorsRetrieve", true),
    ProjectMonitorCollaboratorsUpdate("ProjectMonitorCollaboratorsUpdate", true),

    ProjectMonitorSharedFolderView("ProjectMonitorSharedFolderView", true),
    ProjectMonitorSharedFolderEdit("ProjectMonitorSharedFolderEdit", true),

    ProjectMonitorAuditAndControlView("ProjectMonitorAuditAndControlView", true),
    ProjectMonitorAuditAndControlEdit("ProjectMonitorAuditAndControlEdit", true),
    ProjectMonitorCloseAuditControl("ProjectMonitorCloseAuditControl", true),
    ProjectMonitorCloseAuditControlCorrection("ProjectMonitorCloseAuditControlCorrection", true),
    ProjectMonitorReOpenAuditControl("ProjectMonitorReOpenAuditControl", true),
    // endregion Monitor

    // region top navigation
    PartnerReportsRetrieve("PartnerReportsRetrieve"),
    ProjectReportsRetrieve("ProjectReportsRetrieve"),
    ProjectsWithOwnershipRetrieve("ProjectsWithOwnershipRetrieve"),
    CallPublishedRetrieve("CallPublishedRetrieve"),
    NotificationsRetrieve("NotificationsRetrieve"),

    ProjectRetrieve("ProjectRetrieve"),
    ProjectRetrieveEditUserAssignments("ProjectRetrieveEditUserAssignments"),

    CallRetrieve("CallRetrieve"),
    CallUpdate("CallUpdate"),

    ProgrammeSetupRetrieve("ProgrammeSetupRetrieve"),
    ProgrammeSetupUpdate("ProgrammeSetupUpdate"),

    InstitutionsRetrieve("InstitutionsRetrieve"),
    InstitutionsUpdate("InstitutionsUpdate"),
    InstitutionsUnlimited("InstitutionsUnlimited"),
    AssignmentsUnlimited("AssignmentsUnlimited"),

    InstitutionsAssignmentRetrieve("InstitutionsAssignmentRetrieve"),
    InstitutionsAssignmentUpdate("InstitutionsAssignmentUpdate"),

    PaymentsRetrieve("PaymentsRetrieve"),
    PaymentsUpdate("PaymentsUpdate"),
    AdvancePaymentsRetrieve("AdvancePaymentsRetrieve"),
    AdvancePaymentsUpdate("AdvancePaymentsUpdate"),
    PaymentsToEcRetrieve("PaymentsToEcRetrieve"),
    PaymentsToEcUpdate("PaymentsToEcUpdate"),
    PaymentsAuditRetrieve("PaymentsAuditRetrieve"),
    PaymentsAuditUpdate("PaymentsAuditUpdate"),
    PaymentsAccountRetrieve("PaymentsAccountRetrieve"),
    PaymentsAccountUpdate("PaymentsAccountUpdate"),

    ProgrammeDataExportRetrieve("ProgrammeDataExportRetrieve"),

    UserRetrieve("UserRetrieve"),
    RoleRetrieve("RoleRetrieve"),
    UserCreate("UserCreate"),
    UserUpdate("UserUpdate"),
    UserUpdateRole("UserUpdateRole"),
    UserUpdatePassword("UserUpdatePassword"),
    RoleCreate("RoleCreate"),
    RoleUpdate("RoleUpdate"),

    AuditRetrieve("AuditRetrieve");
    // endregion top navigation

    companion object {
        fun getGlobalProjectRetrievePermissions() = setOf(
            ProjectRetrieve,
            ProjectRetrieveEditUserAssignments,
        )

        fun getProjectMonitorPermissions() = setOf(
            ProjectFormRetrieve,
            ProjectFileApplicationRetrieve,
            ProjectCheckApplicationForm,
            ProjectAssessmentView,
            ProjectStatusDecisionRevert,
            ProjectStatusReturnToApplicant,
            ProjectStartStepTwo,
            ProjectFileAssessmentRetrieve,
            ProjectContractingView,
            ProjectSetToContracted,
            ProjectReportingView,
            ProjectReportingEdit,
            ProjectModificationView,
            ProjectOpenModification,
            ProjectModificationFileAssessmentRetrieve
        )

       val programmeUserRoleDefaultPermissions = setOf(
            ProjectReportingView,
            ProjectContractingPartnerView,
            ProjectContractingView,
            ProjectSetToContracted,
            ProjectContractingManagementView,
            ProjectContractingReportingView,
            ProjectContractsEdit,
            ProjectFormRetrieve,
            ProjectFileApplicationRetrieve,
            ProjectCheckApplicationForm,
            ProjectAssessmentView,
            ProjectAssessmentQualityEnter,
            ProjectAssessmentEligibilityEnter,
            ProjectStatusDecideEligible,
            ProjectStatusDecideIneligible,
            ProjectStatusDecideApproved,
            ProjectStatusDecideApprovedWithConditions,
            ProjectStatusDecideNotApproved,
            ProjectStatusReturnToApplicant,
            ProjectStartStepTwo,
            ProjectFileAssessmentRetrieve,
            ProjectFileAssessmentUpdate,
            ProjectAssessmentChecklistUpdate,
            ProjectAssessmentChecklistSelectedRetrieve,
            ProjectModificationView,
            ProjectStatusDecideModificationApproved,
            ProjectStatusDecideModificationNotApproved,
            ProjectOpenModification,
            ProjectModificationFileAssessmentRetrieve,
            ProjectModificationFileAssessmentUpdate,
            ProjectMonitorCollaboratorsRetrieve,
            ProjectMonitorCollaboratorsUpdate,
            ProjectsWithOwnershipRetrieve,
            ProjectMonitorSharedFolderEdit,
            CallPublishedRetrieve,
            ProjectRetrieve,
            CallRetrieve,
            ProgrammeSetupRetrieve,
            InstitutionsRetrieve,
            InstitutionsAssignmentRetrieve,
            PaymentsRetrieve,
            AdvancePaymentsRetrieve,
            ProgrammeDataExportRetrieve,
            AuditRetrieve
        )
    }
}
