package io.cloudflight.jems.server.payments.service.advance.attachment.downloadPaymentAdvAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.repository.JemsFilePersistenceProvider
import io.cloudflight.jems.server.payments.service.advance.attachment.downloadPaymentAdvanceAttachment.DownloadPaymentAdvanceAttachment
import io.cloudflight.jems.server.payments.service.advance.attachment.downloadPaymentAdvanceAttachment.FileNotFound
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadPaymentAdvAttachmentTest : UnitTest() {

    @MockK
    lateinit var filePersistenceProvider: JemsFilePersistenceProvider

    @InjectMockKs
    lateinit var interactor: DownloadPaymentAdvanceAttachment

    @BeforeEach
    fun reset() {
        clearMocks(filePersistenceProvider)
    }

    @Test
    fun download() {
        val file = mockk<Pair<String, ByteArray>>()
        every { filePersistenceProvider.downloadFile(JemsFileType.PaymentAdvanceAttachment, 9L) } returns file
        assertThat(interactor.download(9L)).isEqualTo(file)
    }

    @Test
    fun `download - not existing`() {
        every { filePersistenceProvider.downloadFile(JemsFileType.PaymentAdvanceAttachment, -1L) } returns null
        assertThrows<FileNotFound> { interactor.download(-1L) }
    }

}
