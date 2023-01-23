package io.cloudflight.jems.server.project.service.report.partner.control.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.ProjectPartnerReportAuthorization
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificate.DownloadReportControlCertificate
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificate.FileNotFound
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadReportControlCertificateTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 5L
        private const val PROJECT_ID = 1L
        private const val REPORT_ID = 2L
        private const val expectedFilePath = "Project/000001/Report/Partner/000005/PartnerControlReport/000002/ControlCertificate/"
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var partnerReportAuth: ProjectPartnerReportAuthorization

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @InjectMockKs
    lateinit var interactor: DownloadReportControlCertificate

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence)
    }

    @Test
    fun `download report control certificate`() {
        val file = mockk<Pair<String, ByteArray>>()

        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { filePersistence.existsFile(PARTNER_ID, expectedFilePath, 15L) } returns true
        every { filePersistence.downloadFile(PARTNER_ID, 15L) } returns file

        assertThat(interactor.downloadReportControlCertificate(PARTNER_ID, REPORT_ID, fileId = 15L)).isEqualTo(file)
    }

    @Test
    fun `download - file not found`() {
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { filePersistence.existsFile(PARTNER_ID, expectedFilePath, -1) } returns false
        every { filePersistence.downloadFile(PARTNER_ID, -1) } returns null

        assertThrows<FileNotFound> { interactor.downloadReportControlCertificate(PARTNER_ID, REPORT_ID, -1L) }
    }
}
