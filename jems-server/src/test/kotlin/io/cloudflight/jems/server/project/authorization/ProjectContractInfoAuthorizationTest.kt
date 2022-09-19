package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ProjectContractInfoAuthorizationTest: UnitTest() {

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
    }


    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var  authorizationUtilService: AuthorizationUtilService

    @MockK
    lateinit var currentUser: CurrentUser

    @InjectMockKs
    lateinit var projectContractInfoAuthorization: ProjectContractInfoAuthorization

    @BeforeEach
    fun resetMocks() {
        clearMocks(currentUser)
        clearMocks(securityService)
        clearMocks(authorizationUtilService)
        every { securityService.currentUser } returns currentUser
    }


    @Test
    fun `project creator can view and edit`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractsView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractsEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns PROJECT_CREATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { authorizationUtilService.userIsPartnerCollaboratorForProject(
            userId = PROJECT_CREATOR_USER_ID,
            projectId = PROJECT_ID
        )
        } returns false
        every {
            authorizationUtilService.userIsProjectOwnerOrProjectCollaborator(userId = PROJECT_CREATOR_USER_ID, applicantAndStatus)
        } returns true
        every {
            authorizationUtilService.userIsProjectCollaboratorWithEditPrivilege(userId = PROJECT_CREATOR_USER_ID, applicantAndStatus)
        } returns true
        Assertions.assertThat(projectContractInfoAuthorization.canViewContractInfo(PROJECT_ID)).isTrue
        Assertions.assertThat(projectContractInfoAuthorization.canEditContractInfo(PROJECT_ID)).isTrue
    }

    @Test
    fun `partner collaborator can view but not edit`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractsView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractsEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { authorizationUtilService.userIsPartnerCollaboratorForProject(
            userId = PARTNER_COLLABORATOR_USER_ID,
            projectId = PROJECT_ID
        )
        } returns true
        every {
            authorizationUtilService.userIsProjectOwnerOrProjectCollaborator(userId = PARTNER_COLLABORATOR_USER_ID, applicantAndStatus)
        } returns false
        every {
            authorizationUtilService.userIsProjectCollaboratorWithEditPrivilege(userId = PARTNER_COLLABORATOR_USER_ID, applicantAndStatus)
        } returns false
        Assertions.assertThat(projectContractInfoAuthorization.canViewContractInfo(PROJECT_ID)).isTrue
        Assertions.assertThat(projectContractInfoAuthorization.canEditContractInfo(PROJECT_ID)).isFalse
    }

    @Test
    fun `user with view can not edit`() {
        every { currentUser.user.id } returns PROGRAMME_USER_ID
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { currentUser.hasPermission(UserRolePermission.ProjectContractsView) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectContractsEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns PROGRAMME_USER_ID
        every { securityService.currentUser } returns currentUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { authorizationUtilService.userIsPartnerCollaboratorForProject(
            userId = PROGRAMME_USER_ID,
            projectId = PROJECT_ID
        )
        } returns false
        every {
            authorizationUtilService.userIsProjectOwnerOrProjectCollaborator(userId = PROGRAMME_USER_ID, applicantAndStatus)
        } returns false
        every {
            authorizationUtilService.userIsProjectCollaboratorWithEditPrivilege(userId = PROGRAMME_USER_ID, applicantAndStatus)
        } returns false
        Assertions.assertThat(projectContractInfoAuthorization.canViewContractInfo(PROJECT_ID)).isTrue
        Assertions.assertThat(projectContractInfoAuthorization.canEditContractInfo(PROJECT_ID)).isFalse
    }

    @Test
    fun `user with edit can view and edit`() {
        every { currentUser.user.id } returns PROGRAMME_USER_ID
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { currentUser.hasPermission(UserRolePermission.ProjectContractsView) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectContractsEdit) } returns true
        every { securityService.getUserIdOrThrow() } returns PROGRAMME_USER_ID
        every { securityService.currentUser } returns currentUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { authorizationUtilService.userIsPartnerCollaboratorForProject(
            userId = PROGRAMME_USER_ID,
            projectId = PROJECT_ID
        )
        } returns false
        every {
            authorizationUtilService.userIsProjectOwnerOrProjectCollaborator(userId = PROGRAMME_USER_ID, applicantAndStatus)
        } returns false
        every {
            authorizationUtilService.userIsProjectCollaboratorWithEditPrivilege(userId = PROGRAMME_USER_ID, applicantAndStatus)
        } returns false
        Assertions.assertThat(projectContractInfoAuthorization.canViewContractInfo(PROJECT_ID)).isTrue
        Assertions.assertThat(projectContractInfoAuthorization.canEditContractInfo(PROJECT_ID)).isTrue
    }
}
