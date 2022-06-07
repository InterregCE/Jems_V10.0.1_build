package io.cloudflight.jems.server.project.service.partner.cofinancing.update_cofinancing

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.AutomaticPublic
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.Private
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.Public
import io.cloudflight.jems.server.call.callFund
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.CallApplicationFormFieldsConfiguration
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
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

internal class UpdateCoFinancingInteractorTest {

    companion object {
        private const val partnerId = 1L
        private const val projectId = 2L
        private const val callId = 3L
        private val fund = ProgrammeFundEntity(id = 1, selected = true)

        private val financingOk = listOf(
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(40.5),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(59.5),
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
        testWrongPercentage(percentages = listOf(null), errorMsg = "project.partner.coFinancing.percentage.invalid")
    }

    @Test
    fun `test negative percentage`() {
        testWrongPercentage(
            percentages = listOf(BigDecimal.valueOf(-1)),
            errorMsg = "project.partner.coFinancing.percentage.invalid"
        )
    }

    @Test
    fun `test percentage over 100`() {
        testWrongPercentage(
            percentages = listOf(BigDecimal.valueOf(101)),
            errorMsg = "project.partner.coFinancing.percentage.invalid"
        )
    }

    @Test
    fun `test percentage over 100 and combination`() {
        testWrongPercentage(
            percentages = listOf(BigDecimal.valueOf(101), BigDecimal.valueOf(50)),
            errorMsg = "project.partner.coFinancing.percentage.invalid"
        )
    }

    @Test
    fun `test percentage wrong sum`() {
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
        val partnerContributions = listOf(
            ProjectPartnerContribution(name = null, amount = BigDecimal.TEN, status = Public, isPartner = true)
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
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(1L))
        val result = ProjectPartnerCoFinancingAndContribution(emptyList(), partnerContributions, "")
        every { persistence.updateCoFinancingAndContribution(partnerId, financingOk, partnerContributions) } returns result

        assertThat(updateCoFinancing.updateCoFinancing(partnerId, financingOk, partnerContributions)).isEqualTo(result)
    }

    @Test
    fun `test exception on partner contribution if disabled in configuration`() {
        val partnerContributions = listOf(
            ProjectPartnerContribution(name = null, amount = BigDecimal.TEN, isPartner = true, status = Public),
            ProjectPartnerContribution(name = null, amount = BigDecimal.TEN, isPartner = false, status = Public)
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
        ignoreFundIdsRetrieval()
        val testCoFinancing = listOf(
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(20),
                fundId = 10
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(80),
                fundId = 11
            )
        )

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.one.and.only.partner.contribution")
    }

    @Test
    fun `test duplicate fundIds - no any null`() {
        every { persistence.getAvailableFunds(1L) } returns setOf(callFund(101L), callFund(102L), callFund(103L))

        val testCoFinancing = listOf(
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(20),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(30),
                fundId = 555
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(50),
                fundId = 555
            )
        )

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.fund.not.unique")
    }

    @Test
    fun `wrong amount of funds - more than MAX`() {
        every { persistence.getAvailableFunds(1L) } returns setOf(
            callFund(101L),
            callFund(102L),
            callFund(103L),
            callFund(104L),
            callFund(105L)
        )

        val testCoFinancing = listOf(
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(40),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(20),
                fundId = 101
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(10),
                fundId = 102
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(10),
                fundId = 103
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(10),
                fundId = 104
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(10),
                fundId = 105
            ),
        )

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.maximum.partner.contributions")
    }

    @Test
    fun `wrong amount of funds - not enough funds available MAX`() {
        every { persistence.getAvailableFunds(1L) } returns setOf(callFund(101L))

        val testCoFinancing = listOf(
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(40),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(30),
                fundId = 101
            ),
            UpdateProjectPartnerCoFinancing(
                percentage = BigDecimal.valueOf(30),
                fundId = 102
            ),
        )

        val ex = assertThrows<I18nValidationException> {
            updateCoFinancing.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.maximum.partner.contributions")
    }

    @Test
    fun `update financing forbidden or not-existing fund`() {
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
            ProjectPartnerContribution(name = null, amount = BigDecimal.TEN, isPartner = true, status = Public)
        )
        updateCoFinancing.updateCoFinancing(1, financingOk, toSave)
        assertThat(slotPartnerContributions.captured).containsExactly(
            ProjectPartnerContribution(name = null, amount = BigDecimal.TEN, isPartner = true, status = Public)
        )
        assertThat(slotFinances.captured).containsExactlyInAnyOrder(
            UpdateProjectPartnerCoFinancing(
                fundId = fund.id,
                percentage = BigDecimal.valueOf(59.5)
            ),
            UpdateProjectPartnerCoFinancing(
                fundId = null,
                percentage = BigDecimal.valueOf(40.5)
            )
        )
    }

    @Test
    fun `update financing OK and contribution - wrong partner numbers`() {
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContribution(name = "not used", amount = BigDecimal.TEN, isPartner = true, status = Public),
            ProjectPartnerContribution(name = null, amount = BigDecimal.TEN, isPartner = true, status = Private)
        )

        assertExceptionMsg(
            executable = { updateCoFinancing.updateCoFinancing(partnerId, financingOk, toSave) },
            expectedError = "project.partner.contribution.one.and.only.partner.contribution",
            description = "there can be only exactly 1 partner contribution"
        )
    }

    @Test
    fun `update financing OK and contribution - wrong partner status`() {
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContribution(
                name = "not used",
                amount = BigDecimal.TEN,
                isPartner = true,
                status = AutomaticPublic
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
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContribution(
                name = "ignored",
                amount = BigDecimal.valueOf(30),
                isPartner = true,
                status = Public
            ),
            ProjectPartnerContribution(name = "", amount = BigDecimal.valueOf(70), isPartner = false, status = Public)
        )

        assertExceptionMsg(
            executable = { updateCoFinancing.updateCoFinancing(partnerId, financingOk, toSave) },
            expectedError = "project.partner.contribution.name.is.mandatory"
        )
    }

    @Test
    fun `update financing OK and contribution - missing status`() {
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
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContribution(name = "ignored", amount = null, isPartner = true, status = Public)
        )

        assertExceptionMsg(
            executable = { updateCoFinancing.updateCoFinancing(partnerId, financingOk, toSave) },
            expectedError = "project.partner.contribution.amount.is.mandatory"
        )
    }

    @Test
    fun `update financing OK and contribution - amount 0`() {
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContribution(name = "zero", amount = BigDecimal.ZERO, isPartner = true, status = Public)
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
            ProjectPartnerContribution(name = "zero", amount = BigDecimal.ZERO, isPartner = true, status = Public)
        )
        assertThat(slotFinances.captured).containsExactlyInAnyOrder(
            UpdateProjectPartnerCoFinancing(
                fundId = fund.id,
                percentage = BigDecimal.valueOf(59.5)
            ),
            UpdateProjectPartnerCoFinancing(
                fundId = null,
                percentage = BigDecimal.valueOf(40.5)
            )
        )
    }

    @Test
    fun `test exception on SPF partner contribution if disabled in configuration`() {
        val partnerSpfContributions = listOf(
            ProjectPartnerContributionSpf(name = null, amount = BigDecimal.TEN, status = Public),
            ProjectPartnerContributionSpf(name = null, amount = BigDecimal.ONE, status = Private)
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
        every { persistence.getAvailableFunds(partnerId) } returns setOf(callFund(fund.id))

        val toSave = listOf(
            ProjectPartnerContributionSpf(name = "name", amount = null, status = Public)
        )

        assertExceptionMsg(
            executable = { updateCoFinancing.updateSpfCoFinancing(partnerId, financingOk, toSave) },
            expectedError = "project.partner.contribution.amount.is.mandatory"
        )
    }

    @Test
    fun updateSpfCoFinancing() {
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
            status = Public
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
