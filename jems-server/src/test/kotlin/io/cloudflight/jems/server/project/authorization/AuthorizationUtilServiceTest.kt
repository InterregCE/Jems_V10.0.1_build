package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuthorizationUtilServiceTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 9L

        private const val PARTNER_COLLABORATOR_USER_ID = 7L

        private const val PROGRAMME_USER_ID = 8L

        private val partnerCollaborator = PartnerCollaborator(
            userId = PARTNER_COLLABORATOR_USER_ID,
            partnerId = PARTNER_COLLABORATOR_USER_ID,
            userEmail = "user05@jems.eu",
            level = PartnerCollaboratorLevel.EDIT
        )
    }

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @InjectMockKs
    lateinit var authorizationUtilService: AuthorizationUtilService

    @Test
    fun userIsPartnerCollaboratorForProject() {
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = any(),
            projectId = any(),
        ) } returns setOf(partnerCollaborator)

        assertThat(authorizationUtilService.userIsPartnerCollaboratorForProject(PARTNER_COLLABORATOR_USER_ID, PROJECT_ID)).isTrue
    }

    @Test
    fun userIsNotPartnerCollaboratorForProject() {
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = any(),
            projectId = any(),
        ) } returns emptySet()
        assertThat(authorizationUtilService.userIsPartnerCollaboratorForProject(PROGRAMME_USER_ID, PROJECT_ID)).isFalse
    }
}
