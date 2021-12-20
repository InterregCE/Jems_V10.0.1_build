package io.cloudflight.jems.server.user.repository.projectuser

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.projectuser.UserProjectEntity
import io.cloudflight.jems.server.project.entity.projectuser.UserProjectId
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectPersistenceProvider
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class UserProjectPersistenceProviderTest : UnitTest() {

    companion object {
    }

    @MockK
    lateinit var userProjectRepository: UserProjectRepository

    @InjectMockKs
    private lateinit var persistence: UserProjectPersistenceProvider

    @Test
    fun getProjectIdsForUser() {
        every { userProjectRepository.findProjectIdsForUserId(14897L) } returns setOf(14L, 22L)
        assertThat(persistence.getProjectIdsForUser(14897L)).containsExactlyInAnyOrder(14L, 22L)
    }

    @Test
    fun getUserIdsForProject() {
        every { userProjectRepository.findUserIdsForProjectId(14L) } returns setOf(22L, 14897L)
        assertThat(persistence.getUserIdsForProject(14L)).containsExactlyInAnyOrder(22L, 14897L)
    }

    @Test
    fun changeUsersAssignedToProject() {
        val deleted = slot<Collection<UserProjectId>>()
        val added = slot<Collection<UserProjectEntity>>()

        every { userProjectRepository.deleteAllByIdIn(capture(deleted)) } answers { }
        every { userProjectRepository.saveAll(capture(added)) } returnsArgument 0
        every { userProjectRepository.findUserIdsForProjectId(458L) } returns setOf(100L, 991L, 992L)

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
