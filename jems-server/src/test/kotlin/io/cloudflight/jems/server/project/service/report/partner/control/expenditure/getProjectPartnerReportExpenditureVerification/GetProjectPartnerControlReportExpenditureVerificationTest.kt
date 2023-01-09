package io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetProjectPartnerControlReportExpenditureVerificationTest : UnitTest() {

    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence

    @InjectMockKs
    lateinit var getExpenditure: GetProjectPartnerControlReportExpenditureVerification

    @Test
    fun getExpenditureVerification() {
        val expenditure = mockk<ProjectPartnerReportExpenditureVerification>()
        every {
            reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(1L, reportId = 10L)
        } returns listOf(expenditure)

        assertThat(getExpenditure.getExpenditureVerification(1L, reportId = 10L)).containsExactly(expenditure)
    }
}
