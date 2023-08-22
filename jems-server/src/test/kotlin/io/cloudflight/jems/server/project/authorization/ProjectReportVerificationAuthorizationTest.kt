package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

internal class ProjectReportVerificationAuthorizationTest: UnitTest() {

    companion object {
        private const val REPORT_ID = 80L
        private const val PROJECT_ID = 90L

        private fun report(status: ProjectReportStatus?, projectId: Long): ProjectReportModel {
            val report = mockk<ProjectReportModel>()
            if (status != null)
                every { report.status } returns status
            every { report.projectId } returns projectId
            return report
        }

        private fun collaboratorAssignedToProject(userId: Long, level: ProjectCollaboratorLevel) : CollaboratorAssignedToProject =
            CollaboratorAssignedToProject(
                userId = userId,
                userEmail = "test@mail.com",
                sendNotificationsToEmail = false,
                userStatus = UserStatus.ACTIVE,
                level = level
            )

    }

    @MockK private lateinit var projectReportPersistence: ProjectReportPersistence
    @MockK private lateinit var userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence
    @MockK private lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence
    @MockK private lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence
    @MockK private lateinit var securityService: SecurityService
    @MockK private lateinit var projectPersistence: ProjectPersistence

    @MockK private lateinit var currentUser: CurrentUser

    @InjectMockKs private lateinit var authorization: ProjectReportVerificationAuthorization

    @BeforeEach
    fun resetMocks() {
        clearMocks(projectReportPersistence, userProjectCollaboratorPersistence, projectPersistence,
            controllerInstitutionPersistence, partnerCollaboratorPersistence, securityService, currentUser)
    }

    @ParameterizedTest(name = "monitor user with-without privilege can access overview {0}")
    @ValueSource(booleans = [true, false])
    fun `monitor user with-without privilege can access overview`(hasPrivilege: Boolean) {
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns 70L + if (hasPrivilege) 0 else 1
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns hasPrivilege

        every { projectReportPersistence.getReportByIdUnSecured(REPORT_ID) } returns report(null, PROJECT_ID)
        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns listOf()
        every { partnerCollaboratorPersistence.findUserIdsByProjectId(PROJECT_ID) } returns setOf()
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns setOf()

        assertThat(authorization.canViewReportVerificationFinance(REPORT_ID)).isEqualTo(hasPrivilege)
    }

    @ParameterizedTest(name = "controller user with-without privilege can access overview {0}")
    @ValueSource(booleans = [true, false])
    fun `controller user with-without privilege can access overview`(hasPrivilege: Boolean) {
        val userId = 70L + if (hasPrivilege) 0 else 1
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns userId
        every { currentUser.user.assignedProjects } returns setOf()
        every { currentUser.hasPermission(UserRolePermission.ProjectRetrieve) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns hasPrivilege

        every { projectReportPersistence.getReportByIdUnSecured(REPORT_ID) } returns report(null, PROJECT_ID)
        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns listOf()
        every { partnerCollaboratorPersistence.findUserIdsByProjectId(PROJECT_ID) } returns setOf()
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns setOf(userId)

        assertThat(authorization.canViewReportVerificationFinance(REPORT_ID)).isEqualTo(hasPrivilege)
    }

    @ParameterizedTest(name = "creator user can access overview when finalized {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["Finalized"])
    fun `creator user can access when finalized`(status: ProjectReportStatus) {
        val userId = 80L + status.ordinal
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns userId
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns false

        every { projectReportPersistence.getReportByIdUnSecured(REPORT_ID) } returns report(status, PROJECT_ID)
        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns listOf(
            collaboratorAssignedToProject(userId, ProjectCollaboratorLevel.EDIT)
        )
        every { partnerCollaboratorPersistence.findUserIdsByProjectId(PROJECT_ID) } returns setOf()
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns setOf()

        assertThat(authorization.canViewReportVerificationFinance(REPORT_ID)).isTrue()
    }


    @ParameterizedTest(name = "creator user can-not access when not-finalized {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["Finalized"], mode = EnumSource.Mode.EXCLUDE)
    fun `creator user can-not access when not-finalized`(status: ProjectReportStatus) {
        val userId = 90L + status.ordinal
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns userId
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns false

        every { projectReportPersistence.getReportByIdUnSecured(REPORT_ID) } returns report(status, PROJECT_ID)
        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns listOf(
            collaboratorAssignedToProject(userId, ProjectCollaboratorLevel.EDIT)
        )
        every { partnerCollaboratorPersistence.findUserIdsByProjectId(PROJECT_ID) } returns setOf()
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns setOf()
        assertThat(authorization.canViewReportVerificationFinance(REPORT_ID)).isFalse()
    }

    @ParameterizedTest(name = "hasPermissionForProjectReportId {0}")
    @ValueSource(booleans = [true, false])
    fun hasPermissionForProjectReportId(hasPrivilege: Boolean) {
        every { securityService.currentUser } returns currentUser
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns hasPrivilege
        every { projectReportPersistence.getReportByIdUnSecured(REPORT_ID) } returns report(null, PROJECT_ID)

        assertThat(authorization.hasPermissionForProjectReportId(UserRolePermission.ProjectReportingVerificationProjectView, REPORT_ID))
            .isEqualTo(hasPrivilege)
    }

    @Test
    fun `canEditVerificationDocuments - monitor`() {
        val userId = 120L
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns userId
        every { currentUser.user.assignedProjects } returns emptySet()
        every { currentUser.hasPermission(UserRolePermission.ProjectRetrieve) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit) } returns true

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns ProjectApplicantAndStatus(
            projectId = PROJECT_ID,
            applicantId = 85L,
            collaboratorEditIds = setOf(),
            collaboratorManageIds = setOf(85L),
            collaboratorViewIds = setOf(),
            projectStatus = ApplicationStatus.CONTRACTED
        )
        assertThat(authorization.canEditDocuments(PROJECT_ID)).isTrue
    }

    @Test
    fun `canViewVerificationDocuments - monitor`() {
        val userId = 120L
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns userId
        every { currentUser.user.assignedProjects } returns emptySet()
        every { currentUser.hasPermission(UserRolePermission.ProjectRetrieve) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns true

        every { userProjectCollaboratorPersistence.getLevelForProjectAndUser(PROJECT_ID, userId) } returns null
        every { partnerCollaboratorPersistence.findUserIdsByProjectId(PROJECT_ID) } returns setOf()
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns setOf()

        assertThat(authorization.canViewDocuments(PROJECT_ID)).isTrue
    }

    @Test
    fun `canViewVerificationDocuments - creator`() {
        val userId = 120L
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns userId
        every { currentUser.user.assignedProjects } returns emptySet()
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit) } returns false

        every { projectReportPersistence.getReportByIdUnSecured(REPORT_ID) } returns report(null, PROJECT_ID)
        every { userProjectCollaboratorPersistence.getLevelForProjectAndUser(PROJECT_ID, userId) } returns ProjectCollaboratorLevel.EDIT
        every { partnerCollaboratorPersistence.findUserIdsByProjectId(PROJECT_ID) } returns setOf()
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns setOf()

        assertThat(authorization.canViewDocuments(PROJECT_ID)).isTrue
    }

    @Test
    fun `canEditVerificationDocuments - creator`() {
        val userId = 120L
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns userId
        every { currentUser.user.assignedProjects } returns emptySet()
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit) } returns false

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns ProjectApplicantAndStatus(
            projectId = PROJECT_ID,
            applicantId = 85L,
            collaboratorEditIds = setOf(userId),
            collaboratorManageIds = setOf(85L),
            collaboratorViewIds = setOf(),
            projectStatus = ApplicationStatus.CONTRACTED
        )
        assertThat(authorization.canEditDocuments(PROJECT_ID)).isTrue
    }

    @Test
    fun `canViewVerificationDocuments - controller`() {
        val userId = 120L
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns userId
        every { currentUser.user.assignedProjects } returns emptySet()
        every { currentUser.hasPermission(UserRolePermission.ProjectRetrieve) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit) } returns true

        every { projectReportPersistence.getReportByIdUnSecured(REPORT_ID) } returns report(null, PROJECT_ID)
        every { userProjectCollaboratorPersistence.getLevelForProjectAndUser(PROJECT_ID, userId) } returns null
        every { partnerCollaboratorPersistence.findUserIdsByProjectId(PROJECT_ID) } returns setOf()
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns setOf(userId)

        assertThat(authorization.canViewDocuments(PROJECT_ID)).isTrue
    }

    @ParameterizedTest(name = "can-NOT-InteractWithVerificationDocuments {0}")
    @ValueSource(booleans = [true, false])
    fun `can-NOT-InteractWithVerificationDocuments`(isEdit: Boolean) {
        val userId = 120L
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns userId
        every { currentUser.user.assignedProjects } returns emptySet()
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit) } returns false

        every { projectReportPersistence.getReportByIdUnSecured(REPORT_ID) } returns report(null, PROJECT_ID)
        every { userProjectCollaboratorPersistence.getLevelForProjectAndUser(PROJECT_ID, userId) } returns null
        every { partnerCollaboratorPersistence.findUserIdsByProjectId(PROJECT_ID) } returns setOf()
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns setOf()
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns ProjectApplicantAndStatus(
            projectId = PROJECT_ID,
            applicantId = 85L,
            collaboratorEditIds = setOf(),
            collaboratorManageIds = setOf(101L),
            collaboratorViewIds = setOf(),
            projectStatus = ApplicationStatus.CONTRACTED
        )

        if (isEdit)
            assertThat(authorization.canEditDocuments(PROJECT_ID)).isFalse
        else
            assertThat(authorization.canViewDocuments(PROJECT_ID)).isFalse
    }

}
