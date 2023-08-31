package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.attachment.downloadPaymentToEcAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.repository.JemsFilePersistenceProvider
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadPaymentToEcAttachmentTest : UnitTest() {

    @MockK
    lateinit var filePersistenceProvider: JemsFilePersistenceProvider

    @InjectMockKs
    lateinit var interactor: DownloadPaymentToEcAttachment

    @BeforeEach
    fun reset() {
        clearMocks(filePersistenceProvider)
    }

    @Test
    fun download() {
        val file = mockk<Pair<String, ByteArray>>()
        every { filePersistenceProvider.downloadFile(JemsFileType.PaymentToEcAttachment, 9L) } returns file
        Assertions.assertThat(interactor.download(9L)).isEqualTo(file)
    }

    @Test
    fun `download - not existing`() {
        every { filePersistenceProvider.downloadFile(JemsFileType.PaymentToEcAttachment, -1L) } returns null
        assertThrows<FileNotFound> { interactor.download(-1L) }
    }

}
