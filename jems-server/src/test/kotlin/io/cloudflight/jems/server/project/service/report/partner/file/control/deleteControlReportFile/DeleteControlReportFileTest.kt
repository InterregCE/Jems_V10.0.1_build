package io.cloudflight.jems.server.project.service.report.partner.file.control.deleteControlReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.partner.file.control.ControlReportFileAuthorizationService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteControlReportFileTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 420L
    }

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var authorization: ControlReportFileAuthorizationService

    @InjectMockKs
    lateinit var interactor: DeleteControlReportFile

    @BeforeEach
    fun reset() {
        clearMocks(reportFilePersistence)
        clearMocks(authorization)
    }

    @Test
    fun delete() {
        val reportId = 92L
        val fileId = 15L

        every { authorization.validateChangeToFileAllowed(PARTNER_ID, reportId, fileId) } answers { }
        every { reportFilePersistence.deleteFile(PARTNER_ID, fileId) } answers { }

        interactor.delete(PARTNER_ID, reportId = reportId, fileId)
        verify(exactly = 1) { authorization.validateChangeToFileAllowed(PARTNER_ID, reportId, fileId) }
        verify(exactly = 1) { reportFilePersistence.deleteFile(PARTNER_ID, fileId) }
    }

}
