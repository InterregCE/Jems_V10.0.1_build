package io.cloudflight.jems.server.project.repository.partneruser

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel.VIEW
import io.cloudflight.jems.server.project.entity.partneruser.UserPartnerCollaboratorEntity
import io.cloudflight.jems.server.project.entity.partneruser.UserPartnerId
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Optional

internal class UserPartnerCollaboratorPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val PARTNER_ID = 2L
        private const val USER_ID = 3L
    }

    @RelaxedMockK
    lateinit var collaboratorRepository: UserPartnerCollaboratorRepository

    @InjectMockKs
    private lateinit var persistence: UserPartnerCollaboratorPersistenceProvider

    @Test
    fun findPartnerCollaboratorsByProjectId() {
        persistence.findPartnerCollaboratorsByProjectId(PROJECT_ID)
        verify { collaboratorRepository.findByProjectId(PROJECT_ID) }
    }

    @Test
    fun getProjectIdsForUser() {
        persistence.getProjectIdsForUser(USER_ID)
        verify { collaboratorRepository.findAllByIdUserId(USER_ID) }
    }

    @Test
    fun findUserIdsByProjectId() {
        persistence.findUserIdsByProjectId(USER_ID)
        verify { collaboratorRepository.findByProjectId(USER_ID) }
    }

    @Test
    fun findPartnerIdsByUserAndProject() {
        persistence.findPartnersByUserAndProject(USER_ID, PROJECT_ID)
        verify { collaboratorRepository.findAllByIdUserIdAndProjectId(USER_ID, PROJECT_ID) }
    }

    @Test
    fun findAllByProjectAndPartners() {
        persistence.findByProjectAndPartners(PROJECT_ID, setOf(USER_ID))
        verify { collaboratorRepository.findAllByProjectAndPartners(PROJECT_ID, setOf(USER_ID)) }
    }

    @Test
    fun deleteByProjectId() {
        persistence.deleteByProjectId(PROJECT_ID)
        verify { collaboratorRepository.deleteAllByProjectId(PROJECT_ID) }
    }

    @Test
    fun changeUsersAssignedToPartner() {
        every { collaboratorRepository.findByPartnerId(PARTNER_ID) } returns setOf(
            PartnerCollaborator (
                userId = 2L,
                partnerId = PARTNER_ID,
                userEmail = "",
                level = PartnerCollaboratorLevel.EDIT
            )
        )

        val deleted = slot<Collection<UserPartnerId>>()
        val added = slot<Collection<UserPartnerCollaboratorEntity>>()
        every { collaboratorRepository.deleteAllByIdIn(capture(deleted)) } answers { }
        every { collaboratorRepository.saveAll(capture(added)) } returnsArgument 0

        val usersToPersist = mapOf(
            USER_ID to VIEW
        )

        persistence.changeUsersAssignedToPartner(PROJECT_ID, PARTNER_ID, usersToPersist)

        assertThat(deleted.captured).containsExactlyInAnyOrder(UserPartnerId(2L, PARTNER_ID))
        assertThat(added.captured.map { it.id.userId }).containsExactlyInAnyOrder(USER_ID)
        verify { collaboratorRepository.findByPartnerId(PARTNER_ID) }

    }

    @Test
    fun findByUserIdAndPartnerId() {
        val id = UserPartnerId(userId = 45L, partnerId = 7896L)
        every { collaboratorRepository.findById(id) } returns Optional
            .of(UserPartnerCollaboratorEntity(id, 0L, VIEW))
        assertThat(persistence.findByUserIdAndPartnerId(45L, 7896L).get()).isEqualTo(VIEW)
    }

    @Test
    fun `findByUserIdAndPartnerId - not existing`() {
        val notExistingId = UserPartnerId(userId = -1L, partnerId = -1L)

        every { collaboratorRepository.findById(notExistingId) } returns Optional.empty()
        assertThat(persistence.findByUserIdAndPartnerId(-1L, -1L)).isEmpty
    }

}
