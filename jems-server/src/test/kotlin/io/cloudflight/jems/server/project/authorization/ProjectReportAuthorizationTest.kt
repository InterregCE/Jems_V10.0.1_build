package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectReportingProjectEdit
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectReportingProjectView
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource

internal class ProjectReportAuthorizationTest : UnitTest() {

    @MockK
    private lateinit var securityService: SecurityService

    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @MockK
    private lateinit var currentUser: CurrentUser

    @InjectMockKs
    private lateinit var reportAuthorization: ProjectReportAuthorization

    @BeforeEach
    fun resetMocks() {
        clearMocks(securityService, reportPersistence, projectPersistence)
        clearMocks(currentUser)
        every { securityService.currentUser } returns currentUser
    }

    @ParameterizedTest(name = "canEditReportNotSpecific - monitor {0} - creator {1}")
    @CsvSource(value = ["true,true", "true,false", "false,true"])
    fun `canEditReportNotSpecific - true (all combinations)`(isMonitor: Boolean, isCreator: Boolean) {
        val projectId = 41L
        val userId = 410L

        mockEdit(projectId, userId, isMonitor, isCreator)

        assertThat(reportAuthorization.canEditReportNotSpecific(projectId)).isTrue()
    }

    @Test
    fun `canEditReportNotSpecific - monitor false - creator false`() {
        val projectId = 42L
        val userId = 420L

        mockEdit(projectId, userId, isMonitor = false, isCreator = false)

        assertThat(reportAuthorization.canEditReportNotSpecific(projectId)).isFalse()
    }

    @ParameterizedTest(name = "canEditReport - wrong status")
    @EnumSource(value = ProjectReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun `canEditReport - wrong status`(status: ProjectReportStatus) {
        val projectId = 43L
        val reportId = 114L + status.ordinal

        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        every { reportPersistence.getReportById(projectId, reportId = reportId) } returns report

        assertThat(reportAuthorization.canEditReport(projectId, reportId)).isFalse()
    }

    @ParameterizedTest(name = "canEditReportNotSpecific - monitor {0} - creator {1}")
    @CsvSource(value = ["true,true", "true,false", "false,true"])
    fun `canEditReport - true (all combinations)`(isMonitor: Boolean, isCreator: Boolean) {
        val projectId = 44L
        val reportId = 144L
        val userId = 440L

        val report = mockk<ProjectReportModel>()
        every { report.status } returns ProjectReportStatus.Draft
        every { reportPersistence.getReportById(projectId, reportId = reportId) } returns report

        mockEdit(projectId, userId, isMonitor, isCreator)

        assertThat(reportAuthorization.canEditReportNotSpecific(projectId)).isTrue()
    }

    @ParameterizedTest(name = "canViewReportNotSpecific - monitor {0} - creator {1}")
    @CsvSource(value = ["true,true", "true,false", "false,true"])
    fun `canViewReport - true (all combinations)`(isMonitor: Boolean, isCreator: Boolean) {
        val projectId = 45L
        val userId = 450L

        mockView(projectId, userId, isMonitor, isCreator)

        assertThat(reportAuthorization.canViewReport(projectId)).isTrue()
    }

    @Test
    fun `canViewReport - monitor false - creator false`() {
        val projectId = 46L
        val userId = 460L

        mockView(projectId, userId, isMonitor = false, isCreator = false)

        assertThat(reportAuthorization.canViewReport(projectId)).isFalse()
    }

    @ParameterizedTest(name = "canCreate - monitor {0} - creator {1}")
    @CsvSource(value = ["true,false", "false,true"])
    fun `canCreateReport - all true`(isMonitor: Boolean, isCreator: Boolean) {
        val projectId = 217L
        val userId = 763L

        mockEdit(projectId, userId, isMonitor, isCreator)

        assertThat(reportAuthorization.canEditReportNotSpecific(projectId)).isTrue()
    }
    @Test
    fun `canCreateReport - monitor false - creator false`() {
        val projectId = 42L
        val userId = 79L
        mockEdit(projectId, userId, isMonitor = false, isCreator = false)
        assertThat(reportAuthorization.canCreateProjectReport(projectId)).isFalse()
    }

    private fun mockEdit(projectId: Long, userId: Long, isMonitor: Boolean, isCreator: Boolean) {
        every { currentUser.hasPermission(ProjectReportingProjectEdit) } returns isMonitor
        every { currentUser.user.assignedProjects } returns if (isMonitor) setOf(projectId) else emptySet()

        val project = mockk<ProjectApplicantAndStatus>()
        every { securityService.getUserIdOrThrow() } returns userId
        every { project.getUserIdsWithEditLevel() } returns if (isCreator) setOf(userId) else emptySet()
        every { projectPersistence.getApplicantAndStatusById(projectId) } returns project
    }

    private fun mockView(projectId: Long, userId: Long, isMonitor: Boolean, isCreator: Boolean) {
        every { currentUser.hasPermission(ProjectReportingProjectView) } returns isMonitor
        every { currentUser.user.assignedProjects } returns if (isMonitor) setOf(projectId) else emptySet()

        val project = mockk<ProjectApplicantAndStatus>()
        every { securityService.getUserIdOrThrow() } returns userId
        every { project.getUserIdsWithViewLevel() } returns if (isCreator) setOf(userId) else emptySet()
        every { projectPersistence.getApplicantAndStatusById(projectId) } returns project
    }
}
