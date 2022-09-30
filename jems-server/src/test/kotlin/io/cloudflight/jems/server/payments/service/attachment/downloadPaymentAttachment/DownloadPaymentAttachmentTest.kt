package io.cloudflight.jems.server.payments.service.attachment.downloadPaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
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
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @InjectMockKs
    lateinit var interactor: DownloadPaymentAttachment

    @BeforeEach
    fun reset() {
        clearMocks(reportFilePersistence)
    }

    @Test
    fun download() {
        val file = mockk<Pair<String, ByteArray>>()
        every { reportFilePersistence.downloadFile(ProjectPartnerReportFileType.PaymentAttachment, 45L) } returns file
        assertThat(interactor.download(45L)).isEqualTo(file)
    }

    @Test
    fun `download - not existing`() {
        every { reportFilePersistence.downloadFile(ProjectPartnerReportFileType.PaymentAttachment, -1L) } returns null
        assertThrows<FileNotFound> { interactor.download(-1L) }
    }

}
