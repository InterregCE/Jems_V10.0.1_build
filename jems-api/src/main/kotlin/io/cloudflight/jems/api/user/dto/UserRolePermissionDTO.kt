package io.cloudflight.jems.api.user.dto

enum class UserRolePermissionDTO(val key: String) {
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
