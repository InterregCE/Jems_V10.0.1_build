package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.user.dto.UserRolePermissionDTO

data class UserPermissionFilterDTO(
    val needsToHaveAtLeastOneFrom: Set<UserRolePermissionDTO>,
    val needsNotToHaveAnyOf: Set<UserRolePermissionDTO>,
 )
