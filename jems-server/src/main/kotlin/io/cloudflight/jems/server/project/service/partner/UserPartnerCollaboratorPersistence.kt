package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import java.util.Optional

interface UserPartnerCollaboratorPersistence {

    fun getProjectIdsForUser(userId: Long): Set<Long>

    fun findPartnersByUserAndProject(userId: Long, projectId: Long): Set<PartnerCollaborator>

    fun findPartnerCollaboratorsByProjectId(projectId: Long): Set<PartnerCollaborator>

    fun findByUserIdAndPartnerId(userId: Long, partnerId: Long): Optional<PartnerCollaboratorLevel>

    fun changeUsersAssignedToPartner(
        projectId: Long,
        partnerId: Long,
        usersToPersist: Map<Long, PartnerCollaboratorLevel>,
    ): Set<PartnerCollaborator>

    fun findUserIdsByProjectId(projectId: Long): Set<Long>

    fun deleteByProjectId(projectId: Long)
}
