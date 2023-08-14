package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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
        private fun collaborator(userId: Long): CollaboratorAssignedToProject {
            val collaborator = mockk<CollaboratorAssignedToProject>()
            every { collaborator.userId } returns userId
            return collaborator
        }

    }

    @MockK private lateinit var projectReportPersistence: ProjectReportPersistence
    @MockK private lateinit var userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence
    @MockK private lateinit var securityService: SecurityService

    @MockK private lateinit var currentUser: CurrentUser

    @InjectMockKs private lateinit var authorization: ProjectReportVerificationAuthorization

    @BeforeEach
    fun resetMocks() {
        clearMocks(projectReportPersistence, userProjectCollaboratorPersistence, securityService, currentUser)
    }

    @ParameterizedTest(name = "monitor user with-without privilege can access overview {0}")
    @ValueSource(booleans = [true, false])
    fun `monitor user with-without privilege can access overview`(hasPrivilege: Boolean) {
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns 70L + if (hasPrivilege) 0 else 1
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns hasPrivilege

        every { projectReportPersistence.getReportByIdUnSecured(REPORT_ID) } returns report(null, PROJECT_ID)
        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns emptyList()
        assertThat(authorization.canViewReportVerificationFinance(REPORT_ID)).isEqualTo(hasPrivilege)
    }

    @ParameterizedTest(name = "creator user can access when finalized {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["Finalized"])
    fun `creator user can access when finalized`(status: ProjectReportStatus) {
        val userId = 80L + status.ordinal
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns userId
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns false

        every { projectReportPersistence.getReportByIdUnSecured(REPORT_ID) } returns report(status, PROJECT_ID)
        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns listOf(collaborator(userId))
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
        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns listOf(collaborator(userId))
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

    @ParameterizedTest(name = "canInteractWithVerificationDocuments - monitor {0} - {1}")
    @CsvSource(value = [
        "false,ProjectReportingVerificationProjectView,true",
        "true,ProjectReportingVerificationProjectEdit,true",
        "false,ProjectReportingVerificationProjectView,false",
        "true,ProjectReportingVerificationProjectEdit,false",
    ])
    fun `canInteractWithVerificationDocuments - monitor`(isEdit: Boolean, perm: UserRolePermission, hasPrivilege: Boolean) {
        val userId = 110L
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns userId
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        every { currentUser.hasPermission(perm) } returns hasPrivilege

        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns emptyList()
        assertThat(authorization.canInteractWithVerificationDocuments(PROJECT_ID, isEdit)).isEqualTo(hasPrivilege)
    }

    @ParameterizedTest(name = "canInteractWithVerificationDocuments - creator {0}")
    @ValueSource(booleans = [true, false])
    fun `canInteractWithVerificationDocuments - creator`(isEdit: Boolean) {
        val userId = 120L
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns userId
        every { currentUser.user.assignedProjects } returns emptySet()
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectView) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit) } returns false

        every { projectReportPersistence.getReportByIdUnSecured(REPORT_ID) } returns report(null, PROJECT_ID)
        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns listOf(collaborator(userId))
        assertThat(authorization.canInteractWithVerificationDocuments(PROJECT_ID, isEdit)).isTrue()
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
        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns emptyList()
        assertThat(authorization.canInteractWithVerificationDocuments(PROJECT_ID, isEdit)).isFalse()
    }

}
