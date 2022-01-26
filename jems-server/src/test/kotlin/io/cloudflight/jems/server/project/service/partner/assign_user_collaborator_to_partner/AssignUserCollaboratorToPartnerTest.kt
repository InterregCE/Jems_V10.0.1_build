package io.cloudflight.jems.server.project.service.partner.assign_user_collaborator_to_partner

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.partner.service.partneruser.assign_user_collaborator_to_partner.UsersAreNotValid
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreate
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.cloudflight.jems.server.utils.partner.projectPartnerEntity
import io.cloudflight.jems.server.utils.partner.projectPartnerSummary
import io.cloudflight.jems.server.utils.partner.projectSummary
import io.cloudflight.jems.server.utils.partner.userSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

internal class AssignUserCollaboratorToPartnerTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val PARTNER_ID = 2L
        private const val USER_ID = 3L
        private const val USER_ROLE_ID = 4L

        private val user = userSummary(USER_ID, USER_ROLE_ID)
    }

    @MockK
    lateinit var userPersistence: UserPersistence

    @MockK
    lateinit var userRolePersistence: UserRolePersistence

    @MockK
    lateinit var collaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var partnerRepository: ProjectPartnerRepository

    @RelaxedMockK
    lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var assignUser: AssignUserCollaboratorToPartner

    @BeforeEach
    fun resetAuditService() {
        clearMocks(eventPublisher)
    }

    @Test
    fun `update partner collaborators`() {
        val userData = slot<Map<Long, PartnerCollaboratorLevel>>()
        val expectedResult = setOf(
            PartnerCollaborator(userId = USER_ID, partnerId = PARTNER_ID, userEmail = user.email, PartnerCollaboratorLevel.EDIT),
        )
        val allEmails = slot<Collection<String>>()

        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(setOf(ProjectCreate), emptySet()) } returns setOf(USER_ROLE_ID)
        every { userPersistence.findAllByEmails(capture(allEmails)) } returns listOf(user)
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary()
        every { partnerRepository.getById(PARTNER_ID) } returns projectPartnerEntity()
        every { collaboratorPersistence.changeUsersAssignedToPartner(PROJECT_ID, PARTNER_ID, capture(userData)) } returns expectedResult

        val result = assignUser.updateUserAssignmentsOnPartner(
            PROJECT_ID, PARTNER_ID, setOf(Pair(user.email, PartnerCollaboratorLevel.EDIT))
        )

        assertThat(result).containsExactlyElementsOf(expectedResult)
        assertThat(allEmails.captured).containsExactlyInAnyOrder(user.email)
        assertThat(userData.captured).containsExactlyEntriesOf(mapOf(USER_ID to PartnerCollaboratorLevel.EDIT))
        verify(exactly = 1) { eventPublisher.publishEvent(AssignCollaboratorToPartnerEvent(
            project = projectSummary(),
            partner = projectPartnerSummary(),
            collaborators = expectedResult,
        )) }
    }

    @Test
    fun `update partner collaborators with missing user emails`() {
        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(setOf(ProjectCreate), emptySet()) } returns setOf(USER_ROLE_ID)

        val allEmails = slot<Collection<String>>()
        every { userPersistence.findAllByEmails(capture(allEmails)) } returns emptyList()

        assertThrows<UsersAreNotValid> {
            assignUser.updateUserAssignmentsOnPartner(PROJECT_ID, PARTNER_ID, setOf(Pair(user.email, PartnerCollaboratorLevel.EDIT)))
        }
    }
}
