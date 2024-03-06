package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.call.dto.applicationFormConfiguration.ApplicationFormFieldConfigurationDTO
import io.cloudflight.jems.api.call.dto.applicationFormConfiguration.StepSelectionOptionDTO
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
import io.cloudflight.jems.server.call.END
import io.cloudflight.jems.server.call.START
import io.cloudflight.jems.server.call.callFundRate
import io.cloudflight.jems.server.call.callFundRateEntity
import io.cloudflight.jems.server.call.controller.toDto
import io.cloudflight.jems.server.call.createCallDetailModel
import io.cloudflight.jems.server.call.createTestCallEntity
import io.cloudflight.jems.server.call.defaultAllowedRealCostsByCallType
import io.cloudflight.jems.server.call.entity.ApplicationFormFieldConfigurationEntity
import io.cloudflight.jems.server.call.entity.ApplicationFormFieldConfigurationId
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.CallSelectedChecklistEntity
import io.cloudflight.jems.server.call.entity.CallSelectedChecklistId
import io.cloudflight.jems.server.call.entity.CallTranslEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.call.entity.ProjectCallStateAidEntity
import io.cloudflight.jems.server.call.entity.StateAidSetupId
import io.cloudflight.jems.server.call.entity.unitCost.ProjectCallUnitCostEntity
import io.cloudflight.jems.server.call.entity.unitCost.ProjectCallUnitCostId
import io.cloudflight.jems.server.call.service.model.AllowedRealCosts
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.model.CallCostOption
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.call.service.model.PreSubmissionPlugins
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidEntity
import io.cloudflight.jems.server.programme.repository.StrategyRepository
import io.cloudflight.jems.server.programme.repository.checklist.ProgrammeChecklistRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.repository.costoption.combineLumpSumTranslatedValues
import io.cloudflight.jems.server.programme.repository.costoption.combineUnitCostTranslatedValues
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.priority.ProgrammeSpecificObjectiveRepository
import io.cloudflight.jems.server.programme.repository.stateaid.ProgrammeStateAidRepository
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.ZonedDateTime
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
        private const val PLUGIN_KEY_PARTNER_REPORT = "plugin-key-partner-report"
        private const val PLUGIN_KEY_PARTNER_CONTROL_REPORT = "plugin-key-partner-control-report"
        private const val PLUGIN_KEY_PROJECT_REPORT = "plugin-key-project-report"
        private const val PLUGIN_KEY_CONTROL_SAMPLING = "plugin-key-control-sampling"

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

        fun callEntity(id: Long, callType: CallType = CallType.STANDARD): CallEntity {
            val call = createTestCallEntity(id, type = callType, unitCosts = mutableSetOf(unitCost2, unitCost3))
            fund = callFundRateEntity(call, FUND_ID)
            call.preSubmissionCheckPluginKey = PLUGIN_KEY
            call.firstStepPreSubmissionCheckPluginKey = PLUGIN_KEY
            call.reportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_REPORT
            call.controlReportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_CONTROL_REPORT
            call.reportProjectCheckPluginKey = PLUGIN_KEY_PROJECT_REPORT
            call.controlReportSamplingCheckPluginKey = PLUGIN_KEY_CONTROL_SAMPLING
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
                    isFastTrack = false
                )
            )
            call.unitCosts.clear()
            call.unitCosts.add(
                ProgrammeUnitCostEntity(
                    id = UNIT_COST_ID,
                    projectId = null,
                    costPerUnit = BigDecimal.TEN,
                    isOneCostCategory = true,
                    categories = mutableSetOf(
                        ProgrammeUnitCostBudgetCategoryEntity(
                            programmeUnitCostId = UNIT_COST_ID,
                            category = BudgetCategory.InfrastructureCosts
                        )
                    ),
                    costPerUnitForeignCurrency = BigDecimal.ZERO,
                    foreignCurrencyCode = null
                )
            )
            return call
        }

        private val expectedStandardCallDetail = createCallDetailModel(
            id = CALL_ID,
            name = "Test call name",
            funds = sortedSetOf(callFundRate(FUND_ID)),
            preSubmissionCheckPluginKey = PLUGIN_KEY,
            firstStepPreSubmissionCheckPluginKey = PLUGIN_KEY,
            reportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_REPORT,
            reportProjectCheckPluginKey = PLUGIN_KEY_PROJECT_REPORT,
            controlReportPartnerCheckPlugin = PLUGIN_KEY_PARTNER_CONTROL_REPORT,
            controlReportSamplingCheckPluginKey = PLUGIN_KEY_CONTROL_SAMPLING
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
            isDirectContributionsAllowed = expectedStandardCallDetail.isDirectContributionsAllowed,
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
            isDirectContributionsAllowed = expectedSPFCallDetail.isDirectContributionsAllowed,
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
            isFastTrack = false
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
            isFastTrack = false
        )

        private val unitCost2 = ProgrammeUnitCostEntity(
            id = 2,
            projectId = null,
            translatedValues = combineUnitCostTranslatedValues(
                programmeUnitCostId = 2,
                name = setOf(InputTranslation(SystemLanguage.EN, "testName 2")),
                description = emptySet(),
                type = setOf(InputTranslation(SystemLanguage.EN, "UC 2")),
                justification = setOf(InputTranslation(SystemLanguage.EN, "justification 2")),
            ),
            isOneCostCategory = false,
            costPerUnit = BigDecimal.ZERO,
            costPerUnitForeignCurrency = BigDecimal.ZERO,
            foreignCurrencyCode = null
        )

        private val unitCost3 = ProgrammeUnitCostEntity(
            id = 3,
            projectId = null,
            translatedValues = combineUnitCostTranslatedValues(
                programmeUnitCostId = 3,
                name = setOf(InputTranslation(SystemLanguage.EN, "testName 3")),
                description = emptySet(),
                type = setOf(InputTranslation(SystemLanguage.EN, "UC 3")),
                justification = setOf(InputTranslation(SystemLanguage.EN, "justification 3")),
            ),
            isOneCostCategory = false,
            costPerUnit = BigDecimal.ONE,
            costPerUnitForeignCurrency = BigDecimal.ZERO,
            foreignCurrencyCode = null
        )

        private fun callWithCostOption(): CallEntity {
            return CallEntity(
                id = 0L,
                creator = mockk(),
                name = "call",
                status = CallStatus.DRAFT,
                type = CallType.STANDARD,
                startDate = ZonedDateTime.now(),
                endDateStep1 = null,
                endDate = ZonedDateTime.now(),
                prioritySpecificObjectives = mutableSetOf(),
                strategies = mutableSetOf(),
                isAdditionalFundAllowed = false,
                isDirectContributionsAllowed = true,
                funds = mutableSetOf(),
                lengthOfPeriod = 1,
                allowedRealCosts = defaultAllowedRealCostsByCallType(CallType.STANDARD),
                preSubmissionCheckPluginKey = null,
                firstStepPreSubmissionCheckPluginKey = null,
                reportPartnerCheckPluginKey = "check-off",
                reportProjectCheckPluginKey = "check-off",
                projectDefinedUnitCostAllowed = true,
                projectDefinedLumpSumAllowed = false,
                controlReportPartnerCheckPluginKey = "control-report-partner-check-off",
                controlReportSamplingCheckPluginKey = "control-report-sampling-check-off"
            )
        }

        private val programmeChecklists = mutableListOf(
            ProgrammeChecklistEntity(
                id = 1L,
                type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
                name = "af_assesment",
                minScore = BigDecimal(0),
                maxScore = BigDecimal(10),
                allowsDecimalScore = true,
                lastModificationDate = ZonedDateTime.now(),
            ),
            ProgrammeChecklistEntity(
                id = 2L,
                type = ProgrammeChecklistType.VERIFICATION,
                name = "verification",
                minScore = BigDecimal(0),
                maxScore = BigDecimal(100),
                allowsDecimalScore = false,
                lastModificationDate = ZonedDateTime.now(),
            )
        )

        private val selectedChecklists = listOf(
            CallSelectedChecklistEntity(
                id = CallSelectedChecklistId(
                    call = mockk { every { id } returns 10L },
                    programmeChecklist = programmeChecklists[0]
                )
            )
        )

        private val updatedSelectedChecklists = programmeChecklists.map { it.toModel(selected = true) }
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

    @MockK
    private lateinit var partnerRepository: ProjectPartnerRepository

    @MockK
    private lateinit var callSelectedChecklistRepository: CallSelectedChecklistRepository

    @MockK
    private lateinit var programmeChecklistRepository: ProgrammeChecklistRepository

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

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

        assertThat(persistence.listCalls(null)).containsExactly(IdNamePair(callEntity.id, callEntity.name))

        verify { callRepo.findAll() }
    }

    @Test
    fun `should fetch the list of calls with the status`() {
        every { callRepo.findAllByStatus(CallStatus.PUBLISHED) } returns listOf()

        persistence.listCalls(CallStatus.PUBLISHED)

        verify { callRepo.findAllByStatus(CallStatus.PUBLISHED) }
    }

    @Test
    fun `should save set of application form field configurations for the call`() {
        val callEntity = callEntity(CALL_ID)
        val oldConfigs = mutableSetOf(
            ApplicationFormFieldConfigurationEntity(ApplicationFormFieldConfigurationId("fieldId-1", callEntity), FieldVisibilityStatus.NONE),
            ApplicationFormFieldConfigurationEntity(ApplicationFormFieldConfigurationId("fieldId-2", callEntity), FieldVisibilityStatus.NONE)
        )
        val newConfigs = mutableSetOf(
            ApplicationFormFieldConfiguration("fieldId-1", FieldVisibilityStatus.STEP_ONE_AND_TWO),
            ApplicationFormFieldConfiguration("fieldId-2", FieldVisibilityStatus.STEP_ONE_AND_TWO)
        )
        val expectedConfigs = mutableSetOf(
            ApplicationFormFieldConfigurationDTO(
                "fieldId-1",
                true,
                StepSelectionOptionDTO.STEP_ONE_AND_TWO,
                visibilityLocked = true,
                stepSelectionLocked = true
            ),
            ApplicationFormFieldConfigurationDTO(
                "fieldId-2",
                true,
                StepSelectionOptionDTO.STEP_ONE_AND_TWO,
                visibilityLocked = true,
                stepSelectionLocked = true
            )
        )
        every { callRepo.findById(CALL_ID) } returns Optional.of(callEntity)
        every { projectCallStateAidRepository.findAllByIdCallId(CALL_ID) } returns stateAidEntities(callEntity)
        every { applicationFormFieldConfigurationRepository.findAllByCallId(CALL_ID) } returns oldConfigs
        every { applicationFormFieldConfigurationRepository.saveAll(any<MutableSet<ApplicationFormFieldConfigurationEntity>>()) } returns newConfigs.toEntities(
            callEntity
        ).toList()
        assertThat(
            persistence.saveApplicationFormFieldConfigurations(
                CALL_ID,
                newConfigs
            ).applicationFormFieldConfigurations.toDto(CallType.STANDARD)
        ).containsAll(expectedConfigs)
    }

    @Test
    fun `should return set of application form field configurations for the call`() {
        val callEntity = callEntity(CALL_ID)
        val fieldId = "id"
        val configEntity = ApplicationFormFieldConfigurationEntity(
            ApplicationFormFieldConfigurationId(
                fieldId, callEntity(CALL_ID)
            ), FieldVisibilityStatus.STEP_ONE_AND_TWO
        )
        val expectedConfig = ApplicationFormFieldConfigurationDTO(
            fieldId,
            true,
            StepSelectionOptionDTO.STEP_ONE_AND_TWO,
            visibilityLocked = true,
            stepSelectionLocked = true
        )
        every { callRepo.findById(CALL_ID) } returns Optional.of(callEntity)
        every { applicationFormFieldConfigurationRepository.findAllByCallId(CALL_ID) } returns mutableSetOf(configEntity)
        assertThat(persistence.getApplicationFormFieldConfigurations(CALL_ID).applicationFormFieldConfigurations.toDto(CallType.STANDARD))
            .containsExactly(expectedConfig)
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
        assertThat(
            persistence.updateProjectCallPreSubmissionCheckPlugin(
                CALL_ID, PreSubmissionPlugins(
                    pluginKey = PLUGIN_KEY,
                    firstStepPluginKey = PLUGIN_KEY,
                    reportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_REPORT,
                    reportProjectCheckPluginKey = PLUGIN_KEY_PROJECT_REPORT,
                    controlReportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_CONTROL_REPORT,
                    controlReportSamplingCheckPluginKey = PLUGIN_KEY_CONTROL_SAMPLING
                )
            )
        ).isEqualTo(expectedStandardCallDetail)
    }


    @Test
    fun `should throw CallNotFound while setting pre-submission check settings for the call and call does not exist`() {
        every { callRepo.findById(CALL_ID) } returns Optional.empty()
        assertThrows<CallNotFound> {
            persistence.updateProjectCallPreSubmissionCheckPlugin(
                CALL_ID, PreSubmissionPlugins(
                    pluginKey = PLUGIN_KEY,
                    firstStepPluginKey = PLUGIN_KEY,
                    reportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_REPORT,
                    reportProjectCheckPluginKey = PLUGIN_KEY_PROJECT_REPORT,
                    controlReportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_CONTROL_REPORT,
                    controlReportSamplingCheckPluginKey = PLUGIN_KEY_CONTROL_SAMPLING
                )
            )
        }
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
    fun getCallSummaryById() {
        val callEntity = callEntity(CALL_ID)
        every { callRepo.findById(CALL_ID) } returns Optional.of(callEntity)
        assertThat(persistence.getCallSummaryById(CALL_ID)).isEqualTo(expectedCall)
    }

    @Test
    fun `getCallSummaryById - not existing`() {
        every { callRepo.findById(-1) } returns Optional.empty()
        assertThrows<CallNotFound> { persistence.getCallSummaryById(-1) }
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
    fun getCallSimpleByPartnerId() {
        val callEntity = callEntity(CALL_ID)

        val partner = mockk<ProjectPartnerEntity>()
        every { partner.project.call } returns callEntity
        every { partnerRepository.getReferenceById(114L) } returns partner

        assertThat(persistence.getCallSimpleByPartnerId(114L)).isEqualTo(
            expectedStandardCallDetail.copy(
                applicationFormFieldConfigurations = mutableSetOf(),
                stateAids = emptyList(),
            )
        )
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

        every { userRepo.getReferenceById(expectedResultEntity.creator.id) } returns expectedResultEntity.creator
        every { programmeSpecificObjectiveRepo.getReferenceById(Digitisation) } returns specificObjectives.first { it.programmeObjectivePolicy == Digitisation }
        every { programmeSpecificObjectiveRepo.getReferenceById(AdvancedTechnologies) } returns
                specificObjectives.first { it.programmeObjectivePolicy == AdvancedTechnologies }
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
        every { callRepo.saveAndFlush(capture(slotCall)) } returnsArgument 0

        persistence.createCall(callUpdate, expectedResultEntity.creator.id)
        with(slotCall.captured) {
            assertThat(id).isEqualTo(expectedResultEntity.id)
            assertThat(creator).isEqualTo(expectedResultEntity.creator)
            assertThat(name).isEqualTo(expectedResultEntity.name)
            assertThat(status).isEqualTo(expectedResultEntity.status)
            assertThat(type).isEqualTo(expectedResultEntity.type)
            assertThat(startDate).isEqualTo(expectedResultEntity.startDate)
            assertThat(endDate).isEqualTo(expectedResultEntity.endDate.withSecond(0).withNano(0).plusMinutes(1).minusNanos(1000000))
            assertThat(lengthOfPeriod).isEqualTo(expectedResultEntity.lengthOfPeriod)
            assertThat(isAdditionalFundAllowed).isEqualTo(expectedResultEntity.isAdditionalFundAllowed)
            assertThat(isDirectContributionsAllowed).isEqualTo(expectedResultEntity.isDirectContributionsAllowed)
            assertThat(prioritySpecificObjectives).containsExactlyInAnyOrderElementsOf(specificObjectives)
            assertThat(funds.map { it.setupId.programmeFund }).containsExactly(fund.setupId.programmeFund)
            assertThat(strategies).containsExactlyInAnyOrderElementsOf(strategies)
            assertThat(flatRates).isEmpty()
            assertThat(lumpSums).isEmpty()
            assertThat(unitCosts).isEmpty()
        }
    }

    @Test
    fun createSPFCall() {
        val expectedResultEntity = callEntity(0L, callType = CallType.SPF)
        expectedResultEntity.translatedValues.clear()
        expectedResultEntity.translatedValues.add(
            CallTranslEntity(
                translationId = TranslationId(
                    expectedResultEntity,
                    language = SystemLanguage.EN
                ), "This is a dummy call"
            )
        )

        every { userRepo.getReferenceById(expectedResultEntity.creator.id) } returns expectedResultEntity.creator
        every { programmeSpecificObjectiveRepo.getReferenceById(Digitisation) } returns
                specificObjectives.first { it.programmeObjectivePolicy == Digitisation }
        every { programmeSpecificObjectiveRepo.getReferenceById(AdvancedTechnologies) } returns
                specificObjectives.first { it.programmeObjectivePolicy == AdvancedTechnologies }
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
        every { callRepo.saveAndFlush(capture(slotCall)) } returnsArgument 0

        persistence.createCall(spfCallUpdate, expectedResultEntity.creator.id)
        with(slotCall.captured) {
            assertThat(id).isEqualTo(expectedResultEntity.id)
            assertThat(creator).isEqualTo(expectedResultEntity.creator)
            assertThat(name).isEqualTo(expectedResultEntity.name)
            assertThat(status).isEqualTo(expectedResultEntity.status)
            assertThat(type).isEqualTo(expectedResultEntity.type)
            assertThat(startDate).isEqualTo(expectedResultEntity.startDate)
            assertThat(endDate).isEqualTo(expectedResultEntity.endDate.withSecond(0).withNano(0).plusMinutes(1).minusNanos(1000000))
            assertThat(lengthOfPeriod).isEqualTo(expectedResultEntity.lengthOfPeriod)
            assertThat(isAdditionalFundAllowed).isEqualTo(expectedResultEntity.isAdditionalFundAllowed)
            assertThat(isDirectContributionsAllowed).isEqualTo(expectedResultEntity.isDirectContributionsAllowed)
            assertThat(prioritySpecificObjectives).containsExactlyInAnyOrderElementsOf(specificObjectives)
            assertThat(funds.map { it.setupId.programmeFund }).containsExactly(fund.setupId.programmeFund)
            assertThat(strategies).containsExactlyInAnyOrderElementsOf(strategies)
            assertThat(flatRates).isEmpty()
            assertThat(lumpSums).isEmpty()
            assertThat(unitCosts).isEmpty()
        }
    }

    @Test
    fun updateCall() {
        val callOld = createTestCallEntity(id = 18L)
        every { callRepo.findById(18L) } returns Optional.of(callOld)

        val stateAid = mockk<ProgrammeStateAidEntity>()
        every { stateAid.id } returns 489L
        val existingStateAid = ProjectCallStateAidEntity(StateAidSetupId(mockk(), stateAid))
        val stateAidNew = ProgrammeStateAidEntity(
            id = 254L,
            measure = ProgrammeStateAidMeasure.GBER_ARTICLE_15,
            schemeNumber = "254-sc",
            maxIntensity = BigDecimal.ONE,
            threshold = BigDecimal.TEN,
        )
        val newStateAid = ProjectCallStateAidEntity(StateAidSetupId(mockk(), stateAidNew))
        every { projectCallStateAidRepository.findAllByIdCallId(18L) } returnsMany listOf(
            mutableSetOf(existingStateAid),
            mutableSetOf(newStateAid),
        )

        every { projectCallStateAidRepository.deleteAllBySetupIdStateAidId(any()) } answers { }
        every { programmeStateAidRepo.findAllById(setOf(254L)) } returns listOf(stateAidNew)

        val savedStateAids = slot<List<ProjectCallStateAidEntity>>()
        every { projectCallStateAidRepository.saveAll(capture(savedStateAids)) } returnsArgument 0

        every { programmeFundRepo.getTop20ByIdInAndSelectedTrue(emptySet()) } returns emptyList()

        val formConfig = ApplicationFormFieldConfigurationEntity(ApplicationFormFieldConfigurationId("af-id", callOld), FieldVisibilityStatus.STEP_TWO_ONLY)
        every { applicationFormFieldConfigurationRepository.findAllByCallId(18L) } returns mutableSetOf(formConfig)
        every { callRepo.save(any()) } returnsArgument 0

        val call = Call(
            id = 18L,
            endDate = END,
            name = "call NEW",
            status = CallStatus.DRAFT,
            type = CallType.STANDARD,
            startDate = START,
            isAdditionalFundAllowed = true,
            isDirectContributionsAllowed = true,
            lengthOfPeriod = 25,
            stateAidIds = setOf(254L),
        )
        val callDetail = CallDetail(
            id = 18L,
            name = "call NEW",
            status = CallStatus.DRAFT,
            endDateStep1 = null,
            endDate = END,
            type = CallType.STANDARD,
            startDate = START,
            isAdditionalFundAllowed = true,
            isDirectContributionsAllowed = true,
            lengthOfPeriod = 25,
            applicationFormFieldConfigurations = mutableSetOf(ApplicationFormFieldConfiguration("af-id", FieldVisibilityStatus.STEP_TWO_ONLY)),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            reportPartnerCheckPluginKey = "check-off",
            reportProjectCheckPluginKey = "check-off",
            stateAids = listOf(
                ProgrammeStateAid(
                    id = 254L,
                    measure = ProgrammeStateAidMeasure.GBER_ARTICLE_15,
                    schemeNumber = "254-sc",
                    maxIntensity = BigDecimal.ONE,
                    threshold = BigDecimal.TEN,
                ),
            ),
            flatRates = sortedSetOf(
                ProjectCallFlatRate(FlatRateType.STAFF_COSTS, rate = 5, adjustable = true),
            ),
            projectDefinedUnitCostAllowed = true,
            projectDefinedLumpSumAllowed = false,
            controlReportPartnerCheckPluginKey = "control-report-partner-check-off",
            controlReportSamplingCheckPluginKey = "control-report-sampling-check-off"
        )
        assertThat(persistence.updateCall(call)).isEqualTo(callDetail)
        verify(exactly = 1) { projectCallStateAidRepository.deleteAllBySetupIdStateAidId(489L) }
        assertThat(savedStateAids.captured).hasSize(1)
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
    fun `update flat rates with same setup type - OK`() {
        val call = createTestCallEntity(1)
        every { callRepo.findById(1L) } returns Optional.of(call)

        every { applicationFormFieldConfigurationRepository.findAllByCallId(call.id) } returns applicationFormFieldConfigurationEntities(
            call
        )
        every { projectCallStateAidRepository.findAllByIdCallId(call.id) } returns stateAidEntities(call)

        persistence.updateProjectCallFlatRate(
            1, setOf(
                ProjectCallFlatRate(
                    type = FlatRateType.STAFF_COSTS,
                    rate = 12,
                    adjustable = false
                ),
            )
        )

        assertThat(call.flatRates).containsExactlyInAnyOrder(
            ProjectCallFlatRateEntity(
                setupId = FlatRateSetupId(call = call, type = FlatRateType.STAFF_COSTS),
                rate = 12,
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
        every { programmeUnitCostRepo.findAllByIdInAndProjectIdNull(setOf(32L)) } returns mutableListOf()
        assertThat(persistence.existsAllProgrammeUnitCostsByIds(setOf(32L))).isFalse

        every { programmeUnitCostRepo.findAllByIdInAndProjectIdNull(setOf(17L)) } returns mutableListOf(unitCost2.copy(id = 17L))
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
        every { programmeUnitCostRepo.findAllByIdInAndProjectIdNull(setOf(2, 3)) } returns mutableListOf(unitCost2, unitCost3)
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

    @Test
    fun getCallCostOptionForProject() {
        every { projectPersistence.getCallIdOfProject(48L) } returns 7L
        every { callRepo.findById(7L) } returns Optional.of(callWithCostOption())
        assertThat(persistence.getCallCostOption(7L)).isEqualTo(
            CallCostOption(
                projectDefinedUnitCostAllowed = true,
                projectDefinedLumpSumAllowed = false,
            )
        )
    }

    @Test
    fun getCallCostOption() {
        every { callRepo.findById(4L) } returns Optional.of(callWithCostOption())
        assertThat(persistence.getCallCostOption(4L)).isEqualTo(
            CallCostOption(
                projectDefinedUnitCostAllowed = true,
                projectDefinedLumpSumAllowed = false,
            )
        )
    }

    @Test
    fun updateCallCostOption() {
        val call = createTestCallEntity(id = 10L)
        call.projectDefinedUnitCostAllowed = false
        call.projectDefinedLumpSumAllowed = false

        every { callRepo.findById(10L) } returns Optional.of(call)
        persistence.updateCallCostOption(10L, CallCostOption(true, true))

        assertThat(call.projectDefinedUnitCostAllowed).isTrue()
        assertThat(call.projectDefinedLumpSumAllowed).isTrue()
    }

    @Test
    fun `unitCost entities are correctly initialized`() {
        val projectCall = mockk<CallEntity>()
        val programmeUnitCost = mockk<ProgrammeUnitCostEntity>()

        val uc1 = ProjectCallUnitCostEntity(
            ProjectCallUnitCostId(
                projectCall = projectCall,
                programmeUnitCost = programmeUnitCost,
            )
        )

        val uc2 = ProjectCallUnitCostEntity(
            ProjectCallUnitCostId(
                projectCall = projectCall,
                programmeUnitCost = programmeUnitCost,
            )
        )

        assertThat(uc1.id.equals(uc2.id)).isTrue()
    }

    @Test
    fun getCallChecklists() {
        every { programmeChecklistRepository.findAll(Sort.unsorted()) } returns programmeChecklists
        every { callSelectedChecklistRepository.findAllByIdCallId(10L) } returns selectedChecklists

        assertThat(persistence.getCallChecklists(10L, Sort.unsorted())).isEqualTo(
            programmeChecklists.map { it.toModel(selected = it.id == 1L) }
        )
    }

    @Test
    fun updateCallChecklistSelection() {
        val callId = 10L
        val call: CallEntity = mockk { every { id } returns callId }
        val checklistIds = setOf(1L, 2L)
        every { callRepo.findById(callId) } returns Optional.of(call)
        every { programmeChecklistRepository.findAllById(checklistIds) } returns programmeChecklists
        every { callSelectedChecklistRepository.findAllByIdCallId(callId) } returns selectedChecklists

        val deleteSlot = slot<Iterable<CallSelectedChecklistEntity>>()
        val createSlot = slot<Iterable<CallSelectedChecklistEntity>>()
        every { callSelectedChecklistRepository.deleteAll(capture(deleteSlot)) } just runs
        every { callSelectedChecklistRepository.saveAll(capture(createSlot)) } returnsArgument 0

        assertThat(persistence.updateCallChecklistSelection(callId, checklistIds)).isEqualTo(updatedSelectedChecklists)

        assertTrue(deleteSlot.captured.none())
        assertTrue(createSlot.captured.all { it.id.programmeChecklist.id == 2L })
    }

}
