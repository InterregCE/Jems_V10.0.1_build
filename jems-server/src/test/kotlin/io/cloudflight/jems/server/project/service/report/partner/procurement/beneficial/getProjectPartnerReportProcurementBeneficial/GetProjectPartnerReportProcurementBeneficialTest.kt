package io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.getProjectPartnerReportProcurementBeneficial

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetProjectPartnerReportProcurementBeneficialTest : UnitTest() {


    @MockK
    lateinit var service: GetProjectPartnerReportProcurementBeneficialService

    @InjectMockKs
    lateinit var interactor: GetProjectPartnerReportProcurementBeneficial

    @BeforeEach
    fun reset() {
        clearMocks(service)
    }

    @Test
    fun getBeneficialOwner() {
        val reportId = 98L
        val procurementId = 45L

        val result = mockk<ProjectPartnerReportProcurementBeneficialOwner>()
        every { service.getBeneficialOwner(20L, reportId = reportId, procurementId = procurementId) } returns listOf(result)

        assertThat(interactor.getBeneficialOwner(20L, reportId = reportId, procurementId = procurementId))
            .containsExactly(result)
    }

}
