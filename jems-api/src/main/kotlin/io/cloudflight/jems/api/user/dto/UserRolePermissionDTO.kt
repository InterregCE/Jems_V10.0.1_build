package io.cloudflight.jems.api.user.dto

enum class UserRolePermissionDTO(val key: String) {

    // region Creator
    ProjectCreate("ProjectCreate"),

    ProjectCreatorCollaboratorsRetrieve("ProjectCreatorCollaboratorsRetrieve"),
    ProjectCreatorCollaboratorsUpdate("ProjectCreatorCollaboratorsUpdate"),

    ProjectCreatorContractingReportingView("ProjectCreatorContractingReportingView"),
    ProjectCreatorContractingReportingEdit("ProjectCreatorContractingReportingEdit"),

    ProjectCreatorSharedFolderView("ProjectCreatorSharedFolderView"),
    ProjectCreatorSharedFolderEdit("ProjectCreatorSharedFolderEdit"),

    ProjectCreatorReportingProjectCreate("ProjectCreatorReportingProjectCreate"),
    // endregion Creator

    // region Monitor
    ProjectReportingView("ProjectReportingView"),
    ProjectReportingEdit("ProjectReportingEdit"),
    ProjectReportingReOpen("ProjectReportingReOpen"),
    ProjectPartnerControlReportingReOpen("ProjectPartnerControlReportingReOpen"),
    ProjectReportingProjectView("ProjectReportingProjectView"),
    ProjectReportingProjectEdit("ProjectReportingProjectEdit"),
    ProjectReportingProjectReOpen("ProjectReportingProjectReOpen"),
    ProjectReportingVerificationProjectView("ProjectReportingVerificationProjectView"),
    ProjectReportingVerificationProjectEdit("ProjectReportingVerificationProjectEdit"),
    ProjectReportingVerificationFinalize("ProjectReportingVerificationFinalize"),
    ProjectReportingVerificationReOpen("ProjectReportingVerificationReOpen"),
    ProjectReportingChecklistAfterControl("ProjectReportingChecklistAfterControl"),

    ProjectContractingPartnerView("ProjectContractingPartnerView"),
    ProjectContractingPartnerEdit("ProjectContractingPartnerEdit"),
    ProjectContractingPartnerStateAidView("ProjectContractingPartnerStateAidView"),
    ProjectContractingPartnerStateAidEdit("ProjectContractingPartnerStateAidEdit"),

    ProjectContractingView("ProjectContractingView"),
    ProjectSetToContracted("ProjectSetToContracted"),

    ProjectContractingManagementView("ProjectContractingManagementView"),
    ProjectContractingManagementEdit("ProjectContractingManagementEdit"),
    ProjectContractingReportingView("ProjectContractingReportingView"),
    ProjectContractingReportingEdit("ProjectContractingReportingEdit"),
    ProjectContractsView("ProjectContractsView"),
    ProjectContractsEdit("ProjectContractsEdit"),

    ProjectFormRetrieve("ProjectFormRetrieve"),
    ProjectFormUpdate("ProjectFormUpdate"),

    ProjectFileApplicationRetrieve("ProjectFileApplicationRetrieve"),
    ProjectFileApplicationUpdate("ProjectFileApplicationUpdate"),

    ProjectCheckApplicationForm("ProjectCheckApplicationForm"),
    ProjectSubmission("ProjectSubmission"),

    ProjectAssessmentView("ProjectAssessmentView"),
    ProjectAssessmentQualityEnter("ProjectAssessmentQualityEnter"),
    ProjectAssessmentEligibilityEnter("ProjectAssessmentEligibilityEnter"),
    ProjectStatusDecideEligible("ProjectStatusDecideEligible"),
    ProjectStatusDecideIneligible("ProjectStatusDecideIneligible"),
    ProjectStatusDecideApproved("ProjectStatusDecideApproved"),
    ProjectStatusDecideApprovedWithConditions("ProjectStatusDecideApprovedWithConditions"),
    ProjectStatusDecideNotApproved("ProjectStatusDecideNotApproved"),

    ProjectStatusDecisionRevert("ProjectStatusDecisionRevert"),
    ProjectStatusReturnToApplicant("ProjectStatusReturnToApplicant"),
    ProjectStartStepTwo("ProjectStartStepTwo"),

    ProjectFileAssessmentRetrieve("ProjectFileAssessmentRetrieve"),
    ProjectFileAssessmentUpdate("ProjectFileAssessmentUpdate"),

    ProjectAssessmentChecklistUpdate("ProjectAssessmentChecklistUpdate"),
    ProjectAssessmentChecklistConsolidate("ProjectAssessmentChecklistConsolidate"),
    ProjectAssessmentChecklistSelectedRetrieve("ProjectAssessmentChecklistSelectedRetrieve"),
    ProjectAssessmentChecklistSelectedUpdate("ProjectAssessmentChecklistSelectedUpdate"),

    ProjectModificationView("ProjectModificationView"),
    ProjectStatusDecideModificationApproved("ProjectStatusDecideModificationApproved"),
    ProjectStatusDecideModificationNotApproved("ProjectStatusDecideModificationNotApproved"),

    ProjectOpenModification("ProjectOpenModification"),

    ProjectModificationFileAssessmentRetrieve("ProjectModificationFileAssessmentRetrieve"),
    ProjectModificationFileAssessmentUpdate("ProjectModificationFileAssessmentUpdate"),

    ProjectMonitorCollaboratorsRetrieve("ProjectMonitorCollaboratorsRetrieve"),
    ProjectMonitorCollaboratorsUpdate("ProjectMonitorCollaboratorsUpdate"),

    ProjectMonitorSharedFolderView("ProjectMonitorSharedFolderView"),
    ProjectMonitorSharedFolderEdit("ProjectMonitorSharedFolderEdit"),

    ProjectMonitorAuditAndControlView("ProjectMonitorAuditAndControlView"),
    ProjectMonitorAuditAndControlEdit("ProjectMonitorAuditAndControlEdit"),
    ProjectMonitorCloseAuditControl("ProjectMonitorCloseAuditControl"),
    ProjectMonitorCloseAuditControlCorrection("ProjectMonitorCloseAuditControlCorrection"),
    ProjectMonitorReOpenAuditControl("ProjectMonitorReOpenAuditControl"),
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

    ProgrammeDataExportRetrieve("ProgrammeDataExportRetrieve"),

    UserRetrieve("UserRetrieve"),
    RoleRetrieve("RoleRetrieve"),
    UserCreate("UserCreate"),
    UserUpdate("UserUpdate"),
    UserUpdateRole("UserUpdateRole"),
    UserUpdatePassword("UserUpdatePassword"),
    RoleCreate("RoleCreate"),
    RoleUpdate("RoleUpdate"),

    AuditRetrieve("AuditRetrieve"),
    // endregion top navigation
}
