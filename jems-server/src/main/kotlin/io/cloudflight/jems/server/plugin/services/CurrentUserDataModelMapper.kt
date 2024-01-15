package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.project.assignment.PartnerCollaboratorData
import io.cloudflight.jems.plugin.contract.models.project.assignment.PartnerCollaboratorLevelData
import io.cloudflight.jems.plugin.contract.models.project.assignment.ProjectCollaboratorLevelData
import io.cloudflight.jems.plugin.contract.models.user.CurrentUserData
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator

fun CurrentUser.toDataModel() = CurrentUserData(
    id = user.id,
    email = user.email,
    assignProjectIds = user.assignedProjects,
    rolePermissions = getAuthorities().map { it.authority }.toSet(),
)

fun PartnerCollaboratorLevel.toDataModel() = PartnerCollaboratorLevelData.valueOf(this.toString())
fun ProjectCollaboratorLevel.toDataModel() = ProjectCollaboratorLevelData.valueOf(this.toString())
fun PartnerCollaborator.toDataModel() = PartnerCollaboratorData(
    partnerId = partnerId,
    userId = userId,
    userEmail = userEmail,
    level = level.toDataModel(),
    gdpr = gdpr
)
