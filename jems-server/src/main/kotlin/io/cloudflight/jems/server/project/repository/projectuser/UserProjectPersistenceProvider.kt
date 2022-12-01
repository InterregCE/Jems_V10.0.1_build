package io.cloudflight.jems.server.project.repository.projectuser

import io.cloudflight.jems.server.project.entity.projectuser.UserProjectEntity
import io.cloudflight.jems.server.project.entity.projectuser.UserProjectId
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProjectPersistenceProvider(
    private val userProjectRepository: UserProjectRepository,
    private val userRepository: UserRepository,
) : UserProjectPersistence {

    @Transactional(readOnly = true)
    override fun getProjectIdsForUser(userId: Long): Set<Long> =
        userProjectRepository.findProjectIdsForUserId(userId = userId)

    @Transactional(readOnly = true)
    override fun getUsersForProject(projectId: Long): Set<UserSummary> =
        userRepository.findAllById(
            userProjectRepository.findUserIdsForProjectId(projectId = projectId),
        ).mapTo(HashSet()) { it.toUserSummary() }

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

        return getUsersForProject(projectId = projectId).mapTo(HashSet()) { it.id }
    }

    @Transactional
    override fun unassignUserFromProjects(
        userId: Long
    ) = userProjectRepository.deleteAllByIdUserId(userId)

}
