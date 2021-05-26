package io.cloudflight.jems.server.user.service.model

enum class UserRolePermission(val key: String) {

    // Module PROGRAMME SETUP
    ProgrammeSetupRetrieve("ProgrammeSetupRetrieve"),
    ProgrammeSetupUpdate("ProgrammeSetupUpdate"),

    // Module APPLICATIONS
    ProjectRetrieve("ProjectRetrieve"),
    ProjectUpdate("ProjectUpdate"),

    // Module APPLICATION LIFECYCLE
    ProjectSubmission("ProjectSubmission"),

    // Module SYSTEM
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
