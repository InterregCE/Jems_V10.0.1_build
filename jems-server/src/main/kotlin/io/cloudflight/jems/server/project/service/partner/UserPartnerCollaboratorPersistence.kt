package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator

interface UserPartnerCollaboratorPersistence {

    fun getProjectIdsForUser(userId: Long): Set<Long>

    fun findPartnersByUserAndProject(userId: Long, projectId: Long): Set<PartnerCollaborator>

    fun findPartnerCollaboratorsByProjectId(projectId: Long): Set<PartnerCollaborator>

    fun changeUsersAssignedToPartner(
        projectId: Long,
        partnerId: Long,
        usersToPersist: Map<Long, PartnerCollaboratorLevel>,
    ): Set<PartnerCollaborator>

    fun findUserIdsByProjectId(projectId: Long): Set<Long>

    fun deleteByProjectId(projectId: Long)
}
