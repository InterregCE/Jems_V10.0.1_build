package io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.getProjectPartnerReportProcurementAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectReportProcurementFile
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetProjectPartnerReportProcurementAttachmentTest : UnitTest() {

    @MockK
    lateinit var service: GetProjectPartnerReportProcurementAttachmentService

    @InjectMockKs
    lateinit var interactor: GetProjectPartnerReportProcurementAttachment

    @BeforeEach
    fun reset() {
        clearMocks(service)
    }

    @Test
    fun getAttachment() {
        val reportId = 528L
        val procurementId = 152L

        val result = mockk<ProjectReportProcurementFile>()
        every { service.getAttachment(5L, reportId = reportId, procurementId = procurementId) } returns listOf(result)

        assertThat(interactor.getAttachment(5L, reportId = reportId, procurementId = procurementId))
            .containsExactly(result)
    }

}
