package io.cloudflight.jems.server.project.service.report.project.verification.certificate.download

import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationCertificate
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadProjectReportVerificationCertificateTest {

    companion object {
        const val PROJECT_ID = 131L
        const val REPORT_ID = 123L
        const val FILE_ID = 115L
        fun filePath() = VerificationCertificate.generatePath(PROJECT_ID, REPORT_ID)
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: DownloadProjectReportVerificationCertificate

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence)
    }

    @Test
    fun download() {
        val file = mockk<Pair<String, ByteArray>>()

        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns true
        every { filePersistence.downloadFile(type = VerificationCertificate, fileId = FILE_ID) } returns file

        assertThat(interactor.download(PROJECT_ID, REPORT_ID, FILE_ID)).isEqualTo(file)
    }

    @Test
    fun `download - FileNotFound`() {
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns false

        assertThrows<FileNotFound> { interactor.download(PROJECT_ID, REPORT_ID, FILE_ID) }
    }
}
