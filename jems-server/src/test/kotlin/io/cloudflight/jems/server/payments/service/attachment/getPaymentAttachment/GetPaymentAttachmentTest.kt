package io.cloudflight.jems.server.payments.service.attachment.getPaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.attachment.getPaymentAttchament.GetPaymentAttachment
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
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
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @InjectMockKs
    lateinit var interactor: GetPaymentAttachment

    @BeforeEach
    fun reset() {
        clearMocks(reportFilePersistence)
    }

    @Test
    fun list() {
        val reportFile = mockk<ProjectReportFile>()
        every {
            reportFilePersistence.listAttachments(
                pageable = any(),
                indexPrefix = "Payment/000004/PaymentAttachment/",
                filterSubtypes = emptySet(),
                filterUserIds = emptySet(),
            )
        } returns PageImpl(listOf(reportFile))

        assertThat(interactor.list(4L, Pageable.unpaged()).content)
            .containsExactly(reportFile)
    }

}
