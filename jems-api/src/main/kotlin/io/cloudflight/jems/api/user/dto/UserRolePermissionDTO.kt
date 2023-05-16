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
    // endregion Creator

    // region Monitor
    ProjectReportingView("ProjectReportingView"),
    ProjectReportingEdit("ProjectReportingEdit"),
    ProjectReportingReOpen("ProjectReportingReOpen"),
    ProjectReportingProjectView("ProjectReportingProjectView"),
    ProjectReportingProjectEdit("ProjectReportingProjectEdit"),
    ProjectReportingChecklistAfterControl("ProjectReportingChecklistAfterControl"),

    ProjectContractingPartnerView("ProjectContractingPartnerView"),
    ProjectContractingPartnerEdit("ProjectContractingPartnerEdit"),

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
    // endregion Monitor

    // region top navigation
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
