package io.cloudflight.jems.api.user.dto

enum class UserRolePermissionDTO(val key: String) {
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
