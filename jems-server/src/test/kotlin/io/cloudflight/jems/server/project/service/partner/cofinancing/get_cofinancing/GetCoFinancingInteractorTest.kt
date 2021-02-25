package io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundType
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus.AutomaticPublic
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus.Public
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetCoFinancingInteractorTest {

    companion object {
        private val fund = ProgrammeFund(id = 1, selected = true)
    }

    @MockK
    lateinit var persistence: ProjectPartnerCoFinancingPersistence

    lateinit var getInteractor: GetCoFinancingInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        getInteractor = GetCoFinancing(persistence)
    }

    @Test
    fun `test get cofinancing`() {
        val finances = setOf(
            ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundType.MainFund, fund = fund, percentage = 20),
            ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundType.PartnerContribution, fund = null, percentage = 80)
        )
        val contributions = listOf(
            ProjectPartnerContribution(
                id = 1,
                name = null,
                status = Public,
                amount = BigDecimal.TEN,
                isPartner = true),
            ProjectPartnerContribution(
                id = 2,
                name = "BMW",
                status = AutomaticPublic,
                amount = BigDecimal.ONE,
                isPartner = false
            )
        )

        every { persistence.getCoFinancingAndContributions(1) } returns
            ProjectPartnerCoFinancingAndContribution(
                finances = finances,
                partnerContributions = contributions,
                partnerAbbreviation = "mocked abbr"
            )

        assertThat(getInteractor.getCoFinancing(1))
            .isEqualTo(
                ProjectPartnerCoFinancingAndContribution(
                    finances = finances,
                    partnerContributions = contributions,
                    partnerAbbreviation = "mocked abbr"
                )
            )
    }

}
