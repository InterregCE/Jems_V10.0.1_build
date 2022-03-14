package io.cloudflight.jems.server.project.service.common

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetSpfCoFinancing
import io.cloudflight.jems.server.project.service.model.PartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnerCostType
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import java.math.BigDecimal
import java.math.RoundingMode

class PartnerBudgetPerFundCalculatorTest : UnitTest() {
    private val partnerId1 = 1L
    private val partnerId2 = 2L
    private val partnerId3 = 3L
    private val partner1 = ProjectPartnerSummary(id = partnerId1, abbreviation = "LP 1", role = ProjectPartnerRole.LEAD_PARTNER, active = true)
    private val partner2 = ProjectPartnerSummary(id = partnerId2, abbreviation = "PP 2", role = ProjectPartnerRole.PARTNER, active = true)
    private val partner3 = ProjectPartnerSummary(id = partnerId3, abbreviation = "PP 3", role = ProjectPartnerRole.PARTNER, active = true)
    private val partnerSpf = ProjectPartnerSummary(id = 4L, abbreviation = "BEN 1", role = ProjectPartnerRole.LEAD_PARTNER, active = true)
    private val fund1 = ProgrammeFund(id = 1L, selected = true, type = ProgrammeFundType.ERDF)
    private val fund2 = ProgrammeFund(id = 2L, selected = true, type = ProgrammeFundType.IPA_III)
    private val fund3 = ProgrammeFund(id = 3L, selected = true, type = ProgrammeFundType.OTHER)
    private val partnerBudgetPerFundCalculator = PartnerBudgetPerFundCalculator()

    @Test
    fun `should calculate budget per fund for partner`() {
        val result = partnerBudgetPerFundCalculator.calculate(
            partners = listOf(partner1, partner2, partner3),
            projectFunds = listOf(fund1, fund2, fund3),
            coFinancing = listOf(PartnerBudgetCoFinancing(
                partner1,
                ProjectPartnerCoFinancingAndContribution(
                    finances = listOf(
                        ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund, fund2, BigDecimal(80)),
                        ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution, null, BigDecimal(20)),
                    ),
                    partnerContributions = listOf(
                        ProjectPartnerContribution(id = 1L, name = "con1", status = ProjectPartnerContributionStatusDTO.Public, amount = BigDecimal(600), isPartner = true)
                    ),
                    partnerAbbreviation = "LP 1"
                ),
                total = BigDecimal(3000)),
                PartnerBudgetCoFinancing(
                    partner2,
                    ProjectPartnerCoFinancingAndContribution(
                        finances = listOf(
                            ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund, fund1, BigDecimal(80)),
                            ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund, fund3, BigDecimal(10)),
                            ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution, null, BigDecimal(10)),
                        ),
                        partnerContributions = listOf(
                            ProjectPartnerContribution(id = 2L, name = "con2", status = ProjectPartnerContributionStatusDTO.Public, amount = BigDecimal(3), isPartner = true)
                        ),
                        partnerAbbreviation = "PP 1"
                    ),
                    total = BigDecimal(30)),
                PartnerBudgetCoFinancing(
                    partner3,
                    ProjectPartnerCoFinancingAndContribution(
                        finances = listOf(
                            ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund, fund2, BigDecimal(80)),
                            ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution, null, BigDecimal(20)),
                        ),
                        partnerContributions = listOf(
                            ProjectPartnerContribution(id = 3L, name = "con3", status = ProjectPartnerContributionStatusDTO.Private, amount = BigDecimal(580), isPartner = true)
                        ),
                        partnerAbbreviation = "PP 2"
                    ),
                    total = BigDecimal(2900))
            ),
            spfCoFinancing = null
        )

        val expectedResult = listOf(ProjectPartnerBudgetPerFund(
            partner = partner1,
            costType = ProjectPartnerCostType.Management,
            budgetPerFund = setOf(
                PartnerBudgetPerFund(
                    fund = fund1,
                    percentage = BigDecimal.ZERO,
                    percentageOfTotal = BigDecimal.ZERO,
                    value = BigDecimal.ZERO
                ),
                PartnerBudgetPerFund(
                    fund = fund2,
                    percentage = BigDecimal(80),
                    percentageOfTotal = BigDecimal(50.85).setScale(2, RoundingMode.HALF_UP),
                    value = BigDecimal(2400).setScale(2)
                ),
                PartnerBudgetPerFund(
                    fund = fund3,
                    percentage = BigDecimal.ZERO,
                    percentageOfTotal = BigDecimal.ZERO,
                    value = BigDecimal.ZERO
                )
            ),
            publicContribution = BigDecimal(600),
            autoPublicContribution = BigDecimal.ZERO,
            privateContribution = BigDecimal.ZERO,
            totalPartnerContribution = BigDecimal(600),
            totalEligibleBudget = BigDecimal(3000).setScale(2),
            percentageOfTotalEligibleBudget = BigDecimal(50.59).setScale(2, RoundingMode.HALF_UP)
        ),
            ProjectPartnerBudgetPerFund(
                partner = partner2,
                costType = ProjectPartnerCostType.Management,
                budgetPerFund = setOf(
                    PartnerBudgetPerFund(
                        fund = fund1,
                        percentage = BigDecimal(80),
                        percentageOfTotal = BigDecimal(100).setScale(2),
                        value = BigDecimal(24).setScale(2)
                    ),
                    PartnerBudgetPerFund(
                        fund = fund2,
                        percentage = BigDecimal.ZERO,
                        percentageOfTotal = BigDecimal.ZERO,
                        value = BigDecimal.ZERO
                    ),
                    PartnerBudgetPerFund(
                        fund = fund3,
                        percentage = BigDecimal(10),
                        percentageOfTotal = BigDecimal(100).setScale(2),
                        value = BigDecimal(3).setScale(2)
                    )
                ),
                publicContribution = BigDecimal(3),
                autoPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                totalPartnerContribution = BigDecimal(3),
                totalEligibleBudget = BigDecimal(30).setScale(2),
                percentageOfTotalEligibleBudget = BigDecimal(0.51).setScale(2, RoundingMode.HALF_UP)
            ),
            ProjectPartnerBudgetPerFund(
                partner = partner3,
                costType = ProjectPartnerCostType.Management,
                budgetPerFund = setOf(
                    PartnerBudgetPerFund(
                        fund = fund1,
                        percentage = BigDecimal.ZERO,
                        percentageOfTotal = BigDecimal.ZERO,
                        value = BigDecimal.ZERO
                    ),
                    PartnerBudgetPerFund(
                        fund = fund2,
                        percentage = BigDecimal(80),
                        percentageOfTotal = BigDecimal(49.15).setScale(2, RoundingMode.HALF_UP),
                        value = BigDecimal(2320).setScale(2)
                    ),
                    PartnerBudgetPerFund(
                        fund = fund3,
                        percentage = BigDecimal.ZERO,
                        percentageOfTotal = BigDecimal.ZERO,
                        value = BigDecimal.ZERO
                    )
                ),
                publicContribution = BigDecimal.ZERO,
                autoPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal(580),
                totalPartnerContribution = BigDecimal(580),
                totalEligibleBudget = BigDecimal(2900).setScale(2),
                percentageOfTotalEligibleBudget = BigDecimal(48.90).setScale(2, RoundingMode.HALF_UP)
            ),
            ProjectPartnerBudgetPerFund(
                partner = null,
                budgetPerFund = setOf(
                    PartnerBudgetPerFund(
                        fund = fund1,
                        percentage = BigDecimal(0.40).setScale(2, RoundingMode.HALF_UP),
                        percentageOfTotal = BigDecimal.ZERO,
                        value = BigDecimal(24).setScale(2)
                    ),
                    PartnerBudgetPerFund(
                        fund = fund2,
                        percentage = BigDecimal(79.60).setScale(2, RoundingMode.HALF_UP),
                        percentageOfTotal = BigDecimal.ZERO,
                        value = BigDecimal(4720).setScale(2)
                    ),
                    PartnerBudgetPerFund(
                        fund = fund3,
                        percentage = BigDecimal(0.05).setScale(2, RoundingMode.HALF_UP),
                        percentageOfTotal = BigDecimal.ZERO,
                        value = BigDecimal(3).setScale(2)
                    )
                ),
                publicContribution = BigDecimal(603),
                autoPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal(580),
                totalPartnerContribution = BigDecimal(1183),
                totalEligibleBudget = BigDecimal(5930),
                percentageOfTotalEligibleBudget = BigDecimal(100)
            ))

        Assertions.assertEquals(expectedResult, result)

    }

    @Test
    fun `should calculate budget per fund for SPF project`() {
        val result = partnerBudgetPerFundCalculator.calculate(
            partners = listOf(partnerSpf),
            projectFunds = listOf(fund1, fund2, fund3),
            coFinancing = listOf(
                PartnerBudgetCoFinancing(
                    partnerSpf,
                    ProjectPartnerCoFinancingAndContribution(
                        finances = listOf(
                            ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund, fund2, BigDecimal(80)),
                            ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution, null, BigDecimal(20)),
                        ),
                        partnerContributions = listOf(
                            ProjectPartnerContribution(id = 1L, name = "con1", status = ProjectPartnerContributionStatusDTO.Public, amount = BigDecimal(600), isPartner = true)
                        ),
                        partnerAbbreviation = partnerSpf.abbreviation
                    ),
                    total = BigDecimal(3000)
                )
            ),
            spfCoFinancing = PartnerBudgetSpfCoFinancing(
                partnerSpf,
                ProjectPartnerCoFinancingAndContributionSpf(
                    finances = listOf(
                        ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund, fund1, BigDecimal(80)),
                        ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund, fund3, BigDecimal(10)),
                        ProjectPartnerCoFinancing(fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution, null, BigDecimal(10)),
                    ),
                    partnerContributions = listOf(
                        ProjectPartnerContributionSpf(
                            id = 2L,
                            name = "contribution spf 1",
                            status = ProjectPartnerContributionStatusDTO.Public,
                            amount = BigDecimal(3),
                        ),
                        ProjectPartnerContributionSpf(
                            id = 3L,
                            name = "contribution spf 1",
                            status = ProjectPartnerContributionStatusDTO.Private,
                            amount = BigDecimal(7),
                        )
                    )
                ),
                total = BigDecimal(30))
        )

        val expectedResult = listOf(ProjectPartnerBudgetPerFund(
            partner = partnerSpf,
            costType = ProjectPartnerCostType.Management,
            budgetPerFund = setOf(
                PartnerBudgetPerFund(
                    fund = fund1,
                    percentage = BigDecimal.ZERO,
                    percentageOfTotal = BigDecimal.ZERO,
                    value = BigDecimal.ZERO
                ),
                PartnerBudgetPerFund(
                    fund = fund2,
                    percentage = BigDecimal(80),
                    percentageOfTotal = BigDecimal(100.00).setScale(2, RoundingMode.HALF_UP),
                    value = BigDecimal(2400).setScale(2)
                ),
                PartnerBudgetPerFund(
                    fund = fund3,
                    percentage = BigDecimal.ZERO,
                    percentageOfTotal = BigDecimal.ZERO,
                    value = BigDecimal.ZERO
                )
            ),
            publicContribution = BigDecimal(600),
            autoPublicContribution = BigDecimal.ZERO,
            privateContribution = BigDecimal.ZERO,
            totalPartnerContribution = BigDecimal(600),
            totalEligibleBudget = BigDecimal(3000).setScale(2),
            percentageOfTotalEligibleBudget = BigDecimal(99.01).setScale(2, RoundingMode.HALF_UP)
        ),
            ProjectPartnerBudgetPerFund(
                partner = partnerSpf,
                costType = ProjectPartnerCostType.Spf,
                budgetPerFund = setOf(
                    PartnerBudgetPerFund(
                        fund = fund1,
                        percentage = BigDecimal(80),
                        percentageOfTotal = BigDecimal(100).setScale(2),
                        value = BigDecimal(24).setScale(2)
                    ),
                    PartnerBudgetPerFund(
                        fund = fund2,
                        percentage = BigDecimal.ZERO,
                        percentageOfTotal = BigDecimal.ZERO,
                        value = BigDecimal.ZERO
                    ),
                    PartnerBudgetPerFund(
                        fund = fund3,
                        percentage = BigDecimal(10),
                        percentageOfTotal = BigDecimal(100).setScale(2),
                        value = BigDecimal(3).setScale(2)
                    )
                ),
                publicContribution = BigDecimal(3),
                autoPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal(7),
                totalPartnerContribution = BigDecimal(10),
                totalEligibleBudget = BigDecimal(30).setScale(2),
                percentageOfTotalEligibleBudget = BigDecimal(0.99).setScale(2, RoundingMode.HALF_UP)
            ),
            ProjectPartnerBudgetPerFund(
                partner = null,
                budgetPerFund = setOf(
                    PartnerBudgetPerFund(
                        fund = fund1,
                        percentage = BigDecimal(0.79).setScale(2, RoundingMode.HALF_UP),
                        percentageOfTotal = BigDecimal.ZERO,
                        value = BigDecimal(24).setScale(2)
                    ),
                    PartnerBudgetPerFund(
                        fund = fund2,
                        percentage = BigDecimal(79.21).setScale(2, RoundingMode.HALF_UP),
                        percentageOfTotal = BigDecimal.ZERO,
                        value = BigDecimal(2400).setScale(2)
                    ),
                    PartnerBudgetPerFund(
                        fund = fund3,
                        percentage = BigDecimal(0.10).setScale(2, RoundingMode.HALF_UP),
                        percentageOfTotal = BigDecimal.ZERO,
                        value = BigDecimal(3).setScale(2)
                    )
                ),
                publicContribution = BigDecimal(603),
                autoPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal(7),
                totalPartnerContribution = BigDecimal(610),
                totalEligibleBudget = BigDecimal(3030),
                percentageOfTotalEligibleBudget = BigDecimal(100)
            ))

        Assertions.assertEquals(expectedResult, result)
    }
}
