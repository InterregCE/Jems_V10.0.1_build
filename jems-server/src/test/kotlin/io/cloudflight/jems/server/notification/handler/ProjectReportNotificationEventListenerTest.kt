package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserSettings
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Map.entry

class ProjectReportNotificationEventListenerTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 2L
        private const val PROJECT_REPORT_ID = 4L
        private const val PROJECT_REPORT_NUMBER = 7

        private fun projectReportSummary(status: ProjectReportStatus) = ProjectReportSubmissionSummary(
            id = PROJECT_REPORT_ID,
            reportNumber = PROJECT_REPORT_NUMBER,
            status = status,
            version = "1.0",
            firstSubmission = ZonedDateTime.now().minusDays(1),
            createdAt = ZonedDateTime.now(),

            projectId = PROJECT_ID,
            projectIdentifier = "01",
            projectAcronym = "project acronym",
            periodNumber = 1
        )

        private val user = User(
            id = 10L,
            email = "x@y",
            userSettings = UserSettings(sendNotificationsToEmail = false),
            name = "",
            surname = "",
            userRole = UserRole(0, "", permissions = emptySet(), isDefault = false),
            userStatus = UserStatus.ACTIVE
        )

        private val currentUser = LocalCurrentUser(
            user, "hash_pass",
            listOf(SimpleGrantedAuthority(UserRolePermission.CallRetrieve.key))
        )
    }

    @MockK
    private lateinit var notificationProjectService: GlobalProjectNotificationServiceInteractor

    @MockK
    private lateinit var projectPersistence : ProjectPersistence

    @MockK
    private lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var listener: ProjectReportNotificationEventListener

    @BeforeEach
    internal fun reset() {
        clearAllMocks()
    }

    @ParameterizedTest(name = "send ProjectReport - {0} notification")
    @CsvSource(value = [
        "Draft,Submitted,ProjectReportSubmitted",
        "Submitted,ReOpenSubmittedLast,ProjectReportReOpen",
        "Submitted,ReOpenSubmittedLimited,ProjectReportReOpen",
        "Submitted,VerificationReOpenedLast,ProjectReportReOpen",
        "Submitted,VerificationReOpenedLimited,ProjectReportReOpen",
        "Submitted,VerificationReOpenedLimited,ProjectReportReOpen",
        "VerificationReOpenedLast,InVerification,ProjectReportSubmitted",
        "VerificationReOpenedLimited,InVerification,ProjectReportSubmitted",
        "Submitted,InVerification,ProjectReportVerificationOngoing",
        "InVerification,Finalized,ProjectReportVerificationFinalized",
        "VerificationReOpenedLast,ReOpenFinalized,ProjectReportSubmitted",
        "VerificationReOpenedLimited,ReOpenFinalized,ProjectReportSubmitted",
        "Finalized,ReOpenFinalized,ProjectReportVerificationReOpen",
    ])
    fun sendProjectReportNotification(statusFrom: ProjectReportStatus, statusTo: ProjectReportStatus, expectedNotifType: NotificationType) {
        val slotType = slot<NotificationType>()
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(capture(slotType), capture(slotVariable)) } answers { }
        every { securityService.currentUser } returns currentUser
        every { projectPersistence.getProjectPeriods(PROJECT_ID, "1.0") } returns listOf(
            ProjectPeriod(1, 1, 1)
        )

        val projectReportSummary = projectReportSummary(statusTo)

        listener.sendNotifications(
            ProjectReportStatusChanged(mockk(), projectReportSummary, statusFrom)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(any(), any()) }
        assertThat(slotType.captured).isEqualTo(expectedNotifType)
        assertThat(slotVariable.captured).containsExactly(
            entry(NotificationVariable.ProjectId, PROJECT_ID),
            entry(NotificationVariable.ProjectIdentifier, "01"),
            entry(NotificationVariable.ProjectAcronym, "project acronym"),
            entry(NotificationVariable.ProjectReportId, PROJECT_REPORT_ID),
            entry(NotificationVariable.ProjectReportNumber, 7),
            entry(NotificationVariable.UserName, user.email),
            entry(NotificationVariable.ReportingPeriodNumber, 1),
            entry(NotificationVariable.ReportingPeriodStart, 1),
            entry(NotificationVariable.ReportingPeriodEnd, 1),

            )
    }
}
