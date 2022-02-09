package io.cloudflight.jems.server.project.entity.partneruser

import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectReportingEdit
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectReportingView

enum class PartnerCollaboratorLevel(val correspondingRolePermission: UserRolePermission) {
    VIEW(ProjectReportingView),
    EDIT(ProjectReportingEdit),
}
