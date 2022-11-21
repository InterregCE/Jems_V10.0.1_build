package io.cloudflight.jems.server.payments.service.regular.attachment.downloadPaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
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

class DownloadPaymentAttachmentTest : UnitTest() {

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: DownloadPaymentAttachment

    @BeforeEach
    fun reset() {
        clearMocks(filePersistence)
    }

    @Test
    fun download() {
        val file = mockk<Pair<String, ByteArray>>()
        every { filePersistence.downloadFile(JemsFileType.PaymentAttachment, 45L) } returns file
        assertThat(interactor.download(45L)).isEqualTo(file)
    }

    @Test
    fun `download - not existing`() {
        every { filePersistence.downloadFile(JemsFileType.PaymentAttachment, -1L) } returns null
        assertThrows<FileNotFound> { interactor.download(-1L) }
    }

}
