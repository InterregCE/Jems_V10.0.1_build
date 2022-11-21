package io.cloudflight.jems.server.payments.service.regular.attachment.getPaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.payments.service.regular.attachment.getPaymentAttchament.GetPaymentAttachment
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class GetPaymentAttachmentTest : UnitTest() {

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: GetPaymentAttachment

    @BeforeEach
    fun reset() {
        clearMocks(filePersistence)
    }

    @Test
    fun list() {
        val reportFile = mockk<JemsFile>()
        every {
            filePersistence.listAttachments(
                pageable = any(),
                indexPrefix = "Payment/Regular/000004/PaymentAttachment/",
                filterSubtypes = emptySet(),
                filterUserIds = emptySet(),
            )
        } returns PageImpl(listOf(reportFile))

        assertThat(interactor.list(4L, Pageable.unpaged()).content)
            .containsExactly(reportFile)
    }

}
