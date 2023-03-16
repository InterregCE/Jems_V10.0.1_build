package io.cloudflight.jems.server.project.repository.projectuser

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.projectuser.UserProjectEntity
import io.cloudflight.jems.server.project.entity.projectuser.UserProjectId
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.utils.uploadedBy
import io.cloudflight.jems.server.utils.userEntity
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class UserProjectPersistenceProviderTest : UnitTest() {

    companion object {
        private fun userMock(id: Long): UserEntity {
            val user = mockk<UserEntity>()
            every { user.id } returns id
            every { user.email } returns "$id-email"
            every { user.sendNotificationsToEmail } returns false
            every { user.name } returns "$id-name"
            every { user.surname } returns "$id-surname"
            every { user.userRole } returns UserRoleEntity(id = 3L, name = "role-3")
            every { user.userStatus } returns UserStatus.ACTIVE
            return user
        }
    }

    @MockK
    private lateinit var userProjectRepository: UserProjectRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var persistence: UserProjectPersistenceProvider

    @Test
    fun getProjectIdsForUser() {
        every { userProjectRepository.findProjectIdsForUserId(14897L) } returns setOf(14L, 22L)
        assertThat(persistence.getProjectIdsForUser(14897L)).containsExactlyInAnyOrder(14L, 22L)
    }

    @Test
    fun getUserIdsForProject() {
        val userIdsForProject = setOf(22L, 14897L)
        every { userProjectRepository.findUserIdsForProjectId(14L) } returns userIdsForProject
        every { userRepository.findAllById(userIdsForProject) } returns listOf(userEntity)
        assertThat(persistence.getUsersForProject(14L)).containsExactly(uploadedBy)
    }

    @Test
    fun changeUsersAssignedToProject() {
        val deleted = slot<Collection<UserProjectId>>()
        val added = slot<Collection<UserProjectEntity>>()

        every { userProjectRepository.deleteAllByIdIn(capture(deleted)) } answers { }
        every { userProjectRepository.saveAll(capture(added)) } returnsArgument 0
        every { userProjectRepository.findUserIdsForProjectId(458L) } returns setOf(100L, 991L, 992L)
        every { userRepository.findAllById(setOf(100L, 991L, 992L)) } returns
            listOf(userMock(100L), userMock(991L), userMock(992L))

        assertThat(persistence.changeUsersAssignedToProject(
            projectId = 458L,
            userIdsToRemove = setOf(256L, 257L),
            userIdsToAssign = setOf(991L, 992L),
        )).containsExactlyInAnyOrder(100L, 991L, 992L)

        assertThat(deleted.captured).containsExactlyInAnyOrder(
            UserProjectId(projectId = 458L, userId = 256L),
            UserProjectId(projectId = 458L, userId = 257L),
        )
        assertThat(added.captured.map { it.id.userId }).containsExactlyInAnyOrder(991, 992)
    }

}
