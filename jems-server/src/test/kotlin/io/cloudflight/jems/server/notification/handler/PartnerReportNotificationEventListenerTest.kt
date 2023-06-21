package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
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
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.ZonedDateTime
import java.util.Map.entry
import java.util.stream.Stream

class PartnerReportNotificationEventListenerTest : UnitTest() {

    companion object {
        private const val CALL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val PARTNER_ID = 3L
        private const val PARTNER_NUMBER = 7
        private const val PARTNER_REPORT_ID = 4L

        private fun projectSummary() = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = CALL_ID,
            callName = "call",
            acronym = "project acronym",
            status = ApplicationStatus.CONTRACTED,
        )

        private fun partnerReportSummary(status: ReportStatus) = ProjectPartnerReportSubmissionSummary(
            id = PARTNER_REPORT_ID,
            reportNumber = 1,
            status = status,
            version = "1.0",
            firstSubmission = ZonedDateTime.now().minusDays(1),
            controlEnd = ZonedDateTime.now().plusDays(1),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "01",
            projectAcronym = "project acronym",
            partnerAbbreviation = "LP-7",
            partnerNumber = PARTNER_NUMBER,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerId = PARTNER_ID,
        )

        @JvmStatic
        fun parameterizedTestValues(): Stream<Arguments> = Stream.of(
            Arguments.of(ReportStatus.Submitted, ReportStatus.Draft, NotificationType.PartnerReportSubmitted),
            Arguments.of(ReportStatus.ReOpenSubmittedLast, ReportStatus.Submitted, NotificationType.PartnerReportReOpen),
            Arguments.of(ReportStatus.Submitted, ReportStatus.ReOpenSubmittedLimited, NotificationType.PartnerReportSubmitted),
            Arguments.of(ReportStatus.InControl, ReportStatus.Submitted, NotificationType.PartnerReportControlOngoing),
            Arguments.of(ReportStatus.ReOpenInControlLast, ReportStatus.InControl, NotificationType.PartnerReportReOpen),
            Arguments.of(ReportStatus.InControl, ReportStatus.ReOpenInControlLast, NotificationType.PartnerReportSubmitted),
            Arguments.of(ReportStatus.Certified, ReportStatus.InControl, NotificationType.PartnerReportCertified),
            Arguments.of(ReportStatus.ReOpenCertified, ReportStatus.Certified, NotificationType.PartnerReportReOpenCertified),
            Arguments.of(ReportStatus.ReOpenInControlLast, ReportStatus.ReOpenCertified, NotificationType.PartnerReportReOpen),
            Arguments.of(ReportStatus.ReOpenCertified, ReportStatus.ReOpenInControlLast, NotificationType.PartnerReportSubmitted),
            Arguments.of(ReportStatus.Certified, ReportStatus.ReOpenCertified, NotificationType.PartnerReportCertified),
        )!!
    }

    @MockK
    private lateinit var notificationProjectService: GlobalProjectNotificationServiceInteractor

    @InjectMockKs
    lateinit var listener: PartnerReportNotificationEventListener

    @BeforeEach
    internal fun reset() {
        clearAllMocks()
    }

    @ParameterizedTest
    @MethodSource("parameterizedTestValues")
    fun testPartnerReportNotification(
        currentStatus: ReportStatus,
        previousStatus: ReportStatus,
        expectedNotificationType: NotificationType
    ) {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val partnerReportSummary = partnerReportSummary(currentStatus)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary, previousStatus)
        )
        val notificationType = currentStatus.toNotificationType(previousStatus)!!
        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }

        assertThat(notificationType).isEqualTo(expectedNotificationType)
        assertThat(slotVariable.captured).containsExactly(
            entry(NotificationVariable.ProjectId, PROJECT_ID),
            entry(NotificationVariable.ProjectIdentifier, "01"),
            entry(NotificationVariable.ProjectAcronym, "project acronym"),
            entry(NotificationVariable.PartnerId, PARTNER_ID),
            entry(NotificationVariable.PartnerRole, ProjectPartnerRole.LEAD_PARTNER),
            entry(NotificationVariable.PartnerNumber, PARTNER_NUMBER),
            entry(NotificationVariable.PartnerAbbreviation, "LP-7"),
            entry(NotificationVariable.PartnerReportId, PARTNER_REPORT_ID),
            entry(NotificationVariable.PartnerReportNumber, 1),
        )
    }

}
