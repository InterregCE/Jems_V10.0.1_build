package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableInvestmentsForReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class GetAvailableInvestmentsForReportTest : UnitTest() {

    private val investment1 = InvestmentSummary(
        id = 1L,
        investmentNumber = 1,
        workPackageNumber = 1
    )

    private val investment2 = InvestmentSummary(
        id = 2L,
        investmentNumber = 2,
        workPackageNumber = 1
    )

    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence

    @InjectMockKs
    lateinit var interactor: GetAvailableInvestmentsForReport

    @Test
    fun getInvestments() {
        every { reportExpenditurePersistence.getAvailableInvestments(1L, 10L) } returns
            listOf(investment1, investment2)
        Assertions.assertThat(interactor.getInvestments(1L, 10L))
            .containsExactlyInAnyOrder(investment1, investment2)
    }
}
