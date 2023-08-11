package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class ProjectReportVerificationAuthorizationTest: UnitTest() {

    companion object {
        private const val REPORT_ID = 80L
        private const val PROJECT_ID = 90L

        private fun projectApplicantAndStatus(viewIds: Set<Long> = setOf(), editIds: Set<Long> = setOf()): ProjectApplicantAndStatus {
            return ProjectApplicantAndStatus(
                PROJECT_ID,
                applicantId = 2698L,
                projectStatus = ApplicationStatus.CONTRACTED,
                collaboratorManageIds = emptySet(),
                collaboratorEditIds = editIds,
                collaboratorViewIds = viewIds,
            )
        }
    }

    @MockK
    private lateinit var securityService: SecurityService

    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @MockK
    private lateinit var projectReportPersistence: ProjectReportPersistence

    @MockK
    private lateinit var authorizationUtilService: AuthorizationUtilService

    @MockK
    private lateinit var currentUser: CurrentUser

    @InjectMockKs
    private lateinit var reportVerificationAuthorization: ProjectReportVerificationAuthorization

    @BeforeEach
    fun resetMocks() {
        clearMocks(securityService, projectPersistence, projectReportPersistence, authorizationUtilService, currentUser)
    }

    @ParameterizedTest(name = "can monitor user access overview with privilege {0}")
    @ValueSource(booleans = [true, false])
    fun `monitor user with privilege can access overview`(hasPrivilege: Boolean) {
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns 99L
        every { currentUser.user } returns mockk()
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns hasPrivilege

        val projectReport = mockk<ProjectReportModel> {
            every { status } returns ProjectReportStatus.InVerification
        }
        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns projectReport
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns projectApplicantAndStatus()
        every { authorizationUtilService.userIsPartnerCollaboratorForProject(99L, PROJECT_ID)} returns false
        assertThat(reportVerificationAuthorization.canViewReportVerificationOverview(PROJECT_ID, REPORT_ID)).isEqualTo(hasPrivilege)
    }

    @ParameterizedTest(name = "can project collaborator user access overview - is partner collaborator {0}")
    @ValueSource(booleans = [true, false])
    fun `project collaborator user can see overview`(isPartnerCollaborator: Boolean) {
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns 99L
        every { currentUser.user } returns mockk()
        every { currentUser.user.assignedProjects } returns setOf()
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns false

        val projectReport = mockk<ProjectReportModel> {
            every { status } returns ProjectReportStatus.Finalized
        }
        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns projectReport
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns projectApplicantAndStatus(viewIds = setOf(99L))
        every { authorizationUtilService.userIsPartnerCollaboratorForProject(99L, PROJECT_ID)} returns isPartnerCollaborator
        assertThat(reportVerificationAuthorization.canViewReportVerificationOverview(PROJECT_ID, REPORT_ID)).isNotEqualTo(isPartnerCollaborator)
    }

    @ParameterizedTest(name = "can monitor user access communication {0}")
    @ValueSource(booleans = [true, false])
    fun `monitor user with privilege can access communication`(hasPrivilege: Boolean) {
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns 99L
        every { currentUser.user } returns mockk()
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns hasPrivilege

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns projectApplicantAndStatus()
        every { authorizationUtilService.userIsPartnerCollaboratorForProject(99L, PROJECT_ID)} returns false
        assertThat(reportVerificationAuthorization.canViewReportVerificationCommunication(PROJECT_ID)).isEqualTo(hasPrivilege)
    }

    @ParameterizedTest(name = "can project collaborator user access communication - is partner collaborator {0}")
    @ValueSource(booleans = [true, false])
    fun `project collaborator user can access communication`(isPartnerCollaborator: Boolean) {
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns 99L
        every { currentUser.user } returns mockk()
        every { currentUser.user.assignedProjects } returns setOf()
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns false

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns projectApplicantAndStatus(viewIds = setOf(99L))
        every { authorizationUtilService.userIsPartnerCollaboratorForProject(99L, PROJECT_ID)} returns isPartnerCollaborator
        assertThat(reportVerificationAuthorization.canViewReportVerificationCommunication(PROJECT_ID)).isNotEqualTo(isPartnerCollaborator)
    }

    @ParameterizedTest(name = "can monitor user access communication {0}")
    @ValueSource(booleans = [true, false])
    fun `monitor user with privilege can edit communication`(hasPrivilege: Boolean) {
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns 99L
        every { currentUser.user } returns mockk()
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit) } returns hasPrivilege

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns projectApplicantAndStatus()
        every { authorizationUtilService.userIsPartnerCollaboratorForProject(99L, PROJECT_ID)} returns false
        assertThat(reportVerificationAuthorization.canEditReportVerificationCommunication(PROJECT_ID)).isEqualTo(hasPrivilege)
    }

    @ParameterizedTest(name = "can project collaborator user access communication - is partner collaborator {0}")
    @ValueSource(booleans = [true, false])
    fun `project collaborator user can edit communication`(isPartnerCollaborator: Boolean) {
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns 99L
        every { currentUser.user } returns mockk()
        every { currentUser.user.assignedProjects } returns setOf()
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit) } returns false

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns projectApplicantAndStatus(editIds = setOf(99L))
        every { authorizationUtilService.userIsPartnerCollaboratorForProject(99L, PROJECT_ID)} returns isPartnerCollaborator
        assertThat(reportVerificationAuthorization.canEditReportVerificationCommunication(PROJECT_ID)).isNotEqualTo(isPartnerCollaborator)
    }

    @Test
    fun `canViewReportVerificationPrivilegedByReportId`() {
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns 99L
        every { currentUser.user } returns mockk()
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns true
        every { projectReportPersistence.getProjectIdForProjectReportId(REPORT_ID) } returns PROJECT_ID
        assertThat(reportVerificationAuthorization.canViewReportVerificationPrivilegedByReportId(REPORT_ID)).isEqualTo(true)
    }

    @Test
    fun `canEditReportVerificationPrivilegedByReportId`() {
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns 99L
        every { currentUser.user } returns mockk()
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit) } returns true
        every { projectReportPersistence.getProjectIdForProjectReportId(REPORT_ID) } returns PROJECT_ID
        assertThat(reportVerificationAuthorization.canEditReportVerificationPrivilegedByReportId(REPORT_ID)).isEqualTo(true)
    }

}
