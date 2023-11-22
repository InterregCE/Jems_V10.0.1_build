package io.cloudflight.jems.server.project.service.partner.cofinancing.update_cofinancing

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.AutomaticPublic
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.Private
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.Public
import io.cloudflight.jems.server.call.callFund
import io.cloudflight.jems.server.call.callFundRate
import io.cloudflight.jems.server.call.callFundRateFixed
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallApplicationFormFieldsConfiguration
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.service.costoption.model.PaymentClaim
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class UpdateCoFinancingInteractorTest {

    companion object {
        private const val partnerId = 1L
        private const val projectId = 2L
        private const val callId = 3L
        private val fund = ProgrammeFundEntity(id = 1, selected = true)

        private val financingOk = listOf(
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(94.5),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(5.5),
                fundId = 1
            )
        )
        private val afConfiguration = CallApplicationFormFieldsConfiguration(
            CallType.STANDARD,
            mutableSetOf(
                ApplicationFormFieldConfiguration(
                    ApplicationFormFieldSetting.PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN.id,
                    FieldVisibilityStatus.STEP_ONE_AND_TWO
                )
            )
        )

        private const val FUND_ID = 1L
        private const val FUND_ID_SECOND = 2L
        private const val FUND_ID_THIRD = 3L
        private const val FUND_ID_FOURTH = 4L
        private const val FUND_ID_FIFTH = 5L

        private val callDetail = CallDetail(
            id = 569L,
            name = "existing call",
            status = CallStatus.PUBLISHED,
            type = CallType.STANDARD,
            startDate = ZonedDateTime.now().minusDays(1),
            endDateStep1 = null,
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            isDirectContributionsAllowed = true,
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
            funds = sortedSetOf(callFundRate(FUND_ID), callFundRateFixed(FUND_ID_SECOND)),
            flatRates = sortedSetOf(
                ProjectCallFlatRate(
                    type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS,
                    rate = 5,
                    adjustable = true
                ),
            ),
            lumpSums = listOf(
                ProgrammeLumpSum(splittingAllowed = true, fastTrack = false, paymentClaim = PaymentClaim.IncurredByBeneficiaries),
            ),
            unitCosts = listOf(
                ProgrammeUnitCost(projectId = null, isOneCostCategory = true, paymentClaim = PaymentClaim.IncurredByBeneficiaries),
            ),
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            reportPartnerCheckPluginKey = null,
            reportProjectCheckPluginKey = null,
            projectDefinedUnitCostAllowed = false,
            projectDefinedLumpSumAllowed = true,
            controlReportPartnerCheckPluginKey = null,
            controlReportSamplingCheckPluginKey = null
        )

        private val callDetailsMaxFunds = CallDetail(
            id = 569L,
            name = "existing call",
            status = CallStatus.PUBLISHED,
            type = CallType.STANDARD,
            startDate = ZonedDateTime.now().minusDays(1),
            endDateStep1 = null,
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            isDirectContributionsAllowed = true,
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
                callFundRate(FUND_ID),
                callFundRateFixed(FUND_ID_SECOND),
                callFundRate(FUND_ID_THIRD),
                callFundRate(FUND_ID_FOURTH),
                callFundRate(FUND_ID_FIFTH)
            ),
            flatRates = sortedSetOf(
                ProjectCallFlatRate(
                    type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS,
                    rate = 5,
                    adjustable = true
                ),
            ),
            lumpSums = listOf(
                ProgrammeLumpSum(splittingAllowed = true, fastTrack = false, paymentClaim = PaymentClaim.IncurredByBeneficiaries),
            ),
            unitCosts = listOf(
                ProgrammeUnitCost(projectId = null, isOneCostCategory = true, paymentClaim = PaymentClaim.IncurredByBeneficiaries),
            ),
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            reportPartnerCheckPluginKey = null,
            reportProjectCheckPluginKey = null,
            projectDefinedUnitCostAllowed = false,
            projectDefinedLumpSumAllowed = true,
            controlReportPartnerCheckPluginKey = null,
            controlReportSamplingCheckPluginKey = null
        )

        private val callDetailsOneFund = CallDetail(
            id = 569L,
            name = "existing call",
            status = CallStatus.PUBLISHED,
            type = CallType.STANDARD,
            startDate = ZonedDateTime.now().minusDays(1),
            endDateStep1 = null,
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            isDirectContributionsAllowed = true,
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
                callFundRate(FUND_ID)
            ),
            flatRates = sortedSetOf(
                ProjectCallFlatRate(
                    type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS,
                    rate = 5,
                    adjustable = true
                ),
            ),
            lumpSums = listOf(
                ProgrammeLumpSum(splittingAllowed = true, fastTrack = false, paymentClaim = PaymentClaim.IncurredByBeneficiaries),
            ),
            unitCosts = listOf(
                ProgrammeUnitCost(projectId = null, isOneCostCategory = true, paymentClaim = PaymentClaim.IncurredByBeneficiaries),
            ),
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            reportPartnerCheckPluginKey = null,
            reportProjectCheckPluginKey = null,
            projectDefinedUnitCostAllowed = false,
            projectDefinedLumpSumAllowed = true,
            controlReportPartnerCheckPluginKey = null,
            controlReportSamplingCheckPluginKey = null
        )

    }

    @MockK
    lateinit var persistence: ProjectPartnerCoFinancingPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var callPersistence: CallPersistence

    @InjectMockKs
    lateinit var updateCoFinancing: UpdateCoFinancing

    @BeforeEach
    fun setup() {
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { projectPersistence.getCallIdOfProject(projectId) } returns callId
        every { callPersistence.getApplicationFormFieldConfigurations(callId) } returns afConfiguration
    }

    @Test
    fun `test percentage null`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        testWrongPercentage(percentages = listOf(null), errorMsg = "project.partner.coFinancing.percentage.invalid")
    }

    @Test
    fun `test negative percentage`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        testWrongPercentage(
            percentages = listOf(BigDecimal.valueOf(-1)),
            errorMsg = "project.partner.coFinancing.percentage.invalid"
        )
    }

    @Test
    fun `test percentage over 100`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        testWrongPercentage(
            percentages = listOf(BigDecimal.valueOf(101)),
            errorMsg = "project.partner.coFinancing.percentage.invalid"
        )
    }

    @Test
    fun `test percentage over 100 and combination`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        testWrongPercentage(
            percentages = listOf(BigDecimal.valueOf(101), BigDecimal.valueOf(50)),
            errorMsg = "project.partner.coFinancing.percentage.invalid"
        )
    }

    @Test
    fun `test percentage wrong sum`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        testWrongPercentage(
            percentages = listOf(BigDecimal.valueOf(45), BigDecimal.valueOf(56)),
            errorMsg = "project.partner.coFinancing.sum.invalid"
        )
    }

    private fun testWrongPercentage(percentages: List<BigDecimal?>, errorMsg: String) {
        val testCoFinancing = percentages.map {
            UpdateProjectPartnerCoFinancing(
                fundId = null,
                percentage = it
            )
        }
        ignoreFundIdsRetrieval()

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(partnerId, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo(errorMsg)
    }

    private fun ignoreFundIdsRetrieval() = every { persistence.getAvailableFunds(partnerId) } returns emptySet()

    @Test
    fun `test wrong amount of fundIds - 2 nulls`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(101L), callFund(102L), callFund(103L))
        val testCoFinancing = listOf(
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(20),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(80),
                fundId = null
            )
        )

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(partnerId, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.one.and.only.partner.contribution")
    }

    @Test
    fun `test success on partner contribution if disabled in configuration`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        val partnerContributions = listOf(
            ProjectPartnerContribution(
                name = null,
                amount = BigDecimal.TEN,
                status = ProjectPartnerContributionStatus.Public,
                isPartner = true
            )
        )
        val afConfigurationNoContrib = CallApplicationFormFieldsConfiguration(
            CallType.STANDARD,
            mutableSetOf(
                ApplicationFormFieldConfiguration(
                    ApplicationFormFieldSetting.PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN.id,
                    FieldVisibilityStatus.NONE
                )
            )
        )
        every { callPersistence.getApplicationFormFieldConfigurations(callId) } returns afConfigurationNoContrib
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(1L), callFund(2L))
        val result = ProjectPartnerCoFinancingAndContribution(emptyList(), partnerContributions, "")
        every { persistence.updateCoFinancingAndContribution(partnerId, financingOk, partnerContributions) } returns result

        assertThat(updateCoFinancing.updateCoFinancing(partnerId, financingOk, partnerContributions)).isEqualTo(result)
    }

    @Test
    fun `test exception on partner contribution if disabled in configuration`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        val partnerContributions = listOf(
            ProjectPartnerContribution(
                name = null,
                amount = BigDecimal.TEN,
                isPartner = true,
                status = ProjectPartnerContributionStatus.Public
            ),
            ProjectPartnerContribution(
                name = null,
                amount = BigDecimal.TEN,
                isPartner = false,
                status = ProjectPartnerContributionStatus.Public
            )
        )
        val afConfigurationNoContrib = CallApplicationFormFieldsConfiguration(
            CallType.STANDARD,
            mutableSetOf(
                ApplicationFormFieldConfiguration(
                    ApplicationFormFieldSetting.PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN.id,
                    FieldVisibilityStatus.NONE
                )
            )
        )
        every { callPersistence.getApplicationFormFieldConfigurations(callId) } returns afConfigurationNoContrib

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(partnerId, financingOk, partnerContributions)
        }
        assertThat(ex.i18nKey).isEqualTo(PARTNER_CONTRIBUTIONS_NOT_ENABLED_ERROR_KEY)
    }

    @Test
    fun `test wrong amount of fundIds - no any null`() {
        every { callPersistence.getCallById(callId) } returns callDetailsOneFund
        ignoreFundIdsRetrieval()
        val testCoFinancing = listOf(
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(10),
                fundId = 1
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(90),
                fundId = 2
            )
        )

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.one.and.only.partner.contribution")
    }

    @Test
    fun `test duplicate fundIds - no any null`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(1L) } returns setOf(callFund(101L), callFund(102L), callFund(103L))

        val testCoFinancing = listOf(
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(20),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(30),
                fundId = 1
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(50),
                fundId = 1
            )
        )

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.fund.not.unique")
    }

    @Test
    fun `test non-fixed percentage when it should be fixed`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(1L) } returns setOf(callFund(1L), callFund(2L))

        val testCoFinancing = listOf(
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(82),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(10),
                fundId = 1
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(8),
                fundId = 2
            )
        )

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.fixed.percentage.invalid")
    }

    @Test
    fun `wrong amount of funds - more than MAX`() {
        every { callPersistence.getCallById(callId) } returns callDetailsMaxFunds
        every { persistence.getAvailableFunds(1L) } returns setOf(
            callFund(1L),
            callFund(2L),
            callFund(3L),
            callFund(4L),
            callFund(5L)
        )

        val testCoFinancing = listOf(
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(40),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(20),
                fundId = 1
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(10),
                fundId = 2
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(10),
                fundId = 3
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(10),
                fundId = 4
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(10),
                fundId = 5
            ),
        )

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.maximum.partner.contributions")
    }

    @Test
    fun `wrong amount of funds - not enough funds available MAX`() {
        every { callPersistence.getCallById(callId) } returns callDetailsOneFund
        every { persistence.getAvailableFunds(1L) } returns setOf(callFund(1L))

        val testCoFinancing = listOf(
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(40),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(30),
                fundId = 1
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(30),
                fundId = 2
            ),
        )

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.maximum.partner.contributions")
    }

    @Test
    fun `update financing forbidden or not-existing fund`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            UpdateProjectPartnerCoFinancing(
                fundId = -1,
                percentage = BigDecimal.valueOf(20)
            ),
            UpdateProjectPartnerCoFinancing(
                fundId = null,
                percentage = BigDecimal.valueOf(80)
            )
        )
        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(partnerId, toSave, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.fundId.not.allowed.for.call")
    }

    @Test
    fun `update financing OK and 1 contribution OK`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(1) } returns setOf(callFund(fund.id))

        val slotFinances = slot<List<UpdateProjectPartnerCoFinancing>>()
        val slotPartnerContributions = slot<List<ProjectPartnerContribution>>()
        every {
            persistence.updateCoFinancingAndContribution(
                1,
                capture(slotFinances),
                capture(slotPartnerContributions)
            )
        } returns
            ProjectPartnerCoFinancingAndContribution(emptyList(), emptyList(), "")

        val toSave = listOf(
            ProjectPartnerContribution(
                name = null,
                amount = BigDecimal.TEN,
                isPartner = true,
                status = ProjectPartnerContributionStatus.Public
            )
        )
        updateCoFinancing.updateCoFinancing(1, financingOk, toSave)
        assertThat(slotPartnerContributions.captured).containsExactly(
            ProjectPartnerContribution(
                name = null,
                amount = BigDecimal.TEN,
                isPartner = true,
                status = ProjectPartnerContributionStatus.Public
            )
        )
        assertThat(slotFinances.captured).containsExactlyInAnyOrder(
            UpdateProjectPartnerCoFinancing(
                fundId = fund.id,
                percentage = BigDecimal.valueOf(5.5)
            ),
            UpdateProjectPartnerCoFinancing(
                fundId = null,
                percentage = BigDecimal.valueOf(94.5)
            )
        )
    }

    @Test
    fun `update financing OK and contribution - wrong partner numbers`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContribution(name = "not used", amount = BigDecimal.TEN, isPartner = true, status = ProjectPartnerContributionStatus.Public),
            ProjectPartnerContribution(name = null, amount = BigDecimal.TEN, isPartner = true, status = ProjectPartnerContributionStatus.Private)
        )

        assertExceptionMsg(
            executable = { updateCoFinancing.updateCoFinancing(partnerId, financingOk, toSave) },
            expectedError = "project.partner.contribution.one.and.only.partner.contribution",
            description = "there can be only exactly 1 partner contribution"
        )
    }

    @Test
    fun `update financing OK and contribution - wrong partner status`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContribution(
                name = "not used",
                amount = BigDecimal.TEN,
                isPartner = true,
                status = ProjectPartnerContributionStatus.AutomaticPublic
            )
        )

        assertExceptionMsg(
            executable = { updateCoFinancing.updateCoFinancing(partnerId, financingOk, toSave) },
            expectedError = "project.partner.contribution.partner.status.invalid",
            description = "Partner cannot be of status $AutomaticPublic"
        )
    }

    @Test
    fun `update financing OK and contribution - missing name`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContribution(
                name = "ignored",
                amount = BigDecimal.valueOf(30),
                isPartner = true,
                status = ProjectPartnerContributionStatus.Public
            ),
            ProjectPartnerContribution(name = "", amount = BigDecimal.valueOf(70), isPartner = false, status = ProjectPartnerContributionStatus.Public)
        )

        assertExceptionMsg(
            executable = { updateCoFinancing.updateCoFinancing(partnerId, financingOk, toSave) },
            expectedError = "project.partner.contribution.name.is.mandatory"
        )
    }

    @Test
    fun `update financing OK and contribution - missing status`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContribution(name = "ignored", amount = BigDecimal.TEN, isPartner = true, status = null)
        )

        assertExceptionMsg(
            executable = { updateCoFinancing.updateCoFinancing(partnerId, financingOk, toSave) },
            expectedError = "project.partner.contribution.status.is.mandatory"
        )
    }

    @Test
    fun `update financing OK and contribution - missing amount`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContribution(name = "ignored", amount = null, isPartner = true, status = ProjectPartnerContributionStatus.Public)
        )

        assertExceptionMsg(
            executable = { updateCoFinancing.updateCoFinancing(partnerId, financingOk, toSave) },
            expectedError = "project.partner.contribution.amount.is.mandatory"
        )
    }

    @Test
    fun `update financing OK and contribution - amount 0`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContribution(name = "zero", amount = BigDecimal.ZERO, isPartner = true, status = ProjectPartnerContributionStatus.Public)
        )

        val slotFinances = slot<List<UpdateProjectPartnerCoFinancing>>()
        val slotPartnerContributions = slot<List<ProjectPartnerContribution>>()
        every {
            persistence.updateCoFinancingAndContribution(
                partnerId,
                capture(slotFinances),
                capture(slotPartnerContributions)
            )
        } returns
            ProjectPartnerCoFinancingAndContribution(emptyList(), emptyList(), "")

        updateCoFinancing.updateCoFinancing(partnerId, financingOk, toSave)
        assertThat(slotPartnerContributions.captured).containsExactly(
            ProjectPartnerContribution(name = "zero", amount = BigDecimal.ZERO, isPartner = true, status = ProjectPartnerContributionStatus.Public)
        )
        assertThat(slotFinances.captured).containsExactlyInAnyOrder(
            UpdateProjectPartnerCoFinancing(
                fundId = fund.id,
                percentage = BigDecimal.valueOf(5.5)
            ),
            UpdateProjectPartnerCoFinancing(
                fundId = null,
                percentage = BigDecimal.valueOf(94.5)
            )
        )
    }

    @Test
    fun `test exception on SPF partner contribution if disabled in configuration`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        val partnerSpfContributions = listOf(
            ProjectPartnerContributionSpf(name = null, amount = BigDecimal.TEN, status = ProjectPartnerContributionStatus.Public),
            ProjectPartnerContributionSpf(name = null, amount = BigDecimal.ONE, status = ProjectPartnerContributionStatus.Private)
        )
        val afConfigurationNoContrib = CallApplicationFormFieldsConfiguration(
            CallType.SPF,
            mutableSetOf(
                ApplicationFormFieldConfiguration(
                    ApplicationFormFieldSetting.PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN.id,
                    FieldVisibilityStatus.NONE
                )
            )
        )
        every { callPersistence.getApplicationFormFieldConfigurations(callId) } returns afConfigurationNoContrib

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateSpfCoFinancing(partnerId, financingOk, partnerSpfContributions)
        }
        assertThat(ex.i18nKey).isEqualTo(PARTNER_CONTRIBUTIONS_NOT_ENABLED_ERROR_KEY)
    }

    @Test
    fun `update financing SPF OK and contribution - missing amount`() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContributionSpf(name = "name", amount = null, status = ProjectPartnerContributionStatus.Public)
        )

        assertExceptionMsg(
            executable = { updateCoFinancing.updateSpfCoFinancing(partnerId, financingOk, toSave) },
            expectedError = "project.partner.contribution.amount.is.mandatory"
        )
    }

    @Test
    fun updateSpfCoFinancing() {
        every { callPersistence.getCallById(callId) } returns callDetail
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))
        val updateFinance1 = UpdateProjectPartnerCoFinancing(fundId = 1, percentage = BigDecimal.valueOf(40))
        val updateFinance2 = UpdateProjectPartnerCoFinancing(fundId = null, percentage = BigDecimal.valueOf(60))
        val finance1 = ProjectPartnerCoFinancing(
            fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
            fund = ProgrammeFund(id = 1, selected = true, ProgrammeFundType.ERDF),
            percentage = BigDecimal.valueOf(40),
        )
        val finance2 = ProjectPartnerCoFinancing(
            fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
            fund = null,
            percentage = BigDecimal.valueOf(60),
        )
        val partnerSpfContribution = ProjectPartnerContributionSpf(
            name = "name",
            amount = BigDecimal.valueOf(30),
            status = ProjectPartnerContributionStatus.Public
        )

        val slotFinances = slot<List<UpdateProjectPartnerCoFinancing>>()
        val slotPartnerSpfContributions = slot<List<ProjectPartnerContributionSpf>>()
        every {
            persistence.updateSpfCoFinancingAndContribution(
                partnerId,
                capture(slotFinances),
                capture(slotPartnerSpfContributions)
            )
        } returns
            ProjectPartnerCoFinancingAndContributionSpf(listOf(finance1, finance2), listOf(partnerSpfContribution))

        updateCoFinancing.updateSpfCoFinancing(partnerId, listOf(updateFinance1, updateFinance2), listOf(partnerSpfContribution))

        assertThat(slotFinances.captured).containsExactlyInAnyOrder(updateFinance1, updateFinance2)
        assertThat(slotPartnerSpfContributions.captured).containsExactly(partnerSpfContribution)
    }

    private fun assertExceptionMsg(executable: () -> Unit, expectedError: String, description: String? = null) {
        val ex = assertThrows<I18nValidationException>(executable)
        if (description != null)
            assertThat(ex.i18nKey)
                .overridingErrorMessage(description)
                .isEqualTo(expectedError)
        else
            assertThat(ex.i18nKey)
                .isEqualTo(expectedError)
    }

}
