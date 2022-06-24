package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ProjectContractingManagementAuthorizationTest: UnitTest() {

    companion object {
        private const val PARTNER_ID = 21L
        private const val PROJECT_ID = 9L
        private const val PROJECT_CREATOR_USER_ID = 3L

        private const val PROJECT_COLLABORATOR_VIEW_USER_ID = 4L
        private const val PROJECT_COLLABORATOR_EDIT_USER_ID = 5L
        private const val PROJECT_COLLABORATOR_MANAGE_USER_ID = 6L

        private const val PARTNER_COLLABORATOR_USER_ID = 7L

        private const val MONITOR_USER_ID = 8L


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
            partnerId = PARTNER_ID,
            userEmail = "user05@jems.eu",
            level = PartnerCollaboratorLevel.EDIT
        )
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var currentUser: CurrentUser

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var projectManagementAuthorization: ProjectContractingManagementAuthorization


    @BeforeEach
    fun resetMocks() {
        clearMocks(currentUser)
        clearMocks(securityService)
        every { securityService.currentUser } returns currentUser
    }


    @Test
    fun `project creator can view and edit`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns PROJECT_CREATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = PROJECT_CREATOR_USER_ID,
            projectId = PROJECT_ID)
        } returns emptySet()
        Assertions.assertThat(projectManagementAuthorization.canViewProjectManagement(PROJECT_ID)).isTrue
        Assertions.assertThat(projectManagementAuthorization.canEditProjectManagement(PROJECT_ID)).isTrue
    }

    @Test
    fun `partner collaborator can view`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = PARTNER_COLLABORATOR_USER_ID,
            projectId = PROJECT_ID)
        } returns setOf(partnerCollaborator)
        Assertions.assertThat(projectManagementAuthorization.canViewProjectManagement(PROJECT_ID)).isTrue
    }

    @Test
    fun `partner collaborator can NOT edit`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = PARTNER_COLLABORATOR_USER_ID,
            projectId = PROJECT_ID)
        } returns setOf(partnerCollaborator)
        Assertions.assertThat(projectManagementAuthorization.canEditProjectManagement(PROJECT_ID)).isFalse
    }

    @Test
    fun `project collaborator can view`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns PROJECT_COLLABORATOR_VIEW_USER_ID
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = PROJECT_COLLABORATOR_VIEW_USER_ID,
            projectId = PROJECT_ID)
        } returns emptySet()
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        Assertions.assertThat(projectManagementAuthorization.canViewProjectManagement(PROJECT_ID)).isTrue
    }

    @Test
    fun `project collaborator with view permission can NOT edit`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns PROJECT_COLLABORATOR_VIEW_USER_ID
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = PROJECT_COLLABORATOR_VIEW_USER_ID,
            projectId = PROJECT_ID)
        } returns emptySet()
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        Assertions.assertThat(projectManagementAuthorization.canEditProjectManagement(PROJECT_ID)).isFalse
    }

    @Test
    fun `project collaborator with edit permission can view and edit`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns PROJECT_COLLABORATOR_EDIT_USER_ID
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = PROJECT_COLLABORATOR_EDIT_USER_ID,
            projectId = PROJECT_ID)
        } returns emptySet()
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        Assertions.assertThat(projectManagementAuthorization.canViewProjectManagement(PROJECT_ID)).isTrue
        Assertions.assertThat(projectManagementAuthorization.canEditProjectManagement(PROJECT_ID)).isTrue
    }

    @Test
    fun `project collaborator with manage can view and edit`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns PROJECT_COLLABORATOR_MANAGE_USER_ID
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = PROJECT_COLLABORATOR_MANAGE_USER_ID,
            projectId = PROJECT_ID)
        } returns emptySet()
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        Assertions.assertThat(projectManagementAuthorization.canViewProjectManagement(PROJECT_ID)).isTrue
        Assertions.assertThat(projectManagementAuthorization.canEditProjectManagement(PROJECT_ID)).isTrue
    }

    @Test
    fun `monitor user with permission can view`() {
        every { currentUser.user.id } returns MONITOR_USER_ID
        every { securityService.getUserIdOrThrow() } returns MONITOR_USER_ID
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = MONITOR_USER_ID,
            projectId = PROJECT_ID)
        } returns emptySet()
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementView) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementEdit) } returns false

        Assertions.assertThat(projectManagementAuthorization.canViewProjectManagement(PROJECT_ID)).isTrue
    }

    @Test
    fun `monitor user with view permission can NOT edit`() {
        every { currentUser.user.id } returns MONITOR_USER_ID
        every { securityService.getUserIdOrThrow() } returns MONITOR_USER_ID
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = MONITOR_USER_ID,
            projectId = PROJECT_ID)
        } returns emptySet()
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementView) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementEdit) } returns false

        Assertions.assertThat(projectManagementAuthorization.canEditProjectManagement(PROJECT_ID)).isFalse
    }

    @Test
    fun `monitor user with permissions can view and edit`() {
        every { currentUser.user.id } returns MONITOR_USER_ID
        every { securityService.getUserIdOrThrow() } returns MONITOR_USER_ID
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = MONITOR_USER_ID,
            projectId = PROJECT_ID)
        } returns emptySet()
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementView) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementEdit) } returns true

        Assertions.assertThat(projectManagementAuthorization.canViewProjectManagement(PROJECT_ID)).isTrue
        Assertions.assertThat(projectManagementAuthorization.canEditProjectManagement(PROJECT_ID)).isTrue
    }

    @Test
    fun `applicant user without permissions can NOT view or edit`() {
        every { currentUser.user.id } returns 9L
        every { securityService.getUserIdOrThrow() } returns 9L
        every { currentUser.user.assignedProjects } returns setOf(99L)
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = 9L,
            projectId = PROJECT_ID)
        } returns emptySet()
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingManagementEdit) } returns false

        Assertions.assertThat(projectManagementAuthorization.canViewProjectManagement(PROJECT_ID)).isFalse
        Assertions.assertThat(projectManagementAuthorization.canEditProjectManagement(PROJECT_ID)).isFalse
    }


}


