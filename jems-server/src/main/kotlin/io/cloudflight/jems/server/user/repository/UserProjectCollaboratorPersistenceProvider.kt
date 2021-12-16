package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.user.entity.CollaboratorLevel
import io.cloudflight.jems.server.user.entity.UserProjectCollaboratorEntity
import io.cloudflight.jems.server.user.entity.UserProjectId
import io.cloudflight.jems.server.user.service.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProjectCollaboratorPersistenceProvider(
    private val collaboratorRepository: UserProjectCollaboratorRepository,
) : UserProjectCollaboratorPersistence {

    @Transactional(readOnly = true)
    override fun getProjectIdsForUser(userId: Long): Set<Long> =
        collaboratorRepository.findAllByIdUserId(userId = userId).mapTo(HashSet()) { it.id.projectId }

    @Transactional(readOnly = true)
    override fun getUserIdsForProject(projectId: Long): List<CollaboratorAssignedToProject> =
        collaboratorRepository.findAllByProjectId(projectId)

    @Transactional(readOnly = true)
    override fun getLevelForProjectAndUser(projectId: Long, userId: Long): CollaboratorLevel? =
        collaboratorRepository.findById(UserProjectId(userId = userId, projectId = projectId)).orElse(null)?.level

    @Transactional
    override fun changeUsersAssignedToProject(
        projectId: Long,
        usersToPersist: Map<Long, CollaboratorLevel>
    ): List<CollaboratorAssignedToProject> {
        val alreadyAssignedUserIds = getUserIdsForProject(projectId).mapTo(HashSet()) { it.userId }

        collaboratorRepository.deleteAllByIdIn(
            alreadyAssignedUserIds.minus(usersToPersist.keys).map { UserProjectId(userId = it, projectId = projectId) }
        )

        collaboratorRepository.saveAll(
            usersToPersist.map {
                UserProjectCollaboratorEntity(UserProjectId(userId = it.key, projectId = projectId), level = it.value)
            }
        )

        return getUserIdsForProject(projectId = projectId)
    }

}
