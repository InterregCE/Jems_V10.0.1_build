package io.cloudflight.jems.server.user.service.model

enum class UserRolePermission(val key: String) {
    ProjectRetrieve("ProjectRetrieve"),
    ProjectUpdate("ProjectUpdate"),
    ProjectSubmission("ProjectSubmission"),

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
