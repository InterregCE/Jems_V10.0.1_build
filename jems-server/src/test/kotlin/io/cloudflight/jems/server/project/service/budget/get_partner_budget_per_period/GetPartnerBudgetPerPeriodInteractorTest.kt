package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.PartnersAggregatedInfo
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudget
import io.cloudflight.jems.server.project.service.budget.model.ProjectSpfBudgetPerPeriod
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.BudgetCostsDetail
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerCostType
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.PartnerTotalBudgetPerCostCategory
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

class GetPartnerBudgetPerPeriodInteractorTest : UnitTest() {

    private val partner1Id = 1L
    private val partner2Id = 2L
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

    private fun lumpSumEntry(partnerId: Long, amount: BigDecimal) = ProjectLumpSum(
        programmeLumpSumId = 2L,
        period = 1,
        lumpSumContributions = listOf(ProjectPartnerLumpSum(partnerId, amount))
    )

    private fun projectPeriods() = listOf(
        ProjectPeriod(number = 1, start = 1, end = 2),
        ProjectPeriod(number = 2, start = 2, end = 2)
    )

    @MockK
    lateinit var persistence: ProjectBudgetPersistence

    @MockK
    lateinit var optionPersistence: ProjectPartnerBudgetOptionsPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var lumpSumPersistence: ProjectLumpSumPersistence

    @MockK
    lateinit var calculatePartnerBudgetPerPeriod: PartnerBudgetPerPeriodCalculator

    @MockK
    lateinit var budgetCostsPersistence: ProjectPartnerBudgetCostsPersistence

    @MockK
    lateinit var callPersistence: CallPersistence

    @InjectMockKs
    private lateinit var getPartnerBudgetPerPeriod: GetPartnerBudgetPerPeriod

    @Test
    fun `getPartnerBudgetPerPeriod - historic version`() {
        val projectId = 1L
        val version = "1.0"

        val partnerTotal1 = PartnerTotalBudgetPerCostCategory(
            partner1Id,
            null,
            null,
            null,
            null,
            null,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        )

        val partnerTotal2 = PartnerTotalBudgetPerCostCategory(
            partner2Id,
            null,
            null,
            null,
            null,
            null,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        )
        // partner 1
        val p1budgetPeriod1 = ProjectPartnerBudget(
            id = partner1Id,
            periodNumber = 1,
            staffCostsPerPeriod = 50.toScaledBigDecimal(),
            externalExpertiseAndServicesCostsPerPeriod = 100.toScaledBigDecimal()
        )
        val p1budgetPeriod2 = ProjectPartnerBudget(
            id = partner1Id,
            periodNumber = 2,
            staffCostsPerPeriod = 50.toScaledBigDecimal(),
            infrastructureAndWorksCostsPerPeriod = 100.toScaledBigDecimal()
        )
        // partner 2
        val p2budgetPeriod1 = ProjectPartnerBudget(
            id = partner2Id,
            periodNumber = 1,
            staffCostsPerPeriod = 100.toScaledBigDecimal(),
            externalExpertiseAndServicesCostsPerPeriod = 50.toScaledBigDecimal()
        )

        val budgetOptions = listOf(
            ProjectPartnerBudgetOptions(
                partnerId = partner1Id,
                officeAndAdministrationOnDirectCostsFlatRate = 10,
                travelAndAccommodationOnStaffCostsFlatRate = 10
            )
        )
        val expectedResult = ProjectBudgetOverviewPerPartnerPerPeriod(
            partnersBudgetPerPeriod = listOf(
                ProjectPartnerBudgetPerPeriod(
                    partner = partner1,
                    periodBudgets = getProjectPeriods(170.00.toScaledBigDecimal(), 130.00.toScaledBigDecimal()),
                    totalPartnerBudget = 300.toScaledBigDecimal(),
                    totalPartnerBudgetDetail = BudgetCostsDetail(),
                    costType = ProjectPartnerCostType.Management
                ),
                ProjectPartnerBudgetPerPeriod(
                    partner = partner2,
                    periodBudgets = getProjectPeriods(250.toScaledBigDecimal(), 0.toScaledBigDecimal()),
                    totalPartnerBudget = 250.toScaledBigDecimal(),
                    totalPartnerBudgetDetail = BudgetCostsDetail(),
                    costType = ProjectPartnerCostType.Management
                )
            ),
            totals = listOf(),
            totalsPercentage = listOf()
        )

        val call = CallDetail(
            id = 1,
            name = "call",
            status = CallStatus.PUBLISHED,
            type = CallType.STANDARD,
            startDate = ZonedDateTime.now(),
            endDateStep1 = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = null,
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            projectDefinedUnitCostAllowed = false,
            projectDefinedLumpSumAllowed = true,
        )

        every { callPersistence.getCallByProjectId(projectId)} returns call
        every { persistence.getPartnersForProjectId(projectId, version) } returns listOf(partner1, partner2)
        every {
            optionPersistence.getBudgetOptions(
                setOf(partner1Id, partner2Id),
                projectId,
                version
            )
        } returns budgetOptions
        every { lumpSumPersistence.getLumpSums(projectId, version) } returns listOf(
            lumpSumEntry(partner2Id, 100.toBigDecimal())
        )

        every {
            persistence.getBudgetTotalForPartners(
                setOf(partner1Id, partner2Id),
                projectId,
                version
            )
        } returns mapOf(Pair(partner1Id, partnerTotal1), Pair(partner2Id, partnerTotal2))
        every { persistence.getBudgetPerPartner(setOf(partner1Id, partner2Id), projectId, version) } returns
            listOf(p1budgetPeriod1, p1budgetPeriod2, p2budgetPeriod1)
        every { projectPersistence.getProjectPeriods(projectId, version) } returns projectPeriods()

        every {
            calculatePartnerBudgetPerPeriod.calculate(
                PartnersAggregatedInfo(
                    listOf(partner1, partner2),
                    budgetOptions,
                    listOf(p1budgetPeriod1, p1budgetPeriod2, p2budgetPeriod1),
                    mapOf(Pair(partner1Id, partnerTotal1), Pair(partner2Id, partnerTotal2))
                ),
                lumpSums = listOf(
                    lumpSumEntry(partner2Id, 100.toBigDecimal())
                ),
                projectPeriods = projectPeriods(),
                spfPartnerBudgetPerPeriod = emptyList()
            )
        } returns expectedResult


        assertThat(getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId, version))
            .isEqualTo(expectedResult)
    }

    @Test
    fun `getPartnerBudgetPerPeriod - historic version SPF`() {
        val projectId = 1L
        val version = "1.0"

        val partnerTotal2 = PartnerTotalBudgetPerCostCategory(
            partner2Id,
            null,
            null,
            null,
            null,
            null,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        )

        // partner 2
        val p2budgetPeriod1 = ProjectPartnerBudget(
            id = partner2Id,
            periodNumber = 1,
            staffCostsPerPeriod = 100.toScaledBigDecimal(),
            externalExpertiseAndServicesCostsPerPeriod = 50.toScaledBigDecimal()
        )

        val budgetOptions = listOf(
            ProjectPartnerBudgetOptions(
                partnerId = partner1Id,
                officeAndAdministrationOnDirectCostsFlatRate = 10,
                travelAndAccommodationOnStaffCostsFlatRate = 10
            )
        )
        val expectedResult = ProjectBudgetOverviewPerPartnerPerPeriod(
            partnersBudgetPerPeriod = listOf(
                ProjectPartnerBudgetPerPeriod(
                    partner = partner2,
                    periodBudgets = getProjectPeriods(250.toScaledBigDecimal(), 0.toScaledBigDecimal()),
                    totalPartnerBudget = 250.toScaledBigDecimal(),
                    totalPartnerBudgetDetail = BudgetCostsDetail(),
                    costType = ProjectPartnerCostType.Management
                )
            ),
            totals = listOf(),
            totalsPercentage = listOf()
        )

        val projectSpfBudgetPerPeriod1 = ProjectSpfBudgetPerPeriod(
            periodNumber = 1,
            spfCostPerPeriod = BigDecimal.TEN
        )

        val spfCall = CallDetail(
            id = 2,
            name = "spf call",
            status = CallStatus.PUBLISHED,
            type = CallType.SPF,
            startDate = ZonedDateTime.now(),
            endDateStep1 = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 2,
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            projectDefinedUnitCostAllowed = false,
            projectDefinedLumpSumAllowed = true,
        )
        every { callPersistence.getCallByProjectId(projectId)} returns spfCall
        every { persistence.getPartnersForProjectId(projectId, version) } returns listOf(partner2)
        every {
            optionPersistence.getBudgetOptions(
                setOf(partner2Id),
                projectId,
                version
            )
        } returns budgetOptions
        every { lumpSumPersistence.getLumpSums(projectId, version) } returns listOf(
            lumpSumEntry(partner2Id, 100.toBigDecimal())
        )

        every {
            persistence.getBudgetTotalForPartners(
                setOf(partner2Id),
                projectId,
                version
            )
        } returns mapOf(Pair(partner2Id, partnerTotal2))
        every { persistence.getBudgetPerPartner(setOf(partner2Id), projectId, version) } returns
            listOf(p2budgetPeriod1)
        every { projectPersistence.getProjectPeriods(projectId, version) } returns projectPeriods()

        every { persistence.getSpfBudgetPerPeriod(partner2Id, projectId, version) } returns listOf(projectSpfBudgetPerPeriod1)

        every { budgetCostsPersistence.getBudgetSpfCostTotal(partner2Id, version) } returns BigDecimal.TEN

        val projectSpfBudgetPerPeriods = emptyList<ProjectPartnerBudgetPerPeriod>()
        every {
            calculatePartnerBudgetPerPeriod.calculateSpfPartnerBudgetPerPeriod(
                spfBeneficiary = partner2,
                spfBudgetPerPeriod = listOf(projectSpfBudgetPerPeriod1),
                spfTotalBudget = BigDecimal.TEN,
                projectPeriods = projectPeriods()
            )
        } returns projectSpfBudgetPerPeriods

        every {
            calculatePartnerBudgetPerPeriod.calculate(
                PartnersAggregatedInfo(
                    listOf(partner2),
                    budgetOptions,
                    listOf(p2budgetPeriod1),
                    mapOf(Pair(partner2Id, partnerTotal2))
                ),
                lumpSums = listOf(
                    lumpSumEntry(partner2Id, 100.toBigDecimal())
                ),
                projectPeriods = projectPeriods(),
                spfPartnerBudgetPerPeriod = emptyList()
            )
        } returns expectedResult

        assertThat(getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId, version))
            .isEqualTo(expectedResult)
    }

    private fun getProjectPeriods(total1: BigDecimal, total2: BigDecimal): MutableList<ProjectPeriodBudget> =
        mutableListOf(
            ProjectPeriodBudget(
                periodNumber = 0,
                periodStart = 0,
                periodEnd = 0,
                totalBudgetPerPeriod = BigDecimal.ZERO,
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
                periodEnd = 2,
                totalBudgetPerPeriod = total2,
                lastPeriod = false,
                budgetPerPeriodDetail = BudgetCostsDetail()
            ),
            ProjectPeriodBudget(
                periodNumber = 255,
                periodStart = 0,
                periodEnd = 0,
                totalBudgetPerPeriod = BigDecimal.ZERO,
                lastPeriod = true,
                budgetPerPeriodDetail = BudgetCostsDetail()
            )
        )
}
