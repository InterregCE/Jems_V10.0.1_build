package io.cloudflight.jems.server.project.service.report.partner.file.downloadProjectPartnerReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadProjectPartnerReportFileTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 408L
    }

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @InjectMockKs
    lateinit var interactor: DownloadProjectPartnerReportFile

    @Test
    fun download() {
        val file = mockk<Pair<String, ByteArray>>()
        every { reportFilePersistence.downloadFile(PARTNER_ID, 45L) } returns file
        assertThat(interactor.download(PARTNER_ID, 45L)).isEqualTo(file)
    }

    @Test
    fun `download - not existing`() {
        every { reportFilePersistence.downloadFile(PARTNER_ID, -1L) } returns null
        assertThrows<FileNotFound> { interactor.download(PARTNER_ID, -1L) }
    }

}
