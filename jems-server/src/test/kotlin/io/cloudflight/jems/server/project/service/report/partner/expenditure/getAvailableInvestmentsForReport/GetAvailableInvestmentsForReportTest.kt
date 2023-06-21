package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableInvestmentsForReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetAvailableInvestmentsForReportTest : UnitTest() {

    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence

    @InjectMockKs
    lateinit var interactor: GetAvailableInvestmentsForReport

    @Test
    fun getInvestments() {
        val investment = mockk<ProjectPartnerReportInvestment>()
        every { reportExpenditurePersistence.getAvailableInvestments(1L, 10L) } returns listOf(investment)
        assertThat(interactor.getInvestments(1L, 10L)).containsExactly(investment)
    }

}
