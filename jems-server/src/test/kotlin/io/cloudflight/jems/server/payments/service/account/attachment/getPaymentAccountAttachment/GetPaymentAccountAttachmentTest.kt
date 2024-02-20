package io.cloudflight.jems.server.payments.service.account.attachment.getPaymentAccountAttachment

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

class GetPaymentAccountAttachmentTest : UnitTest() {

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: GetPaymentAccountAttachment

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
                indexPrefix = "Payment/Account/000005/",
                filterSubtypes = emptySet(),
                filterUserIds = emptySet()
            )
        } returns PageImpl(listOf(file))

        assertThat(interactor.list(5L, Pageable.unpaged()).content).containsExactly(file)
    }
}
