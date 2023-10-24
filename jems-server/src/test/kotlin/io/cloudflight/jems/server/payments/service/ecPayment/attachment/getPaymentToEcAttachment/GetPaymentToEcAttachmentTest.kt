package io.cloudflight.jems.server.payments.service.ecPayment.attachment.getPaymentToEcAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
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

class GetPaymentToEcAttachmentTest : UnitTest() {

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: GetPaymentToEcAttachment

    @BeforeEach
    fun reset() {
        clearMocks(filePersistence)
    }

    @Test
    fun list() {
        val file = mockk<JemsFile>()
        every {
            filePersistence.listAttachments(
                pageable = any(),
                indexPrefix = "Payment/Ec/000005/PaymentToEcAttachment/",
                filterSubtypes = emptySet(),
                filterUserIds = emptySet()
            )
        } returns PageImpl(listOf(file))

        assertThat(interactor.list(5L, Pageable.unpaged()).content).containsExactly(file)
    }
}
