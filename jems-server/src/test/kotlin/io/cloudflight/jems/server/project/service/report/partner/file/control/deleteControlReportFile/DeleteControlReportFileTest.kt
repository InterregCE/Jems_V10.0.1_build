package io.cloudflight.jems.server.project.service.report.partner.file.control.deleteControlReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.file.control.ControlReportFileAuthorizationService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class DeleteControlReportFileTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 420L
        private const val PROJECT_ID = 99L

        private  val dummyFile = JemsFile(
            id = 15L,
            name = "attachment.pdf",
            type = JemsFileType.ControlDocument,
            uploaded = ZonedDateTime.now(),
            author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            description = "desc",
            indexedPath = ""
        )

        private val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = 1L,
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.CONTRACTED
        )
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var authorization: ControlReportFileAuthorizationService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var interactor: DeleteControlReportFile

    @BeforeEach
    fun reset() {
        clearMocks(filePersistence)
        clearMocks(authorization)
    }

    @Test
    fun delete() {
        val reportId = 92L
        val fileId = 15L

        every { authorization.validateChangeToFileAllowed(PARTNER_ID, reportId, fileId, any()) } answers { }
        every { filePersistence.deleteFile(PARTNER_ID, fileId) } answers { }
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } answers { PROJECT_ID }
        every { filePersistence.getFile(fileId, PROJECT_ID) } answers { dummyFile }
        every { projectPersistence.getProjectSummary(PROJECT_ID) } answers { projectSummary }
        every { securityService.currentUser!!.user.email } returns "test@email.com"
        every { auditPublisher.publishEvent(ofType(ProjectFileChangeEvent::class)) } returns Unit

        val auditSlot = slot<ProjectFileChangeEvent>()
        interactor.delete(PARTNER_ID, reportId = reportId, fileId)
        verify(exactly = 1) { authorization.validateChangeToFileAllowed(PARTNER_ID, reportId, fileId, true) }
        verify(exactly = 1) { filePersistence.deleteFile(PARTNER_ID, fileId) }
        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
    }

}
