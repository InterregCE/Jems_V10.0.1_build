package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.downloadProjectPartnerReportGdprFile

import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.downloadProjectPartnerProcurementGdprFile.DownloadProjectPartnerReportGdprFile
import io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.downloadProjectPartnerProcurementGdprFile.FileNotFound
import io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.downloadProjectPartnerProcurementGdprFile.SensitiveFileException
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadProjectPartnerReportGdprFileTest {
    companion object {
        private const val expectedPath = "Project/000008/Report/Partner/000640/PartnerReport/000477/Procurement/000201/ProcurementGdprAttachment/"

        const val PARTNER_ID = 640L
        const val FILE_ID = 200L
        val FILE = mockk<Pair<String, ByteArray>>()
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var sensitiveDataAuthorization: SensitiveDataAuthorizationService

    @InjectMockKs
    lateinit var service: DownloadProjectPartnerReportGdprFile

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence, sensitiveDataAuthorization)
    }

    @Test
    fun downloadGdprFile() {
        every { filePersistence.existsFile(PARTNER_ID, expectedPath, FILE_ID) } returns true
        every { sensitiveDataAuthorization.canViewPartnerSensitiveData(PARTNER_ID) } returns true
        every { filePersistence.downloadFile(PARTNER_ID, FILE_ID) } returns FILE

        assertThat(service.download(PARTNER_ID, FILE_ID)).isEqualTo(FILE)
    }

    @Test
    fun `downloadGdprFile - user does not have access to gdpr`() {
        every { filePersistence.existsFile(PARTNER_ID, expectedPath, FILE_ID) } returns true
        every { sensitiveDataAuthorization.canViewPartnerSensitiveData(PARTNER_ID) } returns false
        every { filePersistence.downloadFile(PARTNER_ID, FILE_ID) } returns FILE

        assertThrows<SensitiveFileException> { service.download(PARTNER_ID, FILE_ID) }
    }
    @Test
    fun `downloadGdprFile - not existing`() {
        every { filePersistence.existsFile(PARTNER_ID, any(), -1L)} returns false
        every { filePersistence.downloadFile(PARTNER_ID, -1L) } returns null
        every { sensitiveDataAuthorization.canViewPartnerSensitiveData(PARTNER_ID) } returns true

        assertThrows<FileNotFound> { service.download(PARTNER_ID, -1L) }
    }
}
