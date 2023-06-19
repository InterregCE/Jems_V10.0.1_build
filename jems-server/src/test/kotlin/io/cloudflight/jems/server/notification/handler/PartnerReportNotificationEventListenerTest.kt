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
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.Map.entry

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
    }

    @MockK
    private lateinit var notificationProjectService: GlobalProjectNotificationServiceInteractor

    @InjectMockKs
    lateinit var listener: PartnerReportNotificationEventListener

    @BeforeEach
    internal fun reset() {
        clearAllMocks()
    }

    @Test
    fun sendDraftToSubmittedPartnerReportNotification() {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val partnerReportSummary = partnerReportSummary(ReportStatus.Submitted)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary, ReportStatus.Draft)
        )
        val notificationType = ReportStatus.Submitted.toNotificationType(ReportStatus.Draft)!!
        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }

        assertThat(notificationType).isEqualTo(NotificationType.PartnerReportSubmitted)
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

    @Test
    fun sendSubmittedToReopenPartnerReportNotification() {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val partnerReportSummary = partnerReportSummary(ReportStatus.ReOpenSubmittedLast)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary, ReportStatus.Submitted)
        )
        val notificationType = ReportStatus.ReOpenSubmittedLast.toNotificationType(ReportStatus.Submitted)!!
        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }

        assertThat(notificationType).isEqualTo(NotificationType.PartnerReportReOpen)
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

    @Test
    fun sendReopenToSubmittedPartnerReportNotification() {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val partnerReportSummary = partnerReportSummary(ReportStatus.Submitted)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary, ReportStatus.ReOpenSubmittedLimited)
        )
        val notificationType = ReportStatus.Submitted.toNotificationType(ReportStatus.ReOpenSubmittedLimited)!!
        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }

        assertThat(notificationType).isEqualTo(NotificationType.PartnerReportSubmitted)
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

    @Test
    fun sendSubmittedToInControlPartnerReportNotification() {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val partnerReportSummary = partnerReportSummary(ReportStatus.InControl)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary, ReportStatus.Submitted)
        )
        val notificationType = ReportStatus.InControl.toNotificationType(ReportStatus.Submitted)!!
        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }

        assertThat(notificationType).isEqualTo(NotificationType.PartnerReportControlOngoing)
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

    @Test
    fun sendInControlToReopenPartnerReportNotification() {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val partnerReportSummary = partnerReportSummary(ReportStatus.ReOpenInControlLast)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary, ReportStatus.InControl)
        )
        val notificationType = ReportStatus.ReOpenInControlLast.toNotificationType(ReportStatus.InControl)!!
        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }

        assertThat(notificationType).isEqualTo(NotificationType.PartnerReportReOpen)
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

    @Test
    fun sendReopenToInControlPartnerReportNotification() {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val partnerReportSummary = partnerReportSummary(ReportStatus.InControl)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary, ReportStatus.ReOpenInControlLast)
        )
        val notificationType = ReportStatus.InControl.toNotificationType(ReportStatus.ReOpenInControlLast)!!
        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }

        assertThat(notificationType).isEqualTo(NotificationType.PartnerReportSubmitted)
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

    @Test
    fun sendInControlToCertifiedPartnerReportNotification() {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val partnerReportSummary = partnerReportSummary(ReportStatus.Certified)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary, ReportStatus.InControl)
        )
        val notificationType = ReportStatus.Certified.toNotificationType(ReportStatus.InControl)!!
        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }

        assertThat(notificationType).isEqualTo(NotificationType.PartnerReportCertified)
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

    @Test
    fun sendCertifiedToControlReopenPartnerReportNotification() {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val partnerReportSummary = partnerReportSummary(ReportStatus.ReOpenCertified)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary, ReportStatus.Certified)
        )
        val notificationType = ReportStatus.ReOpenCertified.toNotificationType(ReportStatus.Certified)!!
        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }

        assertThat(notificationType).isEqualTo(NotificationType.PartnerReportReOpenCertified)
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

    @Test
    fun sendControlReopenToReopenPartnerReportNotification() {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val partnerReportSummary = partnerReportSummary(ReportStatus.ReOpenInControlLast)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary, ReportStatus.ReOpenCertified)
        )
        val notificationType = ReportStatus.ReOpenInControlLast.toNotificationType(ReportStatus.ReOpenCertified)!!
        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }

        assertThat(notificationType).isEqualTo(NotificationType.PartnerReportReOpen)
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

    @Test
    fun sendReopenToControlReopenPartnerReportNotification() {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val partnerReportSummary = partnerReportSummary(ReportStatus.ReOpenCertified)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary, ReportStatus.ReOpenInControlLast)
        )
        val notificationType = ReportStatus.ReOpenCertified.toNotificationType(ReportStatus.ReOpenInControlLast)!!
        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }

        assertThat(notificationType).isEqualTo(NotificationType.PartnerReportSubmitted)
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

    @Test
    fun sendControlReopenToCertifiedPartnerReportNotification() {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        val partnerReportSummary = partnerReportSummary(ReportStatus.Certified)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary, ReportStatus.ReOpenCertified)
        )
        val notificationType = ReportStatus.Certified.toNotificationType(ReportStatus.ReOpenCertified)!!
        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }

        assertThat(notificationType).isEqualTo(NotificationType.PartnerReportCertified)
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
