package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableBudgetOptionsForReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class GetAvailableBudgetOptionsForReportTest: UnitTest()  {
    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence

    @InjectMockKs
    lateinit var interactor: GetAvailableBudgetOptionsForReport

    @Test
    fun getBudgetOptions() {
        val budgetOptions = mockk<ProjectPartnerBudgetOptions>()
        every { reportExpenditurePersistence.getAvailableBudgetOptions(1L, 10L) } returns budgetOptions
        Assertions.assertThat(interactor.getBudgetOptions(1L, 10L)).isEqualTo(budgetOptions)
    }
}
