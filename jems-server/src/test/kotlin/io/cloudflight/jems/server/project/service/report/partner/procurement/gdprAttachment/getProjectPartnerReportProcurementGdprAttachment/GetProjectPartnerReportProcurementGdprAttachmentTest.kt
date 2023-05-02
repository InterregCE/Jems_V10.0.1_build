package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.getProjectPartnerReportProcurementGdprAttachment

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

internal class GetProjectPartnerReportProcurementGdprAttachmentTest : UnitTest() {

    @MockK
    lateinit var service: GetProjectPartnerReportProcurementGdprAttachmentService

    @InjectMockKs
    lateinit var interactor: GetProjectPartnerReportProcurementGdprAttachment

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

        assertThat(interactor.getGdprAttachment(5L, reportId = reportId, procurementId = procurementId))
            .containsExactly(result)
    }

}
