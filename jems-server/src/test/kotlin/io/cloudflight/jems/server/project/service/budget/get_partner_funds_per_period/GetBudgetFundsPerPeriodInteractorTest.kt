package io.cloudflight.jems.server.project.service.budget.get_partner_funds_per_period

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.CallFundRate
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.get_budget_funds_per_period.GetBudgetFundsPerPeriod
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import io.cloudflight.jems.server.project.service.budget.model.ProjectSpfBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.BudgetCostsDetail
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerCostType
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectFundBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectPeriodFund
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing.GetCoFinancingInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

class GetBudgetFundsPerPeriodInteractorTest : UnitTest() {

    private val partner1Id = 1L
    private val partner2Id = 2L
    private val spfBeneficiaryId = 3L
    private val fundIdFirst = 1L
    private val fundIdSecond = 2L
    private val fundIdThird = 3L
    private val partner1 = ProjectPartnerSummary(
        id = partner1Id,
        active = true,
        abbreviation = "PP 2",
        role = ProjectPartnerRole.PARTNER,
        sortNumber = 2
    )
    private val partner2 = ProjectPartnerSummary(
        id = partner2Id,
        active = true,
        abbreviation = "LP 1",
        role = ProjectPartnerRole.LEAD_PARTNER,
        sortNumber = 1
    )
    private val spfBeneficiary = ProjectPartnerSummary(
        id = spfBeneficiaryId,
        active = true,
        abbreviation = "PP1 SPF",
        role = ProjectPartnerRole.LEAD_PARTNER,
        sortNumber = 1
    )

    private val firstFund = ProgrammeFund(id = fundIdFirst, selected = true)
    private val secondFund = ProgrammeFund(id = fundIdSecond, selected = true)
    private val thirdFund = ProgrammeFund(id = fundIdThird, selected = true)

    private val callDetail = CallDetail(
        id = 1L,
        name = "existing call",
        status = CallStatus.PUBLISHED,
        type = CallType.STANDARD,
        startDate = ZonedDateTime.now().minusDays(1),
        endDateStep1 = null,
        endDate = ZonedDateTime.now().plusDays(1),
        isAdditionalFundAllowed = true,
        lengthOfPeriod = 9,
        description = setOf(
            InputTranslation(language = SystemLanguage.EN, translation = "EN desc"),
            InputTranslation(language = SystemLanguage.SK, translation = "SK desc"),
        ),
        objectives = listOf(
            ProgrammePriority(
                code = "PRIO_CODE",
                objective = ProgrammeObjective.PO1,
                specificObjectives = listOf(
                    ProgrammeSpecificObjective(ProgrammeObjectivePolicy.AdvancedTechnologies, "CODE_ADVA"),
                    ProgrammeSpecificObjective(ProgrammeObjectivePolicy.Digitisation, "CODE_DIGI"),
                )
            )
        ),
        strategies = sortedSetOf(ProgrammeStrategy.EUStrategyBalticSeaRegion, ProgrammeStrategy.AtlanticStrategy),
        funds = sortedSetOf(
        CallFundRate(
            programmeFund = firstFund,
            rate = BigDecimal.TEN,
            adjustable = true
        ), CallFundRate(
                programmeFund = secondFund,
                rate = BigDecimal.TEN,
                adjustable = true
        ), CallFundRate(
                programmeFund = thirdFund,
                rate = BigDecimal.TEN,
                adjustable = true
            )
        ),
        flatRates = sortedSetOf(
            ProjectCallFlatRate(
                type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS,
                rate = 5,
                adjustable = true
            ),
        ),
        lumpSums = listOf(
            ProgrammeLumpSum(splittingAllowed = true),
        ),
        unitCosts = listOf(
            ProgrammeUnitCost(isOneCostCategory = true),
        ),
        applicationFormFieldConfigurations = mutableSetOf(),
        preSubmissionCheckPluginKey = null,
        firstStepPreSubmissionCheckPluginKey = null
    )

    private val spfCallDetail = CallDetail(
        id = 1L,
        name = "SPF call",
        status = CallStatus.PUBLISHED,
        type = CallType.SPF,
        startDate = ZonedDateTime.now().minusDays(1),
        endDateStep1 = null,
        endDate = ZonedDateTime.now().plusDays(1),
        isAdditionalFundAllowed = true,
        lengthOfPeriod = 9,
        description = setOf(
            InputTranslation(language = SystemLanguage.EN, translation = "EN desc"),
            InputTranslation(language = SystemLanguage.SK, translation = "SK desc"),
        ),
        objectives = listOf(
            ProgrammePriority(
                code = "PRIO_CODE",
                objective = ProgrammeObjective.PO1,
                specificObjectives = listOf(
                    ProgrammeSpecificObjective(ProgrammeObjectivePolicy.AdvancedTechnologies, "CODE_ADVA"),
                    ProgrammeSpecificObjective(ProgrammeObjectivePolicy.Digitisation, "CODE_DIGI"),
                )
            )
        ),
        strategies = sortedSetOf(ProgrammeStrategy.EUStrategyBalticSeaRegion, ProgrammeStrategy.AtlanticStrategy),
        funds = sortedSetOf(
            CallFundRate(
                programmeFund = firstFund,
                rate = BigDecimal(80),
                adjustable = false
            ), CallFundRate(
                programmeFund = secondFund,
                rate = BigDecimal(80),
                adjustable = true
            ), CallFundRate(
                programmeFund = thirdFund,
                rate = BigDecimal(80),
                adjustable = true
            )
        ),
        applicationFormFieldConfigurations = mutableSetOf(),
        preSubmissionCheckPluginKey = null,
        firstStepPreSubmissionCheckPluginKey = null
    )

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var callPersistence: CallPersistence

    @MockK
    lateinit var getPartnerBudgetPerPeriod: GetPartnerBudgetPerPeriodInteractor

    @MockK
    lateinit var getCoFinancing: GetCoFinancingInteractor

    @MockK
    lateinit var projectBudgetPersistence: ProjectBudgetPersistence

    @MockK
    lateinit var  budgetCostsPersistence: ProjectPartnerBudgetCostsPersistence

    @InjectMockKs
    private lateinit var getPartnerFundsPerPeriod: GetBudgetFundsPerPeriod

    @Test
    fun `getPartnerFundsPerPeriod - without periods`() {
        val projectId = 1L

        val partnerBudgetPerPeriod = listOf(
            ProjectPartnerBudgetPerPeriod(
                partner1,
                mutableListOf(
                    ProjectPeriodBudget(0, 0, 0, BigDecimal.ZERO, BudgetCostsDetail(), false),
                    ProjectPeriodBudget(255, 0, 0, BigDecimal.ZERO, BudgetCostsDetail(),true)
                ),
                totalPartnerBudget = BigDecimal.ZERO,
                totalPartnerBudgetDetail = BudgetCostsDetail(),
                costType = ProjectPartnerCostType.Management
            ),
        )

        val projectPartnerCoFinancing = listOf(ProjectPartnerCoFinancing(
            ProjectPartnerCoFinancingFundTypeDTO.MainFund,
            ProgrammeFund(id = fundIdFirst, selected = true),
            BigDecimal.valueOf(25)
        ))

        val partnerCoFinancing = mapOf(Pair(partner1Id, projectPartnerCoFinancing))

        every { callPersistence.getCallByProjectId(projectId) } returns callDetail
        every { projectPersistence.getProjectPeriods(projectId) } returns listOf()
        every { getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId).partnersBudgetPerPeriod } returns partnerBudgetPerPeriod
        every { getCoFinancing.getCoFinancingForPartnerList(listOf(partner1Id), projectId) } returns partnerCoFinancing

        Assertions.assertThat(getPartnerFundsPerPeriod.getBudgetFundsPerPeriod(projectId).managementFundsPerPeriod)
            .containsExactlyInAnyOrder(
                ProjectFundBudgetPerPeriod(
                    fund = firstFund,
                    costType = ProjectPartnerCostType.Management,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(255, BigDecimal.ZERO.setScale(2))
                    ),
                    totalFundBudget = BigDecimal.ZERO.setScale(2)
                ),
                ProjectFundBudgetPerPeriod(
                    fund = secondFund,
                    costType = ProjectPartnerCostType.Management,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(255, BigDecimal.ZERO.setScale(2))
                    ),
                    totalFundBudget = BigDecimal.ZERO.setScale(2)
                ),
                ProjectFundBudgetPerPeriod(
                    fund = thirdFund,
                    costType = ProjectPartnerCostType.Management,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(255, BigDecimal.ZERO.setScale(2))
                    ),
                    totalFundBudget = BigDecimal.ZERO.setScale(2)
                )
            )
    }

    @Test
    fun `getPartnerFundsPerPeriod - simple with flat rates`() {
        val projectId = 1L
        val partnerBudgetPerPeriod = listOf(
            ProjectPartnerBudgetPerPeriod(
                partner1,
                mutableListOf(
                    ProjectPeriodBudget(0, 0, 0, BigDecimal.ZERO, BudgetCostsDetail(), false),
                    ProjectPeriodBudget(1, 1, 6, 206.23.toScaledBigDecimal(), BudgetCostsDetail(), false),
                    ProjectPeriodBudget(2, 7, 12, 168.75.toScaledBigDecimal(), BudgetCostsDetail(), false),
                    ProjectPeriodBudget(3, 13, 15, 187.52.toScaledBigDecimal(), BudgetCostsDetail(), false),
                    ProjectPeriodBudget(255, 0, 0, BigDecimal.ZERO, BudgetCostsDetail(), true)
                ),
                totalPartnerBudget = 562.5.toScaledBigDecimal(),
                totalPartnerBudgetDetail = BudgetCostsDetail(),
                costType = ProjectPartnerCostType.Management
            ),
        )

        val projectPartnerCoFinancing = listOf(
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                ProgrammeFund(id = fundIdFirst, selected = true),
                BigDecimal.valueOf(25)
            ),
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                ProgrammeFund(id = fundIdSecond, selected = true),
                BigDecimal.valueOf(33)
            )
        )

        val partnerCoFinancing = mapOf(Pair(partner1Id, projectPartnerCoFinancing))

        every { callPersistence.getCallByProjectId(projectId) } returns callDetail
        every { projectPersistence.getProjectPeriods(projectId) } returns listOf(
            ProjectPeriod(number = 1, start = 1, end = 6),
            ProjectPeriod(number = 2, start = 7, end = 12),
            ProjectPeriod(number = 3, start = 13, end = 15)
        )
        every { getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId).partnersBudgetPerPeriod } returns partnerBudgetPerPeriod
        every { getCoFinancing.getCoFinancingForPartnerList(listOf(partner1Id), projectId) } returns partnerCoFinancing

        Assertions.assertThat(getPartnerFundsPerPeriod.getBudgetFundsPerPeriod(projectId).managementFundsPerPeriod)
            .containsExactlyInAnyOrder(
                ProjectFundBudgetPerPeriod(
                    fund = firstFund,
                    costType = ProjectPartnerCostType.Management,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(1, 51.55.toScaledBigDecimal()),
                        ProjectPeriodFund(2, 42.18.toScaledBigDecimal()),
                        ProjectPeriodFund(3, 46.89.toScaledBigDecimal()),
                        ProjectPeriodFund(255, BigDecimal.ZERO.setScale(2))
                    ),
                    totalFundBudget = 140.62.toScaledBigDecimal()
                ),
                ProjectFundBudgetPerPeriod(
                    fund = secondFund,
                    costType = ProjectPartnerCostType.Management,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(1, 68.05.toScaledBigDecimal()),
                        ProjectPeriodFund(2, 55.68.toScaledBigDecimal()),
                        ProjectPeriodFund(3, 61.89.toScaledBigDecimal()),
                        ProjectPeriodFund(255, BigDecimal.ZERO.setScale(2))
                    ),
                    totalFundBudget = 185.62.toScaledBigDecimal()
                ),
                ProjectFundBudgetPerPeriod(
                    fund = thirdFund,
                    costType = ProjectPartnerCostType.Management,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(1, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(2, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(3, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(255, BigDecimal.ZERO.setScale(2))
                    ),
                    totalFundBudget = BigDecimal.ZERO.setScale(2)
                )
            )
    }

    // this test uses data from testcase TB-354 (uses version2 data without lump sums)
    @Test
    fun `getPartnerFundsPerPeriod - historic version`() {
        val projectId = 1L
        val version = "1.0"

        val partnerBudgetPerPeriod = listOf(
            ProjectPartnerBudgetPerPeriod(
                partner1,
                getProjectPeriods(
                    0.toScaledBigDecimal(),
                    55568.16.toScaledBigDecimal(),
                    699.99.toScaledBigDecimal(),
                    2.48.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    555.59.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                ),
                totalPartnerBudget = 56826.22.toBigDecimal(),
                totalPartnerBudgetDetail = BudgetCostsDetail(),
                costType = ProjectPartnerCostType.Management
            ),
            ProjectPartnerBudgetPerPeriod(
                partner2,
                getProjectPeriods(
                    0.toScaledBigDecimal(),
                    55568.16.toScaledBigDecimal(),
                    699.99.toScaledBigDecimal(),
                    2.48.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    555.59.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                ),
                totalPartnerBudget = 56826.22.toBigDecimal(),
                totalPartnerBudgetDetail = BudgetCostsDetail(),
                costType = ProjectPartnerCostType.Management
            )
        )

        val projectPartnerCoFinancing1 = listOf(
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                ProgrammeFund(id = fundIdFirst, selected = true),
                BigDecimal.valueOf(25)
            ),
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                ProgrammeFund(id = fundIdSecond, selected = true),
                BigDecimal.valueOf(33)
            )
        )

        val projectPartnerCoFinancing2 = listOf(
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                ProgrammeFund(id = fundIdThird, selected = true),
                BigDecimal.valueOf(80)
            )
        )

        val partnerCoFinancing = mapOf(Pair(partner1Id, projectPartnerCoFinancing1), Pair(partner2Id, projectPartnerCoFinancing2))

        every { callPersistence.getCallByProjectId(projectId) } returns callDetail
        every { projectPersistence.getProjectPeriods(projectId, version) } returns listOf(
            ProjectPeriod(number = 1, start = 1, end = 2),
            ProjectPeriod(number = 2, start = 2, end = 3),
            ProjectPeriod(number = 3, start = 3, end = 4),
            ProjectPeriod(number = 4, start = 4, end = 5),
            ProjectPeriod(number = 5, start = 5, end = 6),
            ProjectPeriod(number = 6, start = 6, end = 7),
            ProjectPeriod(number = 7, start = 7, end = 8),
            ProjectPeriod(number = 8, start = 8, end = 9),
            ProjectPeriod(number = 9, start = 9, end = 9),
        )
        every { getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId, version).partnersBudgetPerPeriod } returns partnerBudgetPerPeriod
        every { getCoFinancing.getCoFinancingForPartnerList(listOf(partner1Id, partner2Id), projectId, version) } returns partnerCoFinancing

        Assertions.assertThat(getPartnerFundsPerPeriod.getBudgetFundsPerPeriod(projectId, version).managementFundsPerPeriod)
            .containsExactlyInAnyOrder(
                ProjectFundBudgetPerPeriod(
                    fund = firstFund,
                    costType = ProjectPartnerCostType.Management,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(1, 13892.04.toScaledBigDecimal()),
                        ProjectPeriodFund(2, 174.99.toScaledBigDecimal()),
                        ProjectPeriodFund(3, 0.62.toScaledBigDecimal()),
                        ProjectPeriodFund(4, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(5, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(6, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(7, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(8, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(9, 138.9.toScaledBigDecimal()),
                        ProjectPeriodFund(255, BigDecimal.ZERO.setScale(2))
                    ),
                    totalFundBudget = 14206.55.toScaledBigDecimal()
                ),
                ProjectFundBudgetPerPeriod(
                    fund = secondFund,
                    costType = ProjectPartnerCostType.Management,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(1, 18337.49.toScaledBigDecimal()),
                        ProjectPeriodFund(2, 230.99.toScaledBigDecimal()),
                        ProjectPeriodFund(3, 0.81.toScaledBigDecimal()),
                        ProjectPeriodFund(4, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(5, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(6, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(7, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(8, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(9, 183.36.toScaledBigDecimal()),
                        ProjectPeriodFund(255, BigDecimal.ZERO.setScale(2))
                    ),
                    totalFundBudget = 18752.65.toScaledBigDecimal()
                ),
                ProjectFundBudgetPerPeriod(
                    fund = thirdFund,
                    costType = ProjectPartnerCostType.Management,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(1, 44454.52.toScaledBigDecimal()),
                        ProjectPeriodFund(2, 559.99.toScaledBigDecimal()),
                        ProjectPeriodFund(3, 1.98.toScaledBigDecimal()),
                        ProjectPeriodFund(4, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(5, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(6, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(7, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(8, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(9, 444.48.toScaledBigDecimal()),
                        ProjectPeriodFund(255, BigDecimal.ZERO.setScale(2))
                    ),
                    totalFundBudget = 45460.97.toScaledBigDecimal()
                )
            )
    }

    // this test uses data from testcase TB-354
    @Test
    fun `getPartnerFundsPerPeriod - including lump sums`() {
        val projectId = 1L

        val partnerBudgetPerPeriod = listOf(
            ProjectPartnerBudgetPerPeriod(
                partner1,
                getProjectPeriods(
                    55555.56.toScaledBigDecimal(),
                    55568.16.toScaledBigDecimal(),
                    699.99.toScaledBigDecimal(),
                    2.48.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    555.59.toScaledBigDecimal(),
                    5555.56.toScaledBigDecimal(),
                ),
                totalPartnerBudget = 56826.22.toBigDecimal(),
                totalPartnerBudgetDetail = BudgetCostsDetail(),
                costType = ProjectPartnerCostType.Management
            ),
            ProjectPartnerBudgetPerPeriod(
                partner2,
                getProjectPeriods(
                    55555.56.toScaledBigDecimal(),
                    55568.16.toScaledBigDecimal(),
                    699.99.toScaledBigDecimal(),
                    2.48.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                    555.59.toScaledBigDecimal(),
                    0.toScaledBigDecimal(),
                ),
                totalPartnerBudget = 56826.22.toBigDecimal(),
                totalPartnerBudgetDetail = BudgetCostsDetail(),
                costType = ProjectPartnerCostType.Management
            )
        )

        val projectPartnerCoFinancing1 = listOf(
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                ProgrammeFund(id = fundIdFirst, selected = true),
                BigDecimal.valueOf(25)
            ),
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                ProgrammeFund(id = fundIdSecond, selected = true),
                BigDecimal.valueOf(33)
            )
        )

        val projectPartnerCoFinancing2 = listOf(
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                ProgrammeFund(id = fundIdThird, selected = true),
                BigDecimal.valueOf(80)
            )
        )

        val partnerCoFinancing = mapOf(Pair(partner1Id, projectPartnerCoFinancing1), Pair(partner2Id, projectPartnerCoFinancing2))

        every { callPersistence.getCallByProjectId(projectId) } returns callDetail
        every { projectPersistence.getProjectPeriods(projectId) } returns listOf(
            ProjectPeriod(number = 1, start = 1, end = 2),
            ProjectPeriod(number = 2, start = 2, end = 3),
            ProjectPeriod(number = 3, start = 3, end = 4),
            ProjectPeriod(number = 4, start = 4, end = 5),
            ProjectPeriod(number = 5, start = 5, end = 6),
            ProjectPeriod(number = 6, start = 6, end = 7),
            ProjectPeriod(number = 7, start = 7, end = 8),
            ProjectPeriod(number = 8, start = 8, end = 9),
            ProjectPeriod(number = 9, start = 9, end = 9),
        )
        every { getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId).partnersBudgetPerPeriod } returns partnerBudgetPerPeriod
        every { getCoFinancing.getCoFinancingForPartnerList(listOf(partner1Id, partner2Id), projectId) } returns partnerCoFinancing

        Assertions.assertThat(getPartnerFundsPerPeriod.getBudgetFundsPerPeriod(projectId).managementFundsPerPeriod)
            .containsExactlyInAnyOrder(
                ProjectFundBudgetPerPeriod(
                    fund = firstFund,
                    costType = ProjectPartnerCostType.Management,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, 13888.89.toScaledBigDecimal()),
                        ProjectPeriodFund(1, 13892.04.toScaledBigDecimal()),
                        ProjectPeriodFund(2, 174.99.toScaledBigDecimal()),
                        ProjectPeriodFund(3, 0.62.toScaledBigDecimal()),
                        ProjectPeriodFund(4, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(5, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(6, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(7, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(8, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(9, 138.9.toScaledBigDecimal()),
                        ProjectPeriodFund(255, 1388.89.toScaledBigDecimal())
                    ),
                    totalFundBudget = 29484.33.toScaledBigDecimal()
                ),
                ProjectFundBudgetPerPeriod(
                    fund = secondFund,
                    costType = ProjectPartnerCostType.Management,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, 18333.33.toScaledBigDecimal()),
                        ProjectPeriodFund(1, 18337.49.toScaledBigDecimal()),
                        ProjectPeriodFund(2, 230.99.toScaledBigDecimal()),
                        ProjectPeriodFund(3, 0.81.toScaledBigDecimal()),
                        ProjectPeriodFund(4, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(5, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(6, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(7, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(8, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(9, 183.37.toScaledBigDecimal()),
                        ProjectPeriodFund(255, 1833.33.toScaledBigDecimal())
                    ),
                    totalFundBudget = 38919.32.toScaledBigDecimal()
                ),
                ProjectFundBudgetPerPeriod(
                    fund = thirdFund,
                    costType = ProjectPartnerCostType.Management,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, 44444.44.toScaledBigDecimal()),
                        ProjectPeriodFund(1, 44454.52.toScaledBigDecimal()),
                        ProjectPeriodFund(2, 559.99.toScaledBigDecimal()),
                        ProjectPeriodFund(3, 1.98.toScaledBigDecimal()),
                        ProjectPeriodFund(4, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(5, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(6, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(7, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(8, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(9, 444.49.toScaledBigDecimal()),
                        ProjectPeriodFund(255, BigDecimal.ZERO.setScale(2))
                    ),
                    totalFundBudget = 89905.42.toScaledBigDecimal()
                )
            )
    }

    @Test
    fun `getSpfBudgetForFundsPerPeriod`() {
        val projectId = 1L
        val totalSpfBudget = 3500.toBigDecimal()
        val spfBudgetPerPeriods =  mutableListOf(
            ProjectSpfBudgetPerPeriod(
                periodNumber = 1,
                spfCostPerPeriod = 2000.00.toBigDecimal()
            ),
            ProjectSpfBudgetPerPeriod(
                periodNumber = 2,
                spfCostPerPeriod = 1500.00.toBigDecimal()
            )
        )
        val spfCoFinancing = ProjectPartnerCoFinancingAndContributionSpf(
            finances = listOf(
                ProjectPartnerCoFinancing(
                    ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    ProgrammeFund(id = fundIdFirst, selected = true),
                    percentage = 80.00.toBigDecimal()
                )),
            partnerContributions = listOf(
                ProjectPartnerContributionSpf(
                    spfBeneficiaryId,
                    "ONE",
                    amount = BigDecimal(200)
                )
            )
        )

        val partnerBudgetPerPeriod = listOf(
            ProjectPartnerBudgetPerPeriod(
                spfBeneficiary,
                mutableListOf(),
                totalPartnerBudget = BigDecimal.ZERO,
                totalPartnerBudgetDetail = BudgetCostsDetail(),
                costType = ProjectPartnerCostType.Spf
            ),
            ProjectPartnerBudgetPerPeriod(
                spfBeneficiary,
                mutableListOf(),
                totalPartnerBudget = BigDecimal.ZERO,
                totalPartnerBudgetDetail = BudgetCostsDetail(),
                costType = ProjectPartnerCostType.Spf
            )
        )

        val projectPartnerCoFinancing = listOf(
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                ProgrammeFund(id = fundIdFirst, selected = true),
                BigDecimal.valueOf(80)
            )
        )

        val partnerCoFinancing = mapOf(Pair(spfBeneficiaryId, projectPartnerCoFinancing))

        every { projectBudgetPersistence.getPartnersForProjectId(projectId)} returns listOf(spfBeneficiary)
        every { callPersistence.getCallByProjectId(projectId) } returns spfCallDetail
        every { projectPersistence.getProjectPeriods(projectId) } returns listOf(
            ProjectPeriod(number = 1, start = 1, end = 2),
            ProjectPeriod(number = 2, start = 2, end = 2),
        )
        every { getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId).partnersBudgetPerPeriod } returns partnerBudgetPerPeriod
        every { getCoFinancing.getCoFinancingForPartnerList(listOf(spfBeneficiaryId), projectId) } returns partnerCoFinancing

        every { budgetCostsPersistence.getBudgetSpfCostTotal(spfBeneficiaryId, null) } returns totalSpfBudget
        every { projectBudgetPersistence.getSpfBudgetPerPeriod(spfBeneficiaryId, projectId, null) } returns spfBudgetPerPeriods
        every { getCoFinancing.getSpfCoFinancing(spfBeneficiaryId, null) } returns spfCoFinancing

        Assertions.assertThat(getPartnerFundsPerPeriod.getBudgetFundsPerPeriod(projectId).spfFundsPerPeriod)
            .contains(
                ProjectFundBudgetPerPeriod(
                    fund = firstFund,
                    costType = ProjectPartnerCostType.Spf,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO),
                        ProjectPeriodFund(1, BigDecimal(1600).setScale(2)),
                        ProjectPeriodFund(2, BigDecimal(1200).setScale(2)),
                        ProjectPeriodFund(255, BigDecimal.ZERO)
                    ),
                    totalFundBudget = BigDecimal(2800).setScale(2)
                ),
                ProjectFundBudgetPerPeriod(
                    fund = secondFund,
                    costType = ProjectPartnerCostType.Spf,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO),
                        ProjectPeriodFund(1,BigDecimal.ZERO),
                        ProjectPeriodFund(2, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(255, BigDecimal.ZERO)
                    ),
                    totalFundBudget = BigDecimal.ZERO.setScale(2)
                ),
                ProjectFundBudgetPerPeriod(
                    fund = thirdFund,
                    costType = ProjectPartnerCostType.Spf,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO),
                        ProjectPeriodFund(1,BigDecimal.ZERO),
                        ProjectPeriodFund(2, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(255, BigDecimal.ZERO)
                    ),
                    totalFundBudget = BigDecimal.ZERO.setScale(2)
                )
            )
    }

    @Test
    fun `getSpfBudgetForFundsPerPeriod - all budget is set to last period`() {
        val projectId = 1L
        val totalSpfBudget = 3500.toBigDecimal()
        val spfBudgetPerPeriods =  mutableListOf(
            ProjectSpfBudgetPerPeriod(
                periodNumber = 1,
                spfCostPerPeriod = 0.00.toBigDecimal()
            ),
            ProjectSpfBudgetPerPeriod(
                periodNumber = 2,
                spfCostPerPeriod = 0.00.toBigDecimal()
            )
        )
        val spfCoFinancing = ProjectPartnerCoFinancingAndContributionSpf(
            finances = listOf(
                ProjectPartnerCoFinancing(
                    ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    ProgrammeFund(id = fundIdFirst, selected = true),
                    percentage = 80.00.toBigDecimal()
                )),
            partnerContributions = listOf(
                ProjectPartnerContributionSpf(
                    spfBeneficiaryId,
                    "ONE",
                    amount = BigDecimal(200)
                )
            )
        )

        val partnerBudgetPerPeriod = listOf(
            ProjectPartnerBudgetPerPeriod(
                spfBeneficiary,
                mutableListOf(),
                totalPartnerBudget = BigDecimal.ZERO,
                totalPartnerBudgetDetail = BudgetCostsDetail(),
                costType = ProjectPartnerCostType.Spf
            ),
            ProjectPartnerBudgetPerPeriod(
                spfBeneficiary,
                mutableListOf(),
                totalPartnerBudget = BigDecimal.ZERO,
                totalPartnerBudgetDetail = BudgetCostsDetail(),
                costType = ProjectPartnerCostType.Spf
            )
        )

        val projectPartnerCoFinancing = listOf(
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                ProgrammeFund(id = fundIdFirst, selected = true),
                BigDecimal.valueOf(80)
            )
        )

        val partnerCoFinancing = mapOf(Pair(spfBeneficiaryId, projectPartnerCoFinancing))

        every { projectBudgetPersistence.getPartnersForProjectId(projectId)} returns listOf(spfBeneficiary)
        every { callPersistence.getCallByProjectId(projectId) } returns spfCallDetail
        every { projectPersistence.getProjectPeriods(projectId) } returns listOf(
            ProjectPeriod(number = 1, start = 1, end = 2),
            ProjectPeriod(number = 2, start = 2, end = 2),
        )
        every { getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId).partnersBudgetPerPeriod } returns partnerBudgetPerPeriod
        every { getCoFinancing.getCoFinancingForPartnerList(listOf(spfBeneficiaryId), projectId) } returns partnerCoFinancing

        every { budgetCostsPersistence.getBudgetSpfCostTotal(spfBeneficiaryId, null) } returns totalSpfBudget
        every { projectBudgetPersistence.getSpfBudgetPerPeriod(spfBeneficiaryId, projectId, null) } returns spfBudgetPerPeriods
        every { getCoFinancing.getSpfCoFinancing(spfBeneficiaryId, null) } returns spfCoFinancing

        Assertions.assertThat(getPartnerFundsPerPeriod.getBudgetFundsPerPeriod(projectId).spfFundsPerPeriod)
            .contains(
                ProjectFundBudgetPerPeriod(
                    fund = firstFund,
                    costType = ProjectPartnerCostType.Spf,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO),
                        ProjectPeriodFund(1, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(2, BigDecimal(2800).setScale(2)),
                        ProjectPeriodFund(255, BigDecimal.ZERO)
                    ),
                    totalFundBudget = BigDecimal(2800).setScale(2)
                ),
                ProjectFundBudgetPerPeriod(
                    fund = secondFund,
                    costType = ProjectPartnerCostType.Spf,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO),
                        ProjectPeriodFund(1,BigDecimal.ZERO),
                        ProjectPeriodFund(2, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(255, BigDecimal.ZERO)
                    ),
                    totalFundBudget = BigDecimal.ZERO.setScale(2)
                ),
                ProjectFundBudgetPerPeriod(
                    fund = thirdFund,
                    costType = ProjectPartnerCostType.Spf,
                    periodFunds = mutableListOf(
                        ProjectPeriodFund(0, BigDecimal.ZERO),
                        ProjectPeriodFund(1,BigDecimal.ZERO),
                        ProjectPeriodFund(2, BigDecimal.ZERO.setScale(2)),
                        ProjectPeriodFund(255, BigDecimal.ZERO)
                    ),
                    totalFundBudget = BigDecimal.ZERO.setScale(2)
                )
            )
    }

    private fun getProjectPeriods(
        preparation: BigDecimal,
        total1: BigDecimal,
        total2: BigDecimal,
        total3: BigDecimal,
        total4: BigDecimal,
        total5: BigDecimal,
        total6: BigDecimal,
        total7: BigDecimal,
        total8: BigDecimal,
        total9: BigDecimal,
        closure: BigDecimal,
    ): MutableList<ProjectPeriodBudget> = mutableListOf(
        ProjectPeriodBudget(
            periodNumber = 0,
            periodStart = 0,
            periodEnd = 0,
            totalBudgetPerPeriod = preparation,
            lastPeriod = false,
            budgetPerPeriodDetail = BudgetCostsDetail()
        ),
        ProjectPeriodBudget(
            periodNumber = 1,
            periodStart = 1,
            periodEnd = 2,
            totalBudgetPerPeriod = total1,
            lastPeriod = false,
            budgetPerPeriodDetail = BudgetCostsDetail()
        ),
        ProjectPeriodBudget(
            periodNumber = 2,
            periodStart = 2,
            periodEnd = 3,
            totalBudgetPerPeriod = total2,
            lastPeriod = false,
            budgetPerPeriodDetail = BudgetCostsDetail()
        ),
        ProjectPeriodBudget(
            periodNumber = 3,
            periodStart = 3,
            periodEnd = 4,
            totalBudgetPerPeriod = total3,
            lastPeriod = false,
            budgetPerPeriodDetail = BudgetCostsDetail()
        ),
        ProjectPeriodBudget(
            periodNumber = 4,
            periodStart = 4,
            periodEnd = 5,
            totalBudgetPerPeriod = total4,
            lastPeriod = false,
            budgetPerPeriodDetail = BudgetCostsDetail()
        ),
        ProjectPeriodBudget(
            periodNumber = 5,
            periodStart = 5,
            periodEnd = 6,
            totalBudgetPerPeriod = total5,
            lastPeriod = false,
            budgetPerPeriodDetail = BudgetCostsDetail()
        ),
        ProjectPeriodBudget(
            periodNumber = 6,
            periodStart = 6,
            periodEnd = 7,
            totalBudgetPerPeriod = total6,
            lastPeriod = false,
            budgetPerPeriodDetail = BudgetCostsDetail()
        ),
        ProjectPeriodBudget(
            periodNumber = 7,
            periodStart = 7,
            periodEnd = 8,
            totalBudgetPerPeriod = total7,
            lastPeriod = false,
            budgetPerPeriodDetail = BudgetCostsDetail()
        ),
        ProjectPeriodBudget(
            periodNumber = 8,
            periodStart = 8,
            periodEnd = 9,
            totalBudgetPerPeriod = total8,
            lastPeriod = false,
            budgetPerPeriodDetail = BudgetCostsDetail()
        ),
        ProjectPeriodBudget(
            periodNumber = 9,
            periodStart = 9,
            periodEnd = 9,
            totalBudgetPerPeriod = total9,
            lastPeriod = false,
            budgetPerPeriodDetail = BudgetCostsDetail()
        ),
        ProjectPeriodBudget(
            periodNumber = 255,
            periodStart = 0,
            periodEnd = 0,
            totalBudgetPerPeriod = closure,
            lastPeriod = true,
            budgetPerPeriodDetail = BudgetCostsDetail()
        )
    )
}
