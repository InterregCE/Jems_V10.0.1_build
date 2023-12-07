package io.cloudflight.jems.server.payments.service.audit.attachment.downloadPaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.downloadPaymentAuditAttachment.DownloadPaymentAuditAttachment
import io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.downloadPaymentAuditAttachment.FileNotFound
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadPaymentAuditAttachmentTest : UnitTest() {

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: DownloadPaymentAuditAttachment

    @BeforeEach
    fun reset() {
        clearMocks(filePersistence)
    }

    @Test
    fun download() {
        val file = mockk<Pair<String, ByteArray>>()
        every { filePersistence.downloadFile(JemsFileType.PaymentAuditAttachment, 45L) } returns file
        assertThat(interactor.download(45L)).isEqualTo(file)
    }

    @Test
    fun `download - not existing`() {
        every { filePersistence.downloadFile(JemsFileType.PaymentAuditAttachment, -1L) } returns null
        assertThrows<FileNotFound> { interactor.download(-1L) }
    }

}
