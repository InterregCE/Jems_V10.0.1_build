package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.Optional


internal class ProjectContractingPartnerAuthorizationTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 9L
        private const val PROJECT_CREATOR_USER_ID = 3L

        private const val PROJECT_COLLABORATOR_VIEW_USER_ID = 4L
        private const val PROJECT_COLLABORATOR_EDIT_USER_ID = 5L
        private const val PROJECT_COLLABORATOR_MANAGE_USER_ID = 6L

        private const val PARTNER_COLLABORATOR_USER_ID = 7L

        private const val PARTNER_ID = 10L

        private val applicantAndStatus = ProjectApplicantAndStatus(
            projectId = PROJECT_ID,
            applicantId = PROJECT_CREATOR_USER_ID,
            collaboratorViewIds = setOf(PROJECT_COLLABORATOR_VIEW_USER_ID),
            collaboratorEditIds = setOf(PROJECT_COLLABORATOR_EDIT_USER_ID),
            collaboratorManageIds = setOf(PROJECT_COLLABORATOR_MANAGE_USER_ID),
            projectStatus = ApplicationStatus.CONTRACTED
        )

        private val partnerCollaborator =  PartnerCollaborator(
            userId = 1L,
            partnerId = 99L,
            userEmail = "collaborator@test.com",
            level = PartnerCollaboratorLevel.VIEW
        )

        private val collaboratorAssignedToProject = CollaboratorAssignedToProject(
            userId = 1L,
            userEmail = "collaborator@test.com",
            level = ProjectCollaboratorLevel.VIEW
        )
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var projectCollaboratorPersistence: UserProjectCollaboratorPersistence

    @MockK
    lateinit var projectReportAuthorization: ProjectReportAuthorization

    @MockK
    lateinit var projectAuthorization: ProjectAuthorization

    @MockK
    lateinit var currentUser: CurrentUser

    @MockK
    lateinit var institutionPersistence: ControllerInstitutionPersistence

    @InjectMockKs
    lateinit var authorization: ProjectContractingPartnerAuthorization


    @BeforeEach
    fun resetMocks() {
        clearMocks(
            currentUser,
            securityService,
            partnerCollaboratorPersistence,
            projectReportAuthorization,
            projectCollaboratorPersistence,
            institutionPersistence
        )
        every { securityService.currentUser } returns currentUser
    }

    @Test
    fun `partner with edit capability`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerEdit) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns false
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(any(), any()) } returns Optional.of(PartnerCollaboratorLevel.EDIT)
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerEdit, any()) } returns false
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID

        assertThat(authorization.hasEditPermission(PARTNER_ID)).isTrue
    }

    @Test
    fun `partner with only view capability`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerEdit) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns false
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(any(), any()) } returns Optional.of(PartnerCollaboratorLevel.VIEW)
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerEdit, any()) } returns false
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID

        assertThat(authorization.hasEditPermission(PARTNER_ID)).isFalse
    }

    @Test
    fun `user who is not partner`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerEdit) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns false
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(any(), any()) } returns Optional.empty()
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerEdit, any()) } returns false
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID

        assertThat(authorization.hasEditPermission(PARTNER_ID)).isFalse
    }


    @ParameterizedTest(name = "hasView from collaborator {0}")
    @EnumSource(value = PartnerCollaboratorLevel::class)
    fun `hasView from collaborator`(level: PartnerCollaboratorLevel) {
        val userId = 100L
        every { securityService.getUserIdOrThrow() } returns userId

        every { partnerPersistence.getProjectIdForPartnerId(10L) } returns PROJECT_ID
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false

        every { projectReportAuthorization.getLevelForUserCollaborator(10L) } returns Optional.of(level)
        every { projectReportAuthorization.getLevelForUserController(10L) } returns Optional.empty()
        every { projectCollaboratorPersistence.getLevelForProjectAndUser(PROJECT_ID, userId) } returns null

        assertThat(authorization.hasViewPermission(10L)).isTrue()
    }

    @ParameterizedTest(name = "hasView from controller {0}")
    @EnumSource(value = UserInstitutionAccessLevel::class)
    fun `hasView from controller`(level: UserInstitutionAccessLevel) {
        val userId = 101L
        every { securityService.getUserIdOrThrow() } returns userId

        every { partnerPersistence.getProjectIdForPartnerId(11L) } returns PROJECT_ID
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false

        every { projectReportAuthorization.getLevelForUserCollaborator(11L) } returns Optional.empty()
        every { projectReportAuthorization.getLevelForUserController(11L) } returns Optional.of(level)
        every { projectCollaboratorPersistence.getLevelForProjectAndUser(PROJECT_ID, userId) } returns null

        assertThat(authorization.hasViewPermission(11L)).isTrue()
    }

    @ParameterizedTest(name = "hasView from projectCollaborator {0}")
    @EnumSource(value = ProjectCollaboratorLevel::class)
    fun `hasView from projectCollaborator`(level: ProjectCollaboratorLevel) {
        val userId = 1020L
        every { securityService.getUserIdOrThrow() } returns userId

        every { partnerPersistence.getProjectIdForPartnerId(12L) } returns PROJECT_ID
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false

        every { projectReportAuthorization.getLevelForUserCollaborator(12L) } returns Optional.empty()
        every { projectReportAuthorization.getLevelForUserController(12L) } returns Optional.empty()
        every { projectCollaboratorPersistence.getLevelForProjectAndUser(PROJECT_ID, userId) } returns level

        assertThat(authorization.hasViewPermission(12L)).isTrue()
    }

    @Test
    fun `hasView from project assign monitor user`() {
        val userId = 103L
        every { securityService.getUserIdOrThrow() } returns userId

        every { partnerPersistence.getProjectIdForPartnerId(13L) } returns PROJECT_ID
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns true

        assertThat(authorization.hasViewPermission(13L)).isTrue()

        verify(exactly = 0) { projectReportAuthorization.getLevelForUserCollaborator(any()) }
        verify(exactly = 0) { projectReportAuthorization.getLevelForUserController(any()) }
        verify(exactly = 0) { projectCollaboratorPersistence.getLevelForProjectAndUser(any(), any()) }
    }

    @Test()
    fun `hasView not`() {
        val userId = 104L
        every { securityService.getUserIdOrThrow() } returns userId

        every { partnerPersistence.getProjectIdForPartnerId(14L) } returns PROJECT_ID
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false

        every { projectReportAuthorization.getLevelForUserCollaborator(14L) } returns Optional.empty()
        every { projectReportAuthorization.getLevelForUserController(14L) } returns Optional.empty()
        every { projectCollaboratorPersistence.getLevelForProjectAndUser(PROJECT_ID, userId) } returns null

        assertThat(authorization.hasViewPermission(14L)).isFalse()
    }

    @Test
    fun `programme user with view capability`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerEdit) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns false
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(any(), any()) } returns Optional.empty()
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerEdit, any()) } returns false
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns true
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID

        assertThat(authorization.hasEditPermission(PARTNER_ID)).isFalse
        assertThat(authorization.hasViewPermission(PARTNER_ID)).isTrue
    }

    @Test
    fun `programme user with edit capability`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerEdit) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns true
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(any(), any()) } returns Optional.empty()
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerEdit, any()) } returns true
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns true
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID

        assertThat(authorization.hasEditPermission(PARTNER_ID)).isTrue
        assertThat(authorization.hasViewPermission(PARTNER_ID)).isTrue
    }

    @Test
    fun `has contracting partners permission - user with monitor permission`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectRetrieve) } returns true
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns true
        every { currentUser.user } returns mockk()
        every { currentUser.user.assignedProjects } returns setOf(1L)

        assertThat(authorization.hasPartnersPermission(1L)).isTrue
    }

    @Test
    fun `has contracting partners permission - user with partner collaborator`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectRetrieve) } returns true
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false
        every { currentUser.user } returns mockk()
        every { securityService.getUserIdOrThrow() } returns 1L
        every { currentUser.user.assignedProjects } returns setOf(1L)
        every { partnerCollaboratorPersistence.findPartnerCollaboratorsByProjectId(1L) } returns setOf(partnerCollaborator)
        every { projectCollaboratorPersistence.getUserIdsForProject(1L) } returns listOf(collaboratorAssignedToProject)
        every { institutionPersistence.getRelatedUserIdsForProject(1L) } returns setOf(100L)

        assertThat(authorization.hasPartnersPermission(1L)).isTrue
    }

    @Test
    fun `has contracting partners permission - user with controller`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectRetrieve) } returns false
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false
        every { currentUser.user } returns mockk()
        every { securityService.getUserIdOrThrow() } returns 100L
        every { currentUser.user.assignedProjects } returns setOf()
        every { partnerCollaboratorPersistence.findPartnerCollaboratorsByProjectId(1L) } returns setOf(partnerCollaborator)
        every { projectCollaboratorPersistence.getUserIdsForProject(1L) } returns listOf(collaboratorAssignedToProject)
        every { institutionPersistence.getRelatedUserIdsForProject(1L) } returns setOf(100L)

        assertThat(authorization.hasPartnersPermission(1L)).isTrue
    }

    @Test
    fun `has no contracting partners permission`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectRetrieve) } returns false
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false
        every { currentUser.user } returns mockk()
        every { securityService.getUserIdOrThrow() } returns 101L
        every { currentUser.user.assignedProjects } returns setOf()
        every { partnerCollaboratorPersistence.findPartnerCollaboratorsByProjectId(1L) } returns setOf(partnerCollaborator)
        every { projectCollaboratorPersistence.getUserIdsForProject(1L) } returns listOf(collaboratorAssignedToProject)
        every { institutionPersistence.getRelatedUserIdsForProject(1L) } returns setOf(100L)

        assertThat(authorization.hasPartnersPermission(1L)).isFalse
    }
}
