package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetPartnerAndPartnerReportDataTest: UnitTest() {

    @MockK
    private lateinit var service: GetPartnerAndPartnerReportDataService

    @InjectMockKs
    private lateinit var interactor: GetPartnerAndPartnerReportData

    @Test
    fun getPartnerAndPartnerReportData() {
        val result = mockk<List<CorrectionAvailablePartner>>()
        every { service.getPartnerAndPartnerReportData(45L) } returns result
        assertThat(interactor.getPartnerAndPartnerReportData(45L)).isEqualTo(result)
    }

}
