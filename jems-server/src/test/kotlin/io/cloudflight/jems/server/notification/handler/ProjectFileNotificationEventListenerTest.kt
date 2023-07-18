package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.ZonedDateTime

class ProjectFileNotificationEventListenerTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 5L
        private const val PARTNER_ID = 88L
        private const val REPORT_ID = 99L

        private val summary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = 8L,
            callName = "call",
            acronym = "project acronym",
            status = ApplicationStatus.CONTRACTED,
        )

        private fun dummyFile(fileType: JemsFileType) = JemsFile(
            id = 478L,
            name = "attachment.pdf",
            type = fileType,
            uploaded = ZonedDateTime.now(),
            author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            description = "desc",
            indexedPath = "/PartnerControlReport/000099/"
        )

        private val reportSubmissionSummary = ProjectPartnerReportSubmissionSummary(
            id = REPORT_ID,
            reportNumber = 1,
            status = ReportStatus.InControl,
            version = "5.6.1",
            firstSubmission = ZonedDateTime.now(),
            controlEnd = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "01",
            projectAcronym = "project acronym",
            partnerAbbreviation = "LP-1",
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerId = PARTNER_ID
        )
    }

    @MockK
    lateinit var notificationProjectService: GlobalProjectNotificationServiceInteractor

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @InjectMockKs
    lateinit var listener: ProjectFileNotificationEventListener

    @BeforeEach
    internal fun reset() {
        clearAllMocks()
    }

    @ParameterizedTest(name = "Share folder file notification - {0}")
    @EnumSource(value = NotificationType::class, names = ["SharedFolderFileUpload", "SharedFolderFileDelete"])
    fun sharedFolderFileEventTest(type: NotificationType) {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(type, capture(slotVariable)) } answers { }
        val fileAction = if (type == NotificationType.SharedFolderFileUpload) FileChangeAction.Upload else FileChangeAction.Delete
        listener.sendNotifications(
            ProjectFileChangeEvent(
                fileAction,
                summary,
                dummyFile(JemsFileType.SharedFolder),
                overrideAuthorEmail = "test@user.com"
            )
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(type, any()) }
        assertThat(slotVariable.captured).contains(
            Assertions.entry(NotificationVariable.ProjectId, PROJECT_ID),
            Assertions.entry(NotificationVariable.ProjectIdentifier, "01"),
            Assertions.entry(NotificationVariable.ProjectAcronym, "project acronym"),
            Assertions.entry(NotificationVariable.FileUsername, "test@user.com"),
            Assertions.entry(NotificationVariable.FileName, "attachment.pdf"),
        )
    }

    @ParameterizedTest(name = "Control communication file notification - {0}")
    @EnumSource(value = NotificationType::class, names = ["ControlCommunicationFileUpload", "ControlCommunicationFileDelete"])
    fun controlCommunicationFileEventTest(type: NotificationType) {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(type, capture(slotVariable)) } answers { }
        every { reportPersistence.getPartnerReportByIdUnsecured(REPORT_ID) } answers { reportSubmissionSummary }

        val fileAction = if (type == NotificationType.ControlCommunicationFileUpload) FileChangeAction.Upload else FileChangeAction.Delete
        listener.sendNotifications(
            ProjectFileChangeEvent(
                fileAction,
                summary,
                dummyFile(JemsFileType.ControlDocument),
                overrideAuthorEmail = "test@user.com"
            )
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(type, any()) }
        assertThat(slotVariable.captured).contains(
            Assertions.entry(NotificationVariable.ProjectId, PROJECT_ID),
            Assertions.entry(NotificationVariable.ProjectIdentifier, "01"),
            Assertions.entry(NotificationVariable.ProjectAcronym, "project acronym"),
            Assertions.entry(NotificationVariable.FileUsername, "test@user.com"),
            Assertions.entry(NotificationVariable.FileName, "attachment.pdf"),
            Assertions.entry(NotificationVariable.PartnerId, 88L),
            Assertions.entry(NotificationVariable.PartnerRole, ProjectPartnerRole.LEAD_PARTNER),
            Assertions.entry(NotificationVariable.PartnerNumber, 1),
            Assertions.entry(NotificationVariable.PartnerAbbreviation, "LP-1"),
            Assertions.entry(NotificationVariable.PartnerReportId, 99L),
            Assertions.entry(NotificationVariable.PartnerReportNumber, 1),
        )
    }
}
