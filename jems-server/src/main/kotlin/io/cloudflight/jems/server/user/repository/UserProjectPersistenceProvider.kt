package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.user.entity.UserProjectEntity
import io.cloudflight.jems.server.user.entity.UserProjectId
import io.cloudflight.jems.server.user.service.UserProjectPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProjectPersistenceProvider(
    private val userProjectRepository: UserProjectRepository,
) : UserProjectPersistence {

    @Transactional(readOnly = true)
    override fun getProjectIdsForUser(userId: Long): Set<Long> =
        userProjectRepository.findProjectIdsForUserId(userId = userId)

    @Transactional(readOnly = true)
    override fun getUserIdsForProject(projectId: Long): Set<Long> =
        userProjectRepository.findUserIdsForProjectId(projectId = projectId)

    @Transactional
    override fun changeUsersAssignedToProject(
        projectId: Long,
        userIdsToRemove: Set<Long>,
        userIdsToAssign: Set<Long>,
    ): Set<Long> {
        userProjectRepository.deleteAllByIdIn(
            userIdsToRemove.map { UserProjectId(userId = it, projectId = projectId) }
        )

        userProjectRepository.saveAll(
            userIdsToAssign.map { UserProjectEntity(UserProjectId(userId = it, projectId = projectId)) }
        )

        return getUserIdsForProject(projectId = projectId)
    }

}
