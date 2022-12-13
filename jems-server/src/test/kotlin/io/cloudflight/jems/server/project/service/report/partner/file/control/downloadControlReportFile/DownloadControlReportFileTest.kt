package io.cloudflight.jems.server.project.service.report.partner.file.control.downloadControlReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadControlReportFileTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 418L
    }

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: DownloadControlReportFile

    @BeforeEach
    fun reset() {
        clearMocks(partnerPersistence, filePersistence)
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns 7L
    }

    @Test
    fun download() {
        val slotPrefix = slot<String>()
        every { filePersistence.existsFile(partnerId = PARTNER_ID, capture(slotPrefix), fileId = 45L) } returns true

        val file = mockk<Pair<String, ByteArray>>()
        every { filePersistence.downloadFile(PARTNER_ID, 45L) } returns file

        assertThat(interactor.download(PARTNER_ID, 661L, 45L)).isEqualTo(file)
        assertThat(slotPrefix.captured).isEqualTo("Project/000007/Report/Partner/000418/PartnerControlReport/000661/")
    }

    @Test
    fun `download - not existing - when already downloading`() {
        every { filePersistence.existsFile(partnerId = PARTNER_ID, any(), fileId = -1L) } returns true
        every { filePersistence.downloadFile(PARTNER_ID, -1L) } returns null
        assertThrows<FileNotFound> { interactor.download(PARTNER_ID, 662L, -1L) }
    }

    @Test
    fun `download - not existing - when checking report id`() {
        every { filePersistence.existsFile(partnerId = PARTNER_ID, any(), fileId = -2L) } returns false
        assertThrows<FileNotFound> { interactor.download(PARTNER_ID, 663L, -2L) }
        verify(exactly = 0) { filePersistence.downloadFile(any<Long>(), any()) }
    }

}
