package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
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
        private const val PROJECT_CREATOR_USER_ID = 3L

        private const val PROJECT_COLLABORATOR_VIEW_USER_ID = 4L
        private const val PROJECT_COLLABORATOR_EDIT_USER_ID = 5L
        private const val PROJECT_COLLABORATOR_MANAGE_USER_ID = 6L

        private const val PARTNER_COLLABORATOR_USER_ID = 7L

        private const val PROGRAMME_USER_ID = 8L


        private val applicantAndStatus = ProjectApplicantAndStatus(
            projectId = PROJECT_ID,
            applicantId = PROJECT_CREATOR_USER_ID,
            collaboratorViewIds = setOf(PROJECT_COLLABORATOR_VIEW_USER_ID),
            collaboratorEditIds = setOf(PROJECT_COLLABORATOR_EDIT_USER_ID),
            collaboratorManageIds = setOf(PROJECT_COLLABORATOR_MANAGE_USER_ID),
            projectStatus = ApplicationStatus.APPROVED
        )

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
    fun `User Is Project Owner or Project Collaborator`() {
        assertThat(authorizationUtilService.userIsProjectOwnerOrProjectCollaborator(PROJECT_CREATOR_USER_ID, applicantAndStatus)).isTrue
        assertThat(authorizationUtilService.userIsProjectOwnerOrProjectCollaborator(PROJECT_COLLABORATOR_VIEW_USER_ID, applicantAndStatus)).isTrue
    }


    @Test
    fun `User Is NOT Owner or Project Collaborator`() {
        assertThat(authorizationUtilService.userIsProjectOwnerOrProjectCollaborator(99L, applicantAndStatus)).isFalse
    }

    @Test
    fun userIsProjectCollaboratorWithEditPrivilege() {
        assertThat(authorizationUtilService.userIsProjectOwnerOrProjectCollaborator(PROJECT_COLLABORATOR_EDIT_USER_ID, applicantAndStatus)).isTrue
    }

    @Test
    fun userIsNOTProjectCollaboratorWithEditPrivilege() {
        assertThat(authorizationUtilService.userIsProjectOwnerOrProjectCollaborator(PROJECT_COLLABORATOR_VIEW_USER_ID, applicantAndStatus)).isTrue
    }

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
