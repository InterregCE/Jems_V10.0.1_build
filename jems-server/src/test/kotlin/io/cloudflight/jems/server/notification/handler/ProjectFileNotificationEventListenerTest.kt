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
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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

        private fun report(reportId: Long) = ProjectPartnerReportSubmissionSummary(
            id = reportId,
            reportNumber = 4,
            status = ReportStatus.Draft,
            version = "1.0",
            firstSubmission = ZonedDateTime.now(),
            controlEnd = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "PROJ_01",
            projectAcronym = "PROJ acr",
            partnerAbbreviation = "LP-6",
            partnerNumber = 6,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerId = 1357L,
        )

        private val project: ProjectSummary = mockk<ProjectSummary>().also {
            every { it.id } returns 45L
            every { it.customIdentifier } returns "custom identifier 45"
            every { it.acronym } returns "project acronym 45"
        }

        private fun file(type: JemsFileType, indexedPath: String): JemsFile = mockk<JemsFile>().also {
            every { it.name } returns "attachment.txt"
            every { it.type } returns type
            every { it.author.email } returns "author@email"
            every { it.indexedPath } returns indexedPath
        }
    }

    @MockK
    private lateinit var notificationProjectService: GlobalProjectNotificationServiceInteractor

    @MockK
    private lateinit var reportPersistence: ProjectPartnerReportPersistence

    @InjectMockKs
    private lateinit var listener: ProjectFileNotificationEventListener

    @BeforeEach
    internal fun reset() {
        clearMocks(notificationProjectService, reportPersistence)
    }

    @ParameterizedTest(name = "sendNotifications - {0}, {1}")
    @CsvSource(value = [
        "ControlDocument,Upload,1544,ControlCommunicationFileUpload,Project/017442/Report/Partner/009679/PartnerControlReport/001544/ControlDocument/",
        "ControlCertificate,Delete,1620,ControlCommunicationFileDelete,Project/017596/Report/Partner/009784/PartnerControlReport/001620/ControlCertificate/",
        "ControlReport,Upload,1556,ControlCommunicationFileUpload,Project/017442/Report/Partner/009679/PartnerControlReport/001556/ControlReport/",
        "SharedFolder,Upload,,SharedFolderFileUpload,Project/017376/SharedFolder/",
        "SharedFolder,Delete,,SharedFolderFileDelete,Project/017376/SharedFolder/",
    ])
    fun sendNotifications(type: JemsFileType, action: FileChangeAction, reportId: Long?, expectedType: NotificationType, path: String) {
        if (reportId != null)
            every { reportPersistence.getPartnerReportByIdUnsecured(reportId) } returns report(reportId)

        val slotType = slot<NotificationType>()
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(capture(slotType), capture(slotVariable)) } answers { }

        listener.sendNotifications(
            ProjectFileChangeEvent(
                action = action,
                projectSummary = project,
                file = file(type, path),
                overrideAuthorEmail = if (action == FileChangeAction.Delete) "override-email" else null,
            )
        )

        assertThat(slotType.captured).isEqualTo(expectedType)

        val expectedVariables = mutableMapOf<NotificationVariable, Any>(
            NotificationVariable.ProjectId to 45L,
            NotificationVariable.ProjectIdentifier to "custom identifier 45",
            NotificationVariable.ProjectAcronym to "project acronym 45",
            NotificationVariable.FileUsername to if (action == FileChangeAction.Delete) "override-email" else "author@email",
            NotificationVariable.FileName to "attachment.txt",
        )
        if (reportId != null)
            expectedVariables.putAll(mapOf(
                NotificationVariable.PartnerId to 1357L,
                NotificationVariable.PartnerRole to ProjectPartnerRole.LEAD_PARTNER,
                NotificationVariable.PartnerNumber to 6,
                NotificationVariable.PartnerAbbreviation to "LP-6",
                NotificationVariable.PartnerReportId to reportId,
                NotificationVariable.PartnerReportNumber to 4,
            ))

        assertThat(slotVariable.captured).containsExactlyEntriesOf(expectedVariables)
    }

    @ParameterizedTest(name = "do not send notification for wrong file type - {0}")
    @EnumSource(value = JemsFileType::class, names = ["ControlDocument", "ControlCertificate", "ControlReport", "SharedFolder"], mode = EnumSource.Mode.EXCLUDE)
    fun `do not send notification for wrong file type`(type: JemsFileType) {
        val file = mockk<JemsFile>()
        every { file.type } returns type
        listener.sendNotifications(ProjectFileChangeEvent(FileChangeAction.Upload, mockk(), file))
        listener.sendNotifications(ProjectFileChangeEvent(FileChangeAction.Delete, mockk(), file))

        verify(exactly = 0) { notificationProjectService.sendNotifications(any(), any()) }
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
        assertThat(slotVariable.captured).containsExactly(
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
        assertThat(slotVariable.captured).containsExactly(
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
