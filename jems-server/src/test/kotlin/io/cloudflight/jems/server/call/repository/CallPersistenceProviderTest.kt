package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO1
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.Digitisation
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.AtlanticStrategy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.EUStrategyBalticSeaRegion
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.callWithId
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.CallTranslEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.StrategyRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.repository.costoption.combineLumpSumTranslatedValues
import io.cloudflight.jems.server.programme.repository.costoption.combineUnitCostTranslatedValues
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.priority.ProgrammeSpecificObjectiveRepository
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.Optional

@ExtendWith(MockKExtension::class)
internal class CallPersistenceProviderTest {

    companion object {
        private const val CALL_ID = 15L
        private const val FUND_ID = 24L
        private const val LUMP_SUM_ID = 4L
        private const val UNIT_COST_ID = 3L
        private const val USER_ID = 302L

        private val START = ZonedDateTime.now().withSecond(0).withNano(0)
        private val END = ZonedDateTime.now().plusDays(5).withSecond(0).withNano(0).plusMinutes(1).minusNanos(1)

        private val user = UserEntity(
            id = USER_ID,
            email = "admin@admin.dev",
            name = "Name",
            surname = "Surname",
            userRole = UserRoleEntity(id = 1, name = "ADMIN"),
            password = "hash_pass"
        )

        val specificObjectives = setOf(
            ProgrammeSpecificObjectiveEntity(
                programmeObjectivePolicy = Digitisation, code = "CODE_DIGI",
                programmePriority = ProgrammePriorityEntity(code = "PRIO_CODE", objective = Digitisation.objective)
            ),
            ProgrammeSpecificObjectiveEntity(
                programmeObjectivePolicy = AdvancedTechnologies, code = "CODE_ADVA",
                programmePriority = ProgrammePriorityEntity(code = "PRIO_CODE", objective = AdvancedTechnologies.objective)
            ),
        )

        val strategies = setOf(ProgrammeStrategyEntity(EUStrategyBalticSeaRegion, true), ProgrammeStrategyEntity(AtlanticStrategy, true))

        val fund=ProgrammeFundEntity(id = FUND_ID, selected = true)
        private fun callEntity(id: Long? = null): CallEntity {
            val call = callWithId(id ?: CALL_ID)
            call.startDate = START
            call.endDate = END
            call.prioritySpecificObjectives.clear()
            call.prioritySpecificObjectives.addAll(specificObjectives)
            call.strategies.clear()
            call.strategies.addAll(strategies)
            call.funds.clear()
            call.funds.add(fund)
            call.flatRates.clear()
            call.flatRates.add(
                ProjectCallFlatRateEntity(
                    setupId = FlatRateSetupId(call = call, type = FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS),
                    rate = 15,
                    isAdjustable = true,
                )
            )
            call.lumpSums.clear()
            call.lumpSums.add(
                ProgrammeLumpSumEntity(
                    id = LUMP_SUM_ID,
                    cost = BigDecimal.ONE,
                    splittingAllowed = true,
                    phase = ProgrammeLumpSumPhase.Closure,
                    categories = mutableSetOf(ProgrammeLumpSumBudgetCategoryEntity(programmeLumpSumId = LUMP_SUM_ID, category = BudgetCategory.InfrastructureCosts)),
                )
            )
            call.unitCosts.clear()
            call.unitCosts.add(
                ProgrammeUnitCostEntity(
                    id = UNIT_COST_ID,
                    costPerUnit = BigDecimal.TEN,
                    isOneCostCategory = true,
                    categories = mutableSetOf(ProgrammeUnitCostBudgetCategoryEntity(programmeUnitCostId = UNIT_COST_ID, category = BudgetCategory.InfrastructureCosts))
                )
            )
            return call
        }

        private val expectedCallDetail = CallDetail(
            id = CALL_ID,
            name = "Test call name",
            status = CallStatus.DRAFT,
            startDate = START,
            endDateStep1 = null,
            endDate = END,
            isAdditionalFundAllowed = false,
            lengthOfPeriod = 1,
            description = setOf(InputTranslation(SystemLanguage.EN ,"This is a dummy call")),
            objectives = listOf(ProgrammePriority(
                id = 0L,
                code = "PRIO_CODE",
                objective = PO1,
                specificObjectives = listOf(
                    ProgrammeSpecificObjective(AdvancedTechnologies, "CODE_ADVA"),
                    ProgrammeSpecificObjective(Digitisation, "CODE_DIGI"),
                )
            )),
            strategies = sortedSetOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            funds = listOf(ProgrammeFund(id = FUND_ID, selected = true)),
            flatRates = sortedSetOf(ProjectCallFlatRate(type = FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS, rate = 15, isAdjustable = true)),
            lumpSums = listOf(ProgrammeLumpSum(
                id = LUMP_SUM_ID,
                cost = BigDecimal.ONE,
                splittingAllowed = true,
                phase = ProgrammeLumpSumPhase.Closure,
                categories = setOf(BudgetCategory.InfrastructureCosts),
            )),
            unitCosts = listOf(ProgrammeUnitCost(
                id = UNIT_COST_ID,
                costPerUnit = BigDecimal.TEN,
                isOneCostCategory = true,
                categories = setOf(BudgetCategory.InfrastructureCosts),
            ))
        )

        private val expectedCall = CallSummary(
            id = CALL_ID,
            name = expectedCallDetail.name,
            status = expectedCallDetail.status,
            startDate = expectedCallDetail.startDate,
            endDate = expectedCallDetail.endDate,
            endDateStep1 = null
        )

        private val callUpdate = Call(
            name = expectedCallDetail.name,
            status = expectedCallDetail.status,
            startDate = expectedCallDetail.startDate,
            endDate = expectedCallDetail.endDate,
            isAdditionalFundAllowed = expectedCallDetail.isAdditionalFundAllowed,
            lengthOfPeriod = expectedCallDetail.lengthOfPeriod!!,
            description = setOf(InputTranslation(SystemLanguage.EN ,"This is a dummy call")),
            priorityPolicies = setOf(Digitisation, AdvancedTechnologies),
            strategies = setOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            fundIds = setOf(FUND_ID),
        )

        private val lumpSum2 = ProgrammeLumpSumEntity(
            id = 2,
            translatedValues = combineLumpSumTranslatedValues(
                programmeLumpSumId = 2,
                name = setOf(InputTranslation(SystemLanguage.EN, "testName 2")),
                description = emptySet()
            ),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = ProgrammeLumpSumPhase.Closure,
        )

        private val lumpSum3 = ProgrammeLumpSumEntity(
            id = 3,
            translatedValues = combineLumpSumTranslatedValues(
                programmeLumpSumId = 3,
                name = setOf(InputTranslation(SystemLanguage.EN, "testName 3")),
                description = emptySet()
            ),
            cost = BigDecimal.TEN,
            splittingAllowed = false,
            phase = ProgrammeLumpSumPhase.Preparation,
        )

        private val unitCost2 = ProgrammeUnitCostEntity(
            id = 2,
            translatedValues = combineUnitCostTranslatedValues(
                programmeUnitCostId = 2,
                name = setOf(InputTranslation(SystemLanguage.EN, "testName 2")),
                description = emptySet(),
                type = setOf(InputTranslation(SystemLanguage.EN, "UC 2")),
            ),
            isOneCostCategory = false,
            costPerUnit = BigDecimal.ZERO,
        )

        private val unitCost3 = ProgrammeUnitCostEntity(
            id = 3,
            translatedValues = combineUnitCostTranslatedValues(
                programmeUnitCostId = 3,
                name = setOf(InputTranslation(SystemLanguage.EN, "testName 3")),
                description = emptySet(),
                type = setOf(InputTranslation(SystemLanguage.EN, "UC 3")),
            ),
            isOneCostCategory = false,
            costPerUnit = BigDecimal.ONE,
        )
    }

    @MockK
    private lateinit var callRepo: CallRepository

    @MockK
    private lateinit var userRepo: UserRepository

    @MockK
    private lateinit var programmeLumpSumRepo: ProgrammeLumpSumRepository

    @MockK
    private lateinit var programmeUnitCostRepo: ProgrammeUnitCostRepository

    @MockK
    private lateinit var programmeSpecificObjectiveRepo: ProgrammeSpecificObjectiveRepository

    @MockK
    private lateinit var programmeStrategyRepo: StrategyRepository

    @MockK
    private lateinit var programmeFundRepo: ProgrammeFundRepository

    @InjectMockKs
    private lateinit var persistence: CallPersistenceProvider

    @Test
    fun getCalls() {
        every { callRepo.findAll(any<Pageable>()) } returns PageImpl(listOf(callEntity()))
        assertThat(persistence.getCalls(Pageable.unpaged()).content).containsExactly(expectedCall)
    }

    @Test
    fun getPublishedAndOpenCalls() {
        val slotStatus = slot<CallStatus>()
        every { callRepo.findAllByStatusAndEndDateAfter(capture(slotStatus), any(), any()) } returns PageImpl(listOf(callEntity()))
        assertThat(persistence.getPublishedAndOpenCalls(Pageable.unpaged()).content).containsExactly(expectedCall)
        assertThat(slotStatus.captured).isEqualTo(CallStatus.PUBLISHED)
    }

    @Test
    fun getCallById() {
        every { callRepo.findById(CALL_ID) } returns Optional.of(callEntity())
        assertThat(persistence.getCallById(CALL_ID)).isEqualTo(expectedCallDetail)
    }

    @Test
    fun `getCallById - not existing`() {
        every { callRepo.findById(-1) } returns Optional.empty()
        assertThrows<CallNotFound> { persistence.getCallById(-1) }
    }

    @Test
    fun getCallIdForNameIfExists() {
        val call = callEntity(2L)
        call.name = "name which exists"
        every { callRepo.findFirstByName("name which exists") } returns call
        assertThat(persistence.getCallIdForNameIfExists("name which exists")).isEqualTo(2L)
    }

    @Test
    fun `getCallIdForNameIfExists - not existing name`() {
        every { callRepo.findFirstByName("name which does not exist") } returns null
        assertThat(persistence.getCallIdForNameIfExists("name which does not exist")).isNull()
    }

    @Test
    fun `update flat rates - not-existing call`() {
        every { callRepo.findById(-1L) } returns Optional.empty()
        assertThrows<CallNotFound> { persistence.updateProjectCallFlatRate(-1, emptySet()) }
    }

    @Test
    fun createCall() {
        val expectedResultEntity = callEntity(0L)
        expectedResultEntity.translatedValues.clear()
        expectedResultEntity.translatedValues.add(CallTranslEntity(translationId = TranslationId(expectedResultEntity, language = SystemLanguage.EN), "This is a dummy call"))

        every { userRepo.getOne(expectedResultEntity.creator.id) } returns expectedResultEntity.creator
        every { programmeSpecificObjectiveRepo.getOne(Digitisation) } returns specificObjectives.first { it.programmeObjectivePolicy == Digitisation }
        every { programmeSpecificObjectiveRepo.getOne(AdvancedTechnologies) } returns specificObjectives.first { it.programmeObjectivePolicy == AdvancedTechnologies }
        every { programmeStrategyRepo.getAllByStrategyInAndActiveTrue(setOf(EUStrategyBalticSeaRegion, AtlanticStrategy)) } returns strategies
        every { programmeFundRepo.getTop20ByIdInAndSelectedTrue(setOf(FUND_ID)) } returns setOf(fund)

        val slotCall = slot<CallEntity>()
        every { callRepo.save(capture(slotCall)) } returnsArgument 0

        persistence.createCall(callUpdate, expectedResultEntity.creator.id)
        with (slotCall.captured) {
            assertThat(id).isEqualTo(expectedResultEntity.id)
            assertThat(creator).isEqualTo(expectedResultEntity.creator)
            assertThat(name).isEqualTo(expectedResultEntity.name)
            assertThat(status).isEqualTo(expectedResultEntity.status)
            assertThat(startDate).isEqualTo(expectedResultEntity.startDate)
            assertThat(endDate).isEqualTo(expectedResultEntity.endDate)
            assertThat(lengthOfPeriod).isEqualTo(expectedResultEntity.lengthOfPeriod)
            assertThat(isAdditionalFundAllowed).isEqualTo(expectedResultEntity.isAdditionalFundAllowed)
            assertThat(prioritySpecificObjectives).containsExactlyInAnyOrderElementsOf(specificObjectives)
            assertThat(funds).containsExactly(fund)
            assertThat(strategies).containsExactlyInAnyOrderElementsOf(strategies)
            assertThat(flatRates).isEmpty()
            assertThat(lumpSums).isEmpty()
            assertThat(unitCosts).isEmpty()
        }
    }

    @Test
    fun `update flat rates - OK`() {
        val call = callWithId(1)
        every { callRepo.findById(1L) } returns Optional.of(call)

        persistence.updateProjectCallFlatRate(1, setOf(
            ProjectCallFlatRate(type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS, rate = 18, isAdjustable = false),
        ))

        assertThat(call.flatRates).containsExactlyInAnyOrder(ProjectCallFlatRateEntity(
            setupId = FlatRateSetupId(call = call, type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS),
            rate = 18,
            isAdjustable = false,
        ))
    }

    @Test
    fun existsAllProgrammeLumpSumsByIds() {
        every { programmeLumpSumRepo.findAllById(setOf(56L)) } returns emptyList()
        assertThat(persistence.existsAllProgrammeLumpSumsByIds(setOf(56L))).isFalse

        every { programmeLumpSumRepo.findAllById(setOf(90L)) } returns listOf(lumpSum2.copy(id = 90L))
        assertThat(persistence.existsAllProgrammeLumpSumsByIds(setOf(90L))).isTrue
    }

    @Test
    fun `update lump sum - not-existing call`() {
        every { callRepo.findById(eq(-1)) } returns Optional.empty()
        assertThrows<CallNotFound> { persistence.updateProjectCallLumpSum(-1, emptySet()) }
    }

    @Test
    fun `update lump sum - OK`() {
        val call = callWithId(1)
        every { callRepo.findById(1L) } returns Optional.of(call)
        every { programmeLumpSumRepo.findAllById(setOf(2, 3)) } returns listOf(lumpSum2, lumpSum3)
        persistence.updateProjectCallLumpSum(1, setOf(2, 3))
        assertThat(call.lumpSums).containsExactlyInAnyOrder(lumpSum2, lumpSum3)
    }

    @Test
    fun existsAllProgrammeUnitCostsByIds() {
        every { programmeUnitCostRepo.findAllById(setOf(32L)) } returns emptyList()
        assertThat(persistence.existsAllProgrammeUnitCostsByIds(setOf(32L))).isFalse

        every { programmeUnitCostRepo.findAllById(setOf(17L)) } returns listOf(unitCost2.copy(id = 17L))
        assertThat(persistence.existsAllProgrammeUnitCostsByIds(setOf(17L))).isTrue
    }

    @Test
    fun `update unit cost - not-existing call`() {
        every { callRepo.findById(-1L) } returns Optional.empty()
        assertThrows<CallNotFound> { persistence.updateProjectCallUnitCost(-1, emptySet()) }
    }

    @Test
    fun `update unit cost - OK`() {
        val call = callWithId(1)
        every { callRepo.findById(1L) } returns Optional.of(call)
        every { programmeUnitCostRepo.findAllById(setOf(2, 3)) } returns listOf(unitCost2, unitCost3)
        persistence.updateProjectCallUnitCost(1, setOf(2, 3))
        assertThat(call.unitCosts).containsExactlyInAnyOrder(unitCost2, unitCost3)
    }

    @Test
    fun publishCall() {
        val call = callWithId(id = 589)
        call.status = CallStatus.DRAFT
        every { callRepo.findById(589L) } returns Optional.of(call)
        assertThat(persistence.publishCall(589L).status).isEqualTo(CallStatus.PUBLISHED)
    }

    @Test
    fun `publishCall - not existing`() {
        every { callRepo.findById(-1) } returns Optional.empty()
        assertThrows<CallNotFound> { persistence.publishCall(-1) }
    }

    @Test
    fun `should return false when there is no published call`() {
        every { callRepo.existsByStatus(CallStatus.PUBLISHED) } returns false
        assertThat(persistence.hasAnyCallPublished())
            .isEqualTo(false)
    }

    @Test
    fun `should return true when there is a published call`() {
        every { callRepo.existsByStatus(CallStatus.PUBLISHED) } returns true
        assertThat(persistence.hasAnyCallPublished())
            .isEqualTo(true)
    }

}
