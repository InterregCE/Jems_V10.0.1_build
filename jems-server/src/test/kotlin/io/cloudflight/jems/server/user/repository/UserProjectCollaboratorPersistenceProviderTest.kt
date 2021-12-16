package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.entity.CollaboratorLevel
import io.cloudflight.jems.server.user.entity.UserProjectCollaboratorEntity
import io.cloudflight.jems.server.user.entity.UserProjectId
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

internal class UserProjectCollaboratorPersistenceProviderTest : UnitTest() {

    @MockK
    lateinit var collaboratorRepository: UserProjectCollaboratorRepository

    @InjectMockKs
    private lateinit var persistence: UserProjectCollaboratorPersistenceProvider

    @Test
    fun getProjectIdsForUser() {
        every { collaboratorRepository.findAllByIdUserId(12097L) } returns setOf(
            UserProjectCollaboratorEntity(UserProjectId(12097L, 18L), CollaboratorLevel.VIEW),
            UserProjectCollaboratorEntity(UserProjectId(12097L, 20L), CollaboratorLevel.EDIT),
            UserProjectCollaboratorEntity(UserProjectId(12097L, 22L), CollaboratorLevel.MANAGE),
        )
        assertThat(persistence.getProjectIdsForUser(12097L)).containsExactlyInAnyOrder(18L, 20L, 22L)
    }

    @Test
    fun getUserIdsForProject() {
        val result = listOf(
            CollaboratorAssignedToProject(14201L, "email1", CollaboratorLevel.VIEW),
            CollaboratorAssignedToProject(14202L, "email2", CollaboratorLevel.VIEW),
            CollaboratorAssignedToProject(14203L, "email3", CollaboratorLevel.VIEW),
        )
        every { collaboratorRepository.findAllByProjectId(20L) } returns result
        assertThat(persistence.getUserIdsForProject(20L)).containsExactlyElementsOf(result)
    }

    @Test
    fun `getLevelForProjectAndUser - existing`() {
        every { collaboratorRepository.findById(UserProjectId(400L, 25L)) } returns Optional.of(
            UserProjectCollaboratorEntity(UserProjectId(400L, 25L), CollaboratorLevel.VIEW)
        )
        assertThat(persistence.getLevelForProjectAndUser(25L, 400L)).isEqualTo(CollaboratorLevel.VIEW)
    }

    @Test
    fun `getLevelForProjectAndUser - not-existing`() {
        every { collaboratorRepository.findById(UserProjectId(401L, 26L)) } returns Optional.empty()
        assertThat(persistence.getLevelForProjectAndUser(26L, 401L)).isNull()
    }

    @Test
    fun changeUsersAssignedToProject() {
        val deleted = slot<Collection<UserProjectId>>()
        val added = slot<Collection<UserProjectCollaboratorEntity>>()

        every { collaboratorRepository.findAllByProjectId(11L) } returns listOf(
            CollaboratorAssignedToProject(256L, "user-to-be-removed", CollaboratorLevel.VIEW),
            CollaboratorAssignedToProject(257L, "user-to-stay", CollaboratorLevel.MANAGE),
        ) andThen listOf(
            CollaboratorAssignedToProject(257L, "user-to-stay", CollaboratorLevel.MANAGE),
            CollaboratorAssignedToProject(1000L, "user-new-to-be-assigned", CollaboratorLevel.EDIT),
        )
        every { collaboratorRepository.deleteAllByIdIn(capture(deleted)) } answers { }
        every { collaboratorRepository.saveAll(capture(added)) } returnsArgument 0

        assertThat(persistence.changeUsersAssignedToProject(
            projectId = 11L,
            usersToPersist = mapOf(
                257L to CollaboratorLevel.MANAGE,
                1000L to CollaboratorLevel.EDIT,
            ),
        )).containsExactly(
            CollaboratorAssignedToProject(userId = 257L, "user-to-stay", CollaboratorLevel.MANAGE),
            CollaboratorAssignedToProject(userId = 1000L, "user-new-to-be-assigned", CollaboratorLevel.EDIT),
        )

        assertThat(deleted.captured).containsExactlyInAnyOrder(UserProjectId(projectId = 11L, userId = 256L))
        assertThat(added.captured).hasSize(2)
        with(added.captured.find { it.id.userId == 257L }!!) {
            assertThat(id).isEqualTo(UserProjectId(257L, 11L))
            assertThat(level).isEqualTo(CollaboratorLevel.MANAGE)
        }
        with(added.captured.find { it.id.userId == 1000L }!!) {
            assertThat(id).isEqualTo(UserProjectId(1000L, 11L))
            assertThat(level).isEqualTo(CollaboratorLevel.EDIT)
        }
    }

}
