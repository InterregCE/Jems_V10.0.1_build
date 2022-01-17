package io.cloudflight.jems.api.user.dto

enum class UserRolePermissionDTO(val key: String) {

    ProgrammeSetupRetrieve("ProgrammeSetupRetrieve"),
    ProgrammeSetupUpdate("ProgrammeSetupUpdate"),

    CallRetrieve("CallRetrieve"),
    CallPublishedRetrieve("CallPublishedRetrieve"),
    CallUpdate("CallUpdate"),

    ProjectRetrieve("ProjectRetrieve"),
    ProjectRetrieveEditUserAssignments("ProjectRetrieveEditUserAssignments"),
    ProjectsWithOwnershipRetrieve("ProjectsWithOwnershipRetrieve"),
    ProjectCreate("ProjectCreate"),

    ProjectFormRetrieve("ProjectFormRetrieve"),
    ProjectFormUpdate("ProjectFormUpdate"),

    ProjectFileApplicationRetrieve("ProjectFileApplicationRetrieve"),
    ProjectFileApplicationUpdate("ProjectFileApplicationUpdate"),
    ProjectFileAssessmentRetrieve("ProjectFileAssessmentRetrieve"),
    ProjectFileAssessmentUpdate("ProjectFileAssessmentUpdate"),

    ProjectCheckApplicationForm("ProjectCheckApplicationForm"),
    ProjectSubmission("ProjectSubmission"),

    ProjectAssessmentView("ProjectAssessmentView"),
    ProjectAssessmentQualityEnter("ProjectAssessmentQualityEnter"),
    ProjectAssessmentEligibilityEnter("ProjectAssessmentEligibilityEnter"),
    ProjectStatusReturnToApplicant("ProjectStatusReturnToApplicant"),
    ProjectStatusDecideEligible("ProjectStatusDecideEligible"),
    ProjectStatusDecideIneligible("ProjectStatusDecideIneligible"),
    ProjectStatusDecideApproved("ProjectStatusDecideApproved"),
    ProjectStatusDecideApprovedWithConditions("ProjectStatusDecideApprovedWithConditions"),
    ProjectStatusDecideNotApproved("ProjectStatusDecideNotApproved"),
    ProjectStatusDecisionRevert("ProjectStatusDecisionRevert"),
    ProjectStartStepTwo("ProjectStartStepTwo"),

    ProjectContractingView("ProjectContractingView"),
    ProjectSetToContracted("ProjectSetToContracted"),

    ProjectModificationView("ProjectModificationView"),
    ProjectStatusDecideModificationApproved("ProjectStatusDecideModificationApproved"),
    ProjectStatusDecideModificationNotApproved("ProjectStatusDecideModificationNotApproved"),
    ProjectOpenModification("ProjectOpenModification"),
    ProjectModificationFileAssessmentRetrieve("ProjectModificationFileAssessmentRetrieve"),
    ProjectModificationFileAssessmentUpdate("ProjectModificationFileAssessmentUpdate"),

    ProjectCreatorCollaboratorsRetrieve("ProjectCreatorCollaboratorsRetrieve"),
    ProjectCreatorCollaboratorsUpdate("ProjectCreatorCollaboratorsUpdate"),
    ProjectMonitorCollaboratorsRetrieve("ProjectMonitorCollaboratorsRetrieve"),
    ProjectMonitorCollaboratorsUpdate("ProjectMonitorCollaboratorsUpdate"),

    AuditRetrieve("AuditRetrieve"),

    RoleRetrieve("RoleRetrieve"),
    RoleCreate("RoleCreate"),
    RoleUpdate("RoleUpdate"),

    UserRetrieve("UserRetrieve"),
    UserCreate("UserCreate"),
    UserUpdate("UserUpdate"),
    UserUpdateRole("UserUpdateRole"),
    UserUpdatePassword("UserUpdatePassword"),
}
