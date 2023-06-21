package io.cloudflight.jems.server.project.service.report.partner.control.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.service.report.model.partner.control.file.PartnerReportControlFile
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadFile.DownloadReportControlFile
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DownloadReportControlFileTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 5L
        private const val REPORT_ID = 2L
    }

    @MockK
    lateinit var projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence


    @InjectMockKs
    lateinit var interactor: DownloadReportControlFile

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence)
    }

    @Test
    fun `download report control certificate`() {
        val file = mockk<Pair<String, ByteArray>>()
        val controlFile = mockk<PartnerReportControlFile>()

        every { projectPartnerReportControlFilePersistence.getByReportIdAndId(REPORT_ID, 15L) } returns controlFile
        every { controlFile.generatedFile.id } returns 15L
        every { filePersistence.downloadFile(PARTNER_ID, 15L) } returns file

        assertThat(interactor.download(PARTNER_ID, REPORT_ID, fileId = 15L)).isEqualTo(file)
    }

}
