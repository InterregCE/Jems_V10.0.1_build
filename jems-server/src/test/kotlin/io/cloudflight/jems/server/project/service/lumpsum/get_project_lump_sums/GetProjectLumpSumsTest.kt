package io.cloudflight.jems.server.project.service.lumpsum.get_project_lump_sums

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetProjectLumpSumsTest : UnitTest() {

    companion object {
        private val lumpSum = ProjectLumpSum(
            programmeLumpSumId = 51,
            period = 1,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSum(partnerId = 14, amount = BigDecimal.TEN),
                ProjectPartnerLumpSum(partnerId = 15, amount = BigDecimal.ONE),
            ),
        )
    }

    @MockK
    lateinit var persistence: ProjectLumpSumPersistence

    @MockK
    lateinit var budgetCostsPersistence: ProjectPartnerBudgetCostsPersistence

    @InjectMockKs
    lateinit var getProjectLumpSums: GetProjectLumpSums

    @Test
    fun getLumpSums() {
        every { persistence.getLumpSums(1L) } returns listOf(lumpSum)
        assertThat(getProjectLumpSums.getLumpSums(1L)).containsExactly(lumpSum)
    }

    @Test
    fun getLumpSumsTotalForPartner() {
        every { budgetCostsPersistence.getBudgetLumpSumsCostTotal(6L) } returns BigDecimal.TEN
        assertThat(getProjectLumpSums.getLumpSumsTotalForPartner(6L)).isEqualByComparingTo(BigDecimal.TEN)
    }

    @Test
    fun getLumpSumsTotalForPartnerHistoric() {
        every { budgetCostsPersistence.getBudgetLumpSumsCostTotal(7L, "1.0") } returns BigDecimal.ONE
        assertThat(getProjectLumpSums.getLumpSumsTotalForPartner(7L, "1.0")).isEqualByComparingTo(BigDecimal.ONE)
    }

}
