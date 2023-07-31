package io.cloudflight.jems.server.project.service.report.project.verification.file.download

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationDocument
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadProjectReportVerificationFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 51L
        const val REPORT_ID = 55L
        const val FILE_ID = 59L
        fun filePath() = VerificationDocument.generatePath(PROJECT_ID, REPORT_ID)
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: DownloadProjectReportVerificationFile

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence)
    }

    @Test
    fun download() {
        val file = mockk<Pair<String, ByteArray>>()

        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns true
        every { filePersistence.downloadFile(type = VerificationDocument, fileId = FILE_ID) } returns file

        assertThat(interactor.download(PROJECT_ID, REPORT_ID, FILE_ID)).isEqualTo(file)
    }

    @Test
    fun `download - FileNotFound`() {
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns false

        assertThrows<FileNotFound> { interactor.download(PROJECT_ID, REPORT_ID, FILE_ID) }
    }

}
