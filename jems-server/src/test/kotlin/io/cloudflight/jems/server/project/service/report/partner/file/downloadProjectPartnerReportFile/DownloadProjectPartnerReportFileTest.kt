package io.cloudflight.jems.server.project.service.report.partner.file.downloadProjectPartnerReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
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
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var sensitiveDataAuthorization: SensitiveDataAuthorizationService

    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence

    @InjectMockKs
    lateinit var interactor: DownloadProjectPartnerReportFile

    @Test
    fun download() {
        val file = mockk<Pair<String, ByteArray>>()
        every { sensitiveDataAuthorization.canViewPartnerSensitiveData(PARTNER_ID) } returns true
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(PARTNER_ID, fileId = 45L) } returns false

        every { filePersistence.downloadFile(PARTNER_ID, 45L) } returns file
        assertThat(interactor.download(PARTNER_ID, 45L)).isEqualTo(file)
    }

    @Test
    fun `download - not existing`() {
        every { filePersistence.downloadFile(PARTNER_ID, -1L) } returns null
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(PARTNER_ID, fileId = -1L) } returns false
        assertThrows<FileNotFound> { interactor.download(PARTNER_ID, -1L) }
    }

    @Test
    fun `non GDPR user can NOT download sensitive file`(){
        every { sensitiveDataAuthorization.canViewPartnerSensitiveData(PARTNER_ID) } returns false
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(PARTNER_ID, fileId = 36L) } returns true
        assertThrows<SensitiveFileException> { interactor.download(PARTNER_ID, 36L) }
    }

    @Test
    fun `can download sensitive file`(){
        val file = mockk<Pair<String, ByteArray>>()
        every { sensitiveDataAuthorization.canViewPartnerSensitiveData(PARTNER_ID) } returns true
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(PARTNER_ID, fileId = 64L) } returns true

        every { filePersistence.downloadFile(PARTNER_ID, 64L) } returns file
        assertThat(interactor.download(PARTNER_ID, 64L)).isEqualTo(file)
    }

}
