package io.cloudflight.jems.server.user.service.userrole.createUserRole

import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectAssessmentChecklistConsolidate
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectAssessmentChecklistUpdate

private val REQUIRED_CHECKLIST_PERM = Pair(
    ProjectAssessmentChecklistConsolidate /* <- this permission requires: */,
    ProjectAssessmentChecklistUpdate /* this permission is required as precondition */,
)

private val REQUIRED_PERMISSION_COMBINATIONS = setOf(
    REQUIRED_CHECKLIST_PERM,
)

fun checkForFirstInvalidPermissionCombination(permissionSetting: Set<UserRolePermission>): Pair<UserRolePermission, UserRolePermission>? =
    REQUIRED_PERMISSION_COMBINATIONS.firstOrNull {
        permissionSetting.contains(it.first) && !permissionSetting.contains(it.second)
    }
