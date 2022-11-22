package io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.getProjectPartnerReportProcurementSubcontract

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetProjectPartnerReportProcurementSubcontractTest : UnitTest() {


    @MockK
    lateinit var service: GetProjectPartnerReportProcurementSubcontractService

    @InjectMockKs
    lateinit var interactor: GetProjectPartnerReportProcurementSubcontract

    @BeforeEach
    fun reset() {
        clearMocks(service)
    }

    @Test
    fun getSubcontract() {
        val reportId = 58L
        val procurementId = 15L

        val result = mockk<ProjectPartnerReportProcurementSubcontract>()
        every { service.getSubcontract(7L, reportId = reportId, procurementId = procurementId) } returns listOf(result)

        assertThat(interactor.getSubcontract(7L, reportId = reportId, procurementId = procurementId))
            .containsExactly(result)
    }

}
