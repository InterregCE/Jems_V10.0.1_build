package io.cloudflight.jems.api.user.dto

enum class UserRolePermissionDTO(val key: String) {

    ProgrammeSetupRetrieve("ProgrammeSetupRetrieve"),
    ProgrammeSetupUpdate("ProgrammeSetupUpdate"),

    CallRetrieve("CallRetrieve"),
    CallPublishedRetrieve("CallPublishedRetrieve"),
    CallUpdate("CallUpdate"),

    ProjectRetrieve("ProjectRetrieve"),
    ProjectsWithOwnershipRetrieve("ProjectsWithOwnershipRetrieve"),
    ProjectCreate("ProjectCreate"),
    ProjectUpdate("ProjectUpdate"),

    ProjectFileApplicationRetrieve("ProjectFileApplicationRetrieve"),
    ProjectFileApplicationUpdate("ProjectFileApplicationUpdate"),
    ProjectFileAssessmentRetrieve("ProjectFileAssessmentRetrieve"),
    ProjectFileAssessmentUpdate("ProjectFileAssessmentUpdate"),

    ProjectSubmission("ProjectSubmission"),

    ProjectAssessmentView("ProjectAssessmentView"),
    ProjectAssessmentQualityEnter("ProjectAssessmentQualityEnter"),
    ProjectAssessmentEligibilityEnter("ProjectAssessmentEligibilityEnter"),
    ProjectStatusDecideEligible("ProjectStatusDecideEligible"),
    ProjectStatusDecideIneligible("ProjectStatusDecideIneligible"),
    ProjectStatusDecideApproved("ProjectStatusDecideApproved"),
    ProjectStatusDecideApprovedWithConditions("ProjectStatusDecideApprovedWithConditions"),
    ProjectStatusDecideNotApproved("ProjectStatusDecideNotApproved"),

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
