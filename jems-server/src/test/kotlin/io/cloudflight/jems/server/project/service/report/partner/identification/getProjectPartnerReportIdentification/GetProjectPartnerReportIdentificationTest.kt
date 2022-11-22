package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetProjectPartnerReportIdentificationTest : UnitTest() {

    @MockK
    private lateinit var service: GetProjectPartnerReportIdentificationService

    @InjectMockKs
    private lateinit var interactor: GetProjectPartnerReportIdentification

    @Test
    fun getIdentification() {
        val result = mockk<ProjectPartnerReportIdentification>()
        every { service.getIdentification(partnerId = 15L, reportId = 150L) } returns result
        assertThat(interactor.getIdentification(15L, reportId = 150L)).isEqualTo(result)
    }

}
