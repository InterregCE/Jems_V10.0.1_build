package io.cloudflight.jems.server.project.service.partner.cofinancing.update_cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.AutomaticPublic
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.Private
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.Public
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.collections.HashSet

internal class UpdateCoFinancingInteractorTest {

    companion object {
        private val fund = ProgrammeFundEntity(id = 1, selected = true)

        private val financingOk = setOf(
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                percentage = BigDecimal.valueOf(40.5),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                percentage = BigDecimal.valueOf(59.5),
                fundId = 1
            )
        )
    }

    @MockK
    lateinit var persistence: ProjectPartnerCoFinancingPersistence

    lateinit var updateInteractor: UpdateCoFinancingInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        updateInteractor = UpdateCoFinancing(persistence)
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
        val testCoFinancing = percentages.mapTo(HashSet()) {
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                percentage = it
            )
        }
        ignoreFundIdsRetrieval()

        val ex = assertThrows<I18nValidationException> {
            updateInteractor.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo(errorMsg)
    }

    private fun ignoreFundIdsRetrieval() = every { persistence.getAvailableFundIds(1L) } returns emptySet()

    @Test
    fun `test wrong amount of fundIds - 2 nulls`() {
        ignoreFundIdsRetrieval()
        val testCoFinancing = setOf(
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                percentage = BigDecimal.valueOf(20),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                percentage = BigDecimal.valueOf(80),
                fundId = null
            )
        )

        val ex = assertThrows<I18nValidationException> {
            updateInteractor.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.one.and.only.partner.contribution")
    }

    @Test
    fun `test wrong amount of fundIds - no any null`() {
        ignoreFundIdsRetrieval()
        val testCoFinancing = setOf(
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                percentage = BigDecimal.valueOf(20),
                fundId = 10
            ),
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                percentage = BigDecimal.valueOf(80),
                fundId = 11
            )
        )

        val ex = assertThrows<I18nValidationException> {
            updateInteractor.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.one.and.only.partner.contribution")
    }

    @Test
    fun `test duplicate fundIds - no any null`() {
        ignoreFundIdsRetrieval()
        val testCoFinancing = setOf(
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                percentage = BigDecimal.valueOf(20),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                percentage = BigDecimal.valueOf(30),
                fundId = 555
            ),
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.AdditionalFund,
                percentage = BigDecimal.valueOf(50),
                fundId = 555
            )
        )

        val ex = assertThrows<I18nValidationException> {
            updateInteractor.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.fund.not.unique")
    }

    @Test
    fun `wrong amount of funds - more than 2`() {
        ignoreFundIdsRetrieval()
        val testCoFinancing = setOf(
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                percentage = BigDecimal.valueOf(40),
                fundId = null
            ),
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                percentage = BigDecimal.valueOf(20),
                fundId = 1
            ),
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.AdditionalFund,
                percentage = BigDecimal.valueOf(20),
                fundId = 2
            ),
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.AdditionalFund,
                percentage = BigDecimal.valueOf(20),
                fundId = 3
            )
        )

        val ex = assertThrows<I18nValidationException> {
            updateInteractor.updateCoFinancing(1L, testCoFinancing, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.maximum.partner.contributions")
    }

    @Test
    fun `update financing forbidden or not-existing fund`() {
        every { persistence.getAvailableFundIds(5) } returns setOf(fund.id)

        val toSave = setOf(
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fundId = -1,
                percentage = BigDecimal.valueOf(20)
            ),
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                fundId = null,
                percentage = BigDecimal.valueOf(80)
            )
        )
        val ex = assertThrows<I18nValidationException> {
            updateInteractor.updateCoFinancing(5, toSave, emptyList())
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.fundId.not.allowed.for.call")
    }

    @Test
    fun `update financing OK and 1 contribution OK`() {
        every { persistence.getAvailableFundIds(1) } returns setOf(fund.id)

        val slotFinances = slot<Collection<UpdateProjectPartnerCoFinancing>>()
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
        updateInteractor.updateCoFinancing(1, financingOk, toSave)
        assertThat(slotPartnerContributions.captured).containsExactly(
            ProjectPartnerContribution(name = null, amount = BigDecimal.TEN, isPartner = true, status = Public)
        )
        assertThat(slotFinances.captured).containsExactlyInAnyOrder(
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fundId = fund.id,
                percentage = BigDecimal.valueOf(59.5)
            ),
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                fundId = null,
                percentage = BigDecimal.valueOf(40.5)
            )
        )
    }

    @Test
    fun `update financing OK and contribution - wrong partner numbers`() {
        every { persistence.getAvailableFundIds(2) } returns setOf(fund.id)

        val toSave = listOf(
            ProjectPartnerContribution(name = "not used", amount = BigDecimal.TEN, isPartner = true, status = Public),
            ProjectPartnerContribution(name = null, amount = BigDecimal.TEN, isPartner = true, status = Private)
        )

        assertExceptionMsg(
            executable = { updateInteractor.updateCoFinancing(2, financingOk, toSave) },
            expectedError = "project.partner.contribution.one.and.only.partner.contribution",
            description = "there can be only exactly 1 partner contribution"
        )
    }

    @Test
    fun `update financing OK and contribution - wrong partner status`() {
        every { persistence.getAvailableFundIds(3) } returns setOf(fund.id)

        val toSave = listOf(
            ProjectPartnerContribution(
                name = "not used",
                amount = BigDecimal.TEN,
                isPartner = true,
                status = AutomaticPublic
            )
        )

        assertExceptionMsg(
            executable = { updateInteractor.updateCoFinancing(3, financingOk, toSave) },
            expectedError = "project.partner.contribution.partner.status.invalid",
            description = "Partner cannot be of status $AutomaticPublic"
        )
    }

    @Test
    fun `update financing OK and contribution - missing name`() {
        every { persistence.getAvailableFundIds(4) } returns setOf(fund.id)

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
            executable = { updateInteractor.updateCoFinancing(4, financingOk, toSave) },
            expectedError = "project.partner.contribution.name.is.mandatory"
        )
    }

    @Test
    fun `update financing OK and contribution - missing status`() {
        every { persistence.getAvailableFundIds(6) } returns setOf(fund.id)

        val toSave = listOf(
            ProjectPartnerContribution(name = "ignored", amount = BigDecimal.TEN, isPartner = true, status = null)
        )

        assertExceptionMsg(
            executable = { updateInteractor.updateCoFinancing(6, financingOk, toSave) },
            expectedError = "project.partner.contribution.status.is.mandatory"
        )
    }

    @Test
    fun `update financing OK and contribution - missing amount`() {
        every { persistence.getAvailableFundIds(7) } returns setOf(fund.id)

        val toSave = listOf(
            ProjectPartnerContribution(name = "ignored", amount = null, isPartner = true, status = Public)
        )

        assertExceptionMsg(
            executable = { updateInteractor.updateCoFinancing(7, financingOk, toSave) },
            expectedError = "project.partner.contribution.amount.is.mandatory"
        )
    }

    @Test
    fun `update financing OK and contribution - amount 0`() {
        every { persistence.getAvailableFundIds(8) } returns setOf(fund.id)

        val toSave = listOf(
            ProjectPartnerContribution(name = "zero", amount = BigDecimal.ZERO, isPartner = true, status = Public)
        )

        val slotFinances = slot<Collection<UpdateProjectPartnerCoFinancing>>()
        val slotPartnerContributions = slot<List<ProjectPartnerContribution>>()
        every {
            persistence.updateCoFinancingAndContribution(
                8,
                capture(slotFinances),
                capture(slotPartnerContributions)
            )
        } returns
            ProjectPartnerCoFinancingAndContribution(emptyList(), emptyList(), "")

        updateInteractor.updateCoFinancing(8, financingOk, toSave)
        assertThat(slotPartnerContributions.captured).containsExactly(
            ProjectPartnerContribution(name = "zero", amount = BigDecimal.ZERO, isPartner = true, status = Public)
        )
        assertThat(slotFinances.captured).containsExactlyInAnyOrder(
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fundId = fund.id,
                percentage = BigDecimal.valueOf(59.5)
            ),
            UpdateProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                fundId = null,
                percentage = BigDecimal.valueOf(40.5)
            )
        )
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
