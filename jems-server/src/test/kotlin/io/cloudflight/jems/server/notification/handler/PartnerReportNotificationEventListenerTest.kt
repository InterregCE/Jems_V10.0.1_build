package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProjectBase
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
import org.junit.jupiter.params.provider.EnumSource
import java.time.ZonedDateTime

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

    @ParameterizedTest(name = "send PartnerReport - {0} notification")
    @EnumSource(value = ReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun sendPartnerReportNotification(partnerReportStatus: ReportStatus) {
        val slotProject = slot<NotificationProjectBase>()
        every { notificationProjectService.sendNotifications(any(), capture(slotProject), *anyVararg()) } answers { }

        val partnerReportSummary = partnerReportSummary(partnerReportStatus)
        listener.sendNotifications(
            PartnerReportStatusChanged(mockk(), projectSummary(), partnerReportSummary)
        )

        val varArgs = mutableListOf<Variable>()
        verify(exactly = 1) {
            notificationProjectService.sendNotifications(
                partnerReportStatus.toNotificationType()!!,
                any(),
                *varargAll { varArgs.add(it); true })
        }
        assertThat(slotProject.captured).isEqualTo(NotificationProjectBase(PROJECT_ID, "01", "project acronym"))
        assertThat(varArgs).contains(Variable("partnerId", PARTNER_ID))
        assertThat(varArgs).contains(Variable("partnerRole", ProjectPartnerRole.LEAD_PARTNER))
        assertThat(varArgs).contains(Variable("partnerNumber", PARTNER_NUMBER))
    }
}
