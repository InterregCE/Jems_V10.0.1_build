package io.cloudflight.jems.server.project.service.report.partner.file.deleteProjectPartnerReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteProjectPartnerReportFileTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 420L
    }

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @InjectMockKs
    lateinit var interactor: DeleteProjectPartnerReportFile
    @BeforeEach

    fun reset() {
        clearMocks(partnerPersistence)
        clearMocks(reportFilePersistence)
    }

    @Test
    fun delete() {
        val projectId = 96L
        val fileId = 10L
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns projectId
        val searchIndexSlot = slot<String>()
        every { reportFilePersistence.existsFile(PARTNER_ID, capture(searchIndexSlot), fileId) } returns true
        every { reportFilePersistence.deleteFile(PARTNER_ID, fileId) } answers { }

        interactor.delete(PARTNER_ID, reportId = 1890L, fileId)
        verify(exactly = 1) { reportFilePersistence.deleteFile(PARTNER_ID, fileId) }
        assertThat(searchIndexSlot.captured).isEqualTo("Project/000096/Report/Partner/000420/PartnerReport/001890/")
    }

    @Test
    fun `delete - not existing`() {
        val projectId = 94L
        val fileId = -1L
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns projectId
        every { reportFilePersistence.existsFile(PARTNER_ID, any(), fileId) } returns false
        assertThrows<FileNotFound> { interactor.delete(PARTNER_ID, 5L, -1L) }
        verify(exactly = 0) { reportFilePersistence.deleteFile(any(), any()) }
    }

}
