package io.cloudflight.jems.server.project.repository.partneruser

import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.entity.partneruser.UserPartnerCollaboratorEntity
import io.cloudflight.jems.server.project.entity.partneruser.UserPartnerId
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserPartnerCollaboratorPersistenceProvider(
    private val collaboratorRepository: UserPartnerCollaboratorRepository,
) : UserPartnerCollaboratorPersistence {

    @Transactional(readOnly = true)
    override fun getProjectIdsForUser(userId: Long): Set<Long> =
        collaboratorRepository.findAllByIdUserId(userId).map { it.projectId }.toSet()

    @Transactional(readOnly = true)
    override fun findUserIdsByProjectId(projectId: Long): Set<Long> =
        collaboratorRepository.findByProjectId(projectId).map { it.userId }.toSet()

    @Transactional(readOnly = true)
    override fun findPartnerIdsByUserAndProject(userId: Long, projectId: Long): Set<Long> =
        collaboratorRepository.findAllByIdUserIdAndProjectId(userId = userId, projectId = projectId)
            .map { it.id.partnerId }.toSet()

    @Transactional(readOnly = true)
    override fun findPartnerCollaboratorsByProjectId(projectId: Long, partnerIds: Set<Long>): Set<PartnerCollaborator> =
        collaboratorRepository.findByProjectId(projectId = projectId)

    @Transactional
    override fun changeUsersAssignedToPartner(
        projectId: Long,
        partnerId: Long,
        usersToPersist: Map<Long, PartnerCollaboratorLevel>
    ): Set<PartnerCollaborator> {
        val alreadyAssignedUserIds = collaboratorRepository.findByPartnerId(partnerId).mapTo(HashSet()) { it.userId }

        collaboratorRepository.deleteAllByIdIn(
            alreadyAssignedUserIds.minus(usersToPersist.keys)
                .map { UserPartnerId(userId = it, partnerId = partnerId) }
        )

        collaboratorRepository.saveAll(
            usersToPersist.map {
                UserPartnerCollaboratorEntity(
                    UserPartnerId(userId = it.key, partnerId = partnerId),
                    projectId = projectId,
                    level = it.value
                )
            }
        )

        return collaboratorRepository.findByPartnerId(partnerId = partnerId)
    }

    @Transactional
    override fun deleteByProjectId(projectId: Long) =
        collaboratorRepository.deleteAllByProjectId(projectId)
}
