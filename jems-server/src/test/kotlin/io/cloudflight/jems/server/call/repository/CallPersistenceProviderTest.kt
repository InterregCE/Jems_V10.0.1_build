package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.Digitisation
import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.AtlanticStrategy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.EUStrategyBalticSeaRegion
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.callFundRate
import io.cloudflight.jems.server.call.callFundRateEntity
import io.cloudflight.jems.server.call.createCallDetailModel
import io.cloudflight.jems.server.call.createTestCallEntity
import io.cloudflight.jems.server.call.entity.ApplicationFormFieldConfigurationEntity
import io.cloudflight.jems.server.call.entity.ApplicationFormFieldConfigurationId
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.CallTranslEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.call.entity.ProjectCallStateAidEntity
import io.cloudflight.jems.server.call.entity.StateAidSetupId
import io.cloudflight.jems.server.call.service.model.AllowedRealCosts
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidEntity
import io.cloudflight.jems.server.programme.repository.StrategyRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.repository.costoption.combineLumpSumTranslatedValues
import io.cloudflight.jems.server.programme.repository.costoption.combineUnitCostTranslatedValues
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.priority.ProgrammeSpecificObjectiveRepository
import io.cloudflight.jems.server.programme.repository.stateaid.ProgrammeStateAidRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.Optional

@ExtendWith(MockKExtension::class)
internal class CallPersistenceProviderTest {

    companion object {
        private const val CALL_ID = 15L
        private const val PROJECT_ID = 1L
        private const val FUND_ID = 24L
        private const val STATE_AID_ID = 23L
        private const val LUMP_SUM_ID = 4L
        private const val UNIT_COST_ID = 3L
        private const val PLUGIN_KEY = "plugin-key"

        private fun applicationFormFieldConfigurationEntities(callEntity: CallEntity) = mutableSetOf(
            ApplicationFormFieldConfigurationEntity(
                ApplicationFormFieldConfigurationId("fieldId", callEntity),
                FieldVisibilityStatus.STEP_ONE_AND_TWO
            )
        )

        private fun stateAidEntities(callEntity: CallEntity) = mutableSetOf(
            ProjectCallStateAidEntity(
                StateAidSetupId(callEntity, stateAid)
            )
        )

        val specificObjectives = setOf(
            ProgrammeSpecificObjectiveEntity(
                programmeObjectivePolicy = Digitisation, code = "CODE_DIGI",
                programmePriority = ProgrammePriorityEntity(code = "PRIO_CODE", objective = Digitisation.objective)
            ),
            ProgrammeSpecificObjectiveEntity(
                programmeObjectivePolicy = AdvancedTechnologies, code = "CODE_ADVA",
                programmePriority = ProgrammePriorityEntity(
                    code = "PRIO_CODE",
                    objective = AdvancedTechnologies.objective
                )
            ),
        )

        val strategies = setOf(
            ProgrammeStrategyEntity(EUStrategyBalticSeaRegion, true),
            ProgrammeStrategyEntity(AtlanticStrategy, true)
        )

        var fund = callFundRateEntity(createTestCallEntity(CALL_ID), FUND_ID)

        val stateAid = ProgrammeStateAidEntity(
            id = STATE_AID_ID,
            measure = ProgrammeStateAidMeasure.OTHER_1,
            maxIntensity = BigDecimal.ZERO,
            threshold = BigDecimal.ZERO,
            schemeNumber = ""
        )

        private fun callEntity(id: Long): CallEntity {
            val call = createTestCallEntity(id)
            fund = callFundRateEntity(call, FUND_ID)
            call.preSubmissionCheckPluginKey = PLUGIN_KEY
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
                    categories = mutableSetOf(
                        ProgrammeLumpSumBudgetCategoryEntity(
                            programmeLumpSumId = LUMP_SUM_ID,
                            category = BudgetCategory.InfrastructureCosts
                        )
                    ),
                )
            )
            call.unitCosts.clear()
            call.unitCosts.add(
                ProgrammeUnitCostEntity(
                    id = UNIT_COST_ID,
                    costPerUnit = BigDecimal.TEN,
                    isOneCostCategory = true,
                    categories = mutableSetOf(
                        ProgrammeUnitCostBudgetCategoryEntity(
                            programmeUnitCostId = UNIT_COST_ID,
                            category = BudgetCategory.InfrastructureCosts
                        )
                    )
                )
            )
            return call
        }

        private val expectedStandardCallDetail = createCallDetailModel(
            id = CALL_ID,
            name = "Test call name",
            funds = sortedSetOf(callFundRate(FUND_ID)),
            preSubmissionCheckPluginKey = PLUGIN_KEY
        )

        private val expectedSPFCallDetail = createCallDetailModel(
            id = CALL_ID,
            name = "Test call name",
            type = CallType.SPF,
            funds = sortedSetOf(callFundRate(FUND_ID))
        )

        private val expectedCall = CallSummary(
            id = CALL_ID,
            name = expectedStandardCallDetail.name,
            status = expectedStandardCallDetail.status,
            startDate = expectedStandardCallDetail.startDate,
            endDate = expectedStandardCallDetail.endDate,
            endDateStep1 = null
        )

        private val callUpdate = Call(
            name = expectedStandardCallDetail.name,
            status = expectedStandardCallDetail.status,
            type = expectedStandardCallDetail.type,
            startDate = expectedStandardCallDetail.startDate,
            endDate = expectedStandardCallDetail.endDate,
            isAdditionalFundAllowed = expectedStandardCallDetail.isAdditionalFundAllowed,
            lengthOfPeriod = expectedStandardCallDetail.lengthOfPeriod!!,
            description = setOf(InputTranslation(SystemLanguage.EN, "This is a dummy call")),
            priorityPolicies = setOf(Digitisation, AdvancedTechnologies),
            strategies = expectedStandardCallDetail.strategies,
            funds = setOf(callFundRate(FUND_ID)),
            stateAidIds = setOf(STATE_AID_ID)
        )

        private val spfCallUpdate = Call(
            name = expectedSPFCallDetail.name,
            status = expectedSPFCallDetail.status,
            type = expectedSPFCallDetail.type,
            startDate = expectedSPFCallDetail.startDate,
            endDate = expectedSPFCallDetail.endDate,
            isAdditionalFundAllowed = expectedSPFCallDetail.isAdditionalFundAllowed,
            lengthOfPeriod = expectedSPFCallDetail.lengthOfPeriod!!,
            description = setOf(InputTranslation(SystemLanguage.EN, "This is a dummy call")),
            priorityPolicies = setOf(Digitisation, AdvancedTechnologies),
            strategies = expectedSPFCallDetail.strategies,
            funds = setOf(callFundRate(FUND_ID)),
            stateAidIds = setOf(STATE_AID_ID)
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
    private lateinit var applicationFormFieldConfigurationRepository: ApplicationFormFieldConfigurationRepository

    @MockK
    private lateinit var programmeFundRepo: ProgrammeFundRepository

    @MockK
    private lateinit var projectCallStateAidRepository: ProjectCallStateAidRepository

    @MockK
    private lateinit var programmeStateAidRepo: ProgrammeStateAidRepository

    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    private lateinit var persistence: CallPersistenceProvider

    @Test
    fun getCalls() {
        every { callRepo.findAll(any<Pageable>()) } returns PageImpl(listOf(callEntity(CALL_ID)))
        assertThat(persistence.getCalls(Pageable.unpaged()).content).containsExactly(expectedCall)
    }

    @Test
    fun `should return list of calls id,name pair`() {
        val callEntity = callEntity(CALL_ID)
        every { callRepo.findAll() } returns listOf(callEntity)
        assertThat(persistence.listCalls()).containsExactly(IdNamePair(callEntity.id, callEntity.name))
    }

    @Test
    fun `should save set of application form field configurations for the call`() {
        val callEntity = callEntity(CALL_ID)
        val newConfigs = mutableSetOf(
            ApplicationFormFieldConfiguration("fieldId-1", FieldVisibilityStatus.STEP_ONE_AND_TWO),
            ApplicationFormFieldConfiguration("fieldId-2", FieldVisibilityStatus.STEP_ONE_AND_TWO)
        )
        every { callRepo.findById(CALL_ID) } returns Optional.of(callEntity)
        every { projectCallStateAidRepository.findAllByIdCallId(CALL_ID) } returns stateAidEntities(callEntity)
        every { applicationFormFieldConfigurationRepository.saveAll(any<MutableSet<ApplicationFormFieldConfigurationEntity>>()) } returns newConfigs.toEntities(
            callEntity
        ).toList()
        assertThat(
            persistence.saveApplicationFormFieldConfigurations(
                CALL_ID,
                newConfigs
            ).applicationFormFieldConfigurations
        ).containsAll(newConfigs)
    }

    @Test
    fun `should return set of application form field configurations for the call`() {
        val fieldId = "id"
        val configEntity = ApplicationFormFieldConfigurationEntity(
            ApplicationFormFieldConfigurationId(
                fieldId, callEntity(CALL_ID)
            ), FieldVisibilityStatus.STEP_ONE_AND_TWO
        )
        val config = ApplicationFormFieldConfiguration(fieldId, FieldVisibilityStatus.STEP_ONE_AND_TWO)
        every { applicationFormFieldConfigurationRepository.findAllByCallId(CALL_ID) } returns mutableSetOf(configEntity)
        assertThat(persistence.getApplicationFormFieldConfigurations(CALL_ID)).containsExactly(config)
    }


    @Test
    fun `should return pre-submission check settings of for the call`() {
        val callEntity = callEntity(CALL_ID)
        val applicationFormConfigEntity = ApplicationFormFieldConfigurationEntity(
            ApplicationFormFieldConfigurationId(
                "fieldId", callEntity(
                    CALL_ID
                )
            ), FieldVisibilityStatus.STEP_ONE_AND_TWO
        )
        every { callRepo.findById(CALL_ID) } returns Optional.of(callEntity)
        every { projectCallStateAidRepository.findAllByIdCallId(CALL_ID) } returns stateAidEntities(callEntity)
        every { applicationFormFieldConfigurationRepository.findAllByCallId(CALL_ID) } returns mutableSetOf(applicationFormConfigEntity)
        assertThat(persistence.updateProjectCallPreSubmissionCheckPlugin(CALL_ID, PLUGIN_KEY)).isEqualTo(expectedStandardCallDetail)
    }


    @Test
    fun `should throw CallNotFound while setting pre-submission check settings for the call and call does not exist`() {
        every { callRepo.findById(CALL_ID) } returns Optional.empty()
        assertThrows<CallNotFound> { persistence.updateProjectCallPreSubmissionCheckPlugin(CALL_ID, PLUGIN_KEY)}
    }

    @Test
    fun getPublishedAndOpenCalls() {
        val slotStatus = slot<CallStatus>()
        every { callRepo.findAllByStatusAndEndDateAfter(capture(slotStatus), any(), any()) } returns PageImpl(
            listOf(callEntity(CALL_ID))
        )
        assertThat(persistence.getPublishedAndOpenCalls(Pageable.unpaged()).content).containsExactly(expectedCall)
        assertThat(slotStatus.captured).isEqualTo(CallStatus.PUBLISHED)
    }

    @Test
    fun getCallById() {
        val callEntity = callEntity(CALL_ID)
        every { callRepo.findById(CALL_ID) } returns Optional.of(callEntity)
        every { applicationFormFieldConfigurationRepository.findAllByCallId(CALL_ID) } returns applicationFormFieldConfigurationEntities(
            callEntity
        )
        every { projectCallStateAidRepository.findAllByIdCallId(CALL_ID) } returns stateAidEntities(callEntity)
        assertThat(persistence.getCallById(CALL_ID)).isEqualTo(expectedStandardCallDetail)
    }

    @Test
    fun `getCallById - not existing`() {
        every { callRepo.findById(-1) } returns Optional.empty()
        assertThrows<CallNotFound> { persistence.getCallById(-1) }
    }

    @Test
    fun `should return call detail by project id`() {
        val callEntity = callEntity(CALL_ID)
        every { callRepo.findById(CALL_ID) } returns Optional.of(callEntity)
        every { projectPersistence.getCallIdOfProject(PROJECT_ID) } returns CALL_ID
        every { applicationFormFieldConfigurationRepository.findAllByCallId(CALL_ID) } returns applicationFormFieldConfigurationEntities(
            callEntity
        )
        every { projectCallStateAidRepository.findAllByIdCallId(CALL_ID) } returns stateAidEntities(callEntity)
        assertThat(persistence.getCallByProjectId(PROJECT_ID)).isEqualTo(expectedStandardCallDetail)
    }

    @Test
    fun `should throw CallNotFound when call does not exist`() {
        every { projectPersistence.getCallIdOfProject(PROJECT_ID) } returns CALL_ID
        every { callRepo.findById(CALL_ID) } returns Optional.empty()
        assertThrows<CallNotFound> {
            persistence.getCallByProjectId(PROJECT_ID)
        }
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
        expectedResultEntity.translatedValues.add(
            CallTranslEntity(
                translationId = TranslationId(
                    expectedResultEntity,
                    language = SystemLanguage.EN
                ), "This is a dummy call"
            )
        )

        every { userRepo.getOne(expectedResultEntity.creator.id) } returns expectedResultEntity.creator
        every { programmeSpecificObjectiveRepo.getOne(Digitisation) } returns specificObjectives.first { it.programmeObjectivePolicy == Digitisation }
        every { programmeSpecificObjectiveRepo.getOne(AdvancedTechnologies) } returns specificObjectives.first { it.programmeObjectivePolicy == AdvancedTechnologies }
        every {
            programmeStrategyRepo.getAllByStrategyInAndActiveTrue(
                setOf(
                    EUStrategyBalticSeaRegion,
                    AtlanticStrategy
                )
            )
        } returns strategies
        every { programmeFundRepo.getTop20ByIdInAndSelectedTrue(setOf(FUND_ID)) } returns setOf(fund.setupId.programmeFund)
        every { applicationFormFieldConfigurationRepository.findAllByCallId(expectedResultEntity.id) } returns applicationFormFieldConfigurationEntities(
            expectedResultEntity
        )
        every { projectCallStateAidRepository.findAllByIdCallId(expectedResultEntity.id) } returns stateAidEntities(
            expectedResultEntity
        )
        val slotCall = slot<CallEntity>()
        every { callRepo.save(capture(slotCall)) } returnsArgument 0

        persistence.createCall(callUpdate, expectedResultEntity.creator.id)
        with(slotCall.captured) {
            assertThat(id).isEqualTo(expectedResultEntity.id)
            assertThat(creator).isEqualTo(expectedResultEntity.creator)
            assertThat(name).isEqualTo(expectedResultEntity.name)
            assertThat(status).isEqualTo(expectedResultEntity.status)
            assertThat(type).isEqualTo(expectedResultEntity.type)
            assertThat(startDate).isEqualTo(expectedResultEntity.startDate)
            assertThat(endDate).isEqualTo(expectedResultEntity.endDate)
            assertThat(lengthOfPeriod).isEqualTo(expectedResultEntity.lengthOfPeriod)
            assertThat(isAdditionalFundAllowed).isEqualTo(expectedResultEntity.isAdditionalFundAllowed)
            assertThat(prioritySpecificObjectives).containsExactlyInAnyOrderElementsOf(specificObjectives)
            assertThat(funds.map { it.setupId.programmeFund }).containsExactly(fund.setupId.programmeFund)
            assertThat(strategies).containsExactlyInAnyOrderElementsOf(strategies)
            assertThat(flatRates).isEmpty()
            assertThat(lumpSums).isEmpty()
            assertThat(unitCosts).isEmpty()
        }
    }

    @Test
    fun createSPFCall(){
        val expectedResultEntity = createTestCallEntity(0L, type = CallType.SPF)
        expectedResultEntity.translatedValues.clear()
        expectedResultEntity.translatedValues.add(
            CallTranslEntity(
                translationId = TranslationId(
                    expectedResultEntity,
                    language = SystemLanguage.EN
                ), "This is a dummy call"
            )
        )

        every { userRepo.getOne(expectedResultEntity.creator.id) } returns expectedResultEntity.creator
        every { programmeSpecificObjectiveRepo.getOne(Digitisation) } returns specificObjectives.first { it.programmeObjectivePolicy == Digitisation }
        every { programmeSpecificObjectiveRepo.getOne(AdvancedTechnologies) } returns specificObjectives.first { it.programmeObjectivePolicy == AdvancedTechnologies }
        every {
            programmeStrategyRepo.getAllByStrategyInAndActiveTrue(
                setOf(
                    EUStrategyBalticSeaRegion,
                    AtlanticStrategy
                )
            )
        } returns strategies
        every { programmeFundRepo.getTop20ByIdInAndSelectedTrue(setOf(FUND_ID)) } returns setOf(fund.setupId.programmeFund)
        every { applicationFormFieldConfigurationRepository.findAllByCallId(expectedResultEntity.id) } returns applicationFormFieldConfigurationEntities(
            expectedResultEntity
        )
        every { projectCallStateAidRepository.findAllByIdCallId(expectedResultEntity.id) } returns stateAidEntities(
            expectedResultEntity
        )

        val slotCall = slot<CallEntity>()
        every { callRepo.save(capture(slotCall)) } returnsArgument 0

        persistence.createCall(spfCallUpdate, expectedResultEntity.creator.id)
        with(slotCall.captured) {
            assertThat(id).isEqualTo(expectedResultEntity.id)
            assertThat(creator).isEqualTo(expectedResultEntity.creator)
            assertThat(name).isEqualTo(expectedResultEntity.name)
            assertThat(status).isEqualTo(expectedResultEntity.status)
            assertThat(type).isEqualTo(expectedResultEntity.type)
            assertThat(startDate).isEqualTo(expectedResultEntity.startDate)
            assertThat(endDate).isEqualTo(expectedResultEntity.endDate)
            assertThat(lengthOfPeriod).isEqualTo(expectedResultEntity.lengthOfPeriod)
            assertThat(isAdditionalFundAllowed).isEqualTo(expectedResultEntity.isAdditionalFundAllowed)
            assertThat(prioritySpecificObjectives).containsExactlyInAnyOrderElementsOf(specificObjectives)
            assertThat(funds.map { it.setupId.programmeFund }).containsExactly(fund.setupId.programmeFund)
            assertThat(strategies).containsExactlyInAnyOrderElementsOf(strategies)
            assertThat(flatRates).isEmpty()
            assertThat(lumpSums).isEmpty()
            assertThat(unitCosts).isEmpty()
        }


    }

    @Test
    fun `update flat rates - OK`() {
        val call = createTestCallEntity(1)
        every { callRepo.findById(1L) } returns Optional.of(call)

        every { applicationFormFieldConfigurationRepository.findAllByCallId(call.id) } returns applicationFormFieldConfigurationEntities(
            call
        )
        every { projectCallStateAidRepository.findAllByIdCallId(call.id) } returns stateAidEntities(call)

        persistence.updateProjectCallFlatRate(
            1, setOf(
                ProjectCallFlatRate(
                    type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS,
                    rate = 18,
                    adjustable = false
                ),
            )
        )

        assertThat(call.flatRates).containsExactlyInAnyOrder(
            ProjectCallFlatRateEntity(
                setupId = FlatRateSetupId(call = call, type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS),
                rate = 18,
                isAdjustable = false,
            )
        )
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
        val call = createTestCallEntity(1)
        every { callRepo.findById(1L) } returns Optional.of(call)
        every { programmeLumpSumRepo.findAllById(setOf(2, 3)) } returns listOf(lumpSum2, lumpSum3)
        every { applicationFormFieldConfigurationRepository.findAllByCallId(call.id) } returns applicationFormFieldConfigurationEntities(
            call
        )
        every { projectCallStateAidRepository.findAllByIdCallId(call.id) } returns stateAidEntities(call)
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
        val call = createTestCallEntity(1)
        every { callRepo.findById(1L) } returns Optional.of(call)
        every { programmeUnitCostRepo.findAllById(setOf(2, 3)) } returns listOf(unitCost2, unitCost3)
        every { applicationFormFieldConfigurationRepository.findAllByCallId(1) } returns applicationFormFieldConfigurationEntities(
            call
        )
        every { projectCallStateAidRepository.findAllByIdCallId(1) } returns stateAidEntities(call)
        persistence.updateProjectCallUnitCost(1, setOf(2, 3))
        assertThat(call.unitCosts).containsExactlyInAnyOrder(unitCost2, unitCost3)
    }

    @Test
    fun publishCall() {
        val call = createTestCallEntity(id = 589)
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

    @Test
    fun `update allow real costs`() {
        val call = createTestCallEntity(1)
        every { callRepo.findById(1L) } returns Optional.of(call)

        persistence.updateAllowedRealCosts(1, AllowedRealCosts(true, true, true, true, true))

        verify(exactly = 1) {
            callRepo.findById(1L)
        }
    }

    @Test
    fun `get allow real costs`() {
        every { callRepo.findById(1L) } returns Optional.of(createTestCallEntity(1))
        persistence.getAllowedRealCosts(1)

        verify(exactly = 1) {
            callRepo.findById(1)
        }
    }

}
