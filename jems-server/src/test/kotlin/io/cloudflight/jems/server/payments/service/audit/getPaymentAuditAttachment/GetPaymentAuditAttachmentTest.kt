package io.cloudflight.jems.server.payments.service.audit.attachment.getPaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.payments.service.audit.export.attachment.getPaymentAuditAttchament.GetPaymentAuditAttachment
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

class GetPaymentAuditAttachmentTest : UnitTest() {

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: GetPaymentAuditAttachment

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
                indexPrefix = "Payment/Audit/PaymentAuditAttachment/",
                filterSubtypes = emptySet(),
                filterUserIds = emptySet(),
            )
        } returns PageImpl(listOf(reportFile))

        assertThat(interactor.list(Pageable.unpaged()).content)
            .containsExactly(reportFile)
    }

}
