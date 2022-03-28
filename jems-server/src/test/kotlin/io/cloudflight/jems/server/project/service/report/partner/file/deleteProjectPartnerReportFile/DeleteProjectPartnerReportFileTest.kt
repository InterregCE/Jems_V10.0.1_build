package io.cloudflight.jems.server.project.service.report.partner.file.deleteProjectPartnerReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteProjectPartnerReportFileTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 420L
    }

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @InjectMockKs
    lateinit var interactor: DeleteProjectPartnerReportFile
    @BeforeEach

    fun reset() {
        clearMocks(reportFilePersistence)
    }

    @Test
    fun delete() {
        every { reportFilePersistence.existsFile(PARTNER_ID, 82L) } returns true
        every { reportFilePersistence.deleteFile(PARTNER_ID, 82L) } answers { }

        interactor.delete(PARTNER_ID, 82L)
        verify(exactly = 1) { reportFilePersistence.deleteFile(PARTNER_ID, 82L) }
    }

    @Test
    fun `delete - not existing`() {
        every { reportFilePersistence.existsFile(PARTNER_ID, -1L) } returns false
        assertThrows<FileNotFound> { interactor.delete(PARTNER_ID, -1L) }
        verify(exactly = 0) { reportFilePersistence.deleteFile(any(), any()) }
    }

}
