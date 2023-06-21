package io.cloudflight.jems.server.project.service.report.project.annexes.download

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DownloadProjectReportAnnexesFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 1L
        const val REPORT_ID = 2L
        const val FILE_ID = 3L
        const val pathPrefix = "Project/000001/Report/ProjectReport/000002/"
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var downloadProjectReportAnnexesFile: DownloadProjectReportAnnexesFile

    @Test
    fun `should download file from the project report annexes (root)`() {
        val file = mockk<Pair<String, ByteArray>>()
        every { filePersistence.existsReportFile(PROJECT_ID, pathPrefix, FILE_ID) } returns true
        every { filePersistence.downloadReportFile(PROJECT_ID, FILE_ID) } returns file

        assertThat(downloadProjectReportAnnexesFile.download(PROJECT_ID, REPORT_ID, FILE_ID)).isEqualTo(file)
    }

    @Test
    fun `should throw FileNotFound when file does not exist`() {
        every { filePersistence.existsReportFile(PROJECT_ID, pathPrefix, -1L) } returns false
        every { filePersistence.downloadReportFile(PROJECT_ID, -1L) } returns null

        assertThrows<FileNotFound> { downloadProjectReportAnnexesFile.download(PROJECT_ID, REPORT_ID, -1L) }
    }
}
