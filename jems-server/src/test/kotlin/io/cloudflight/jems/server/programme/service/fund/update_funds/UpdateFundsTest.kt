package io.cloudflight.jems.server.programme.service.fund.update_funds

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.fund.update_funds.UpdateFunds.Companion.MAX_FUND_ABBREVIATION_LENGTH
import io.cloudflight.jems.server.programme.service.fund.update_funds.UpdateFunds.Companion.MAX_FUND_DESCRIPTION_LENGTH
import io.cloudflight.jems.server.programme.service.fund.update_funds.UpdateFunds.Companion.MAX_NUMBER_OF_FUNDS
import io.cloudflight.jems.server.programme.service.is_programme_setup_locked.IsProgrammeSetupLockedInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.context.ApplicationEventPublisher

internal class UpdateFundsTest : UnitTest() {

    private val idExistingToUpdate = 14L
    private val idExistingToDelete = 31L
    private val inputErrorMap = mapOf("error" to I18nMessage("error.key"))
    private val fundToCreate = ProgrammeFund(
        id = 0,
        selected = true,
        type = ProgrammeFundType.OTHER,
        abbreviation = setOf(InputTranslation(EN, "EN to create abbr"), InputTranslation(SK, "SK to create abbr")),
        description = setOf(InputTranslation(EN, "EN desc"), InputTranslation(SK, "SK desc"))
    )

    @MockK
    lateinit var persistence: ProgrammeFundPersistence

    @MockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var updateFunds: UpdateFunds

    @RelaxedMockK
    lateinit var mockedList: List<ProgrammeFund>

    @BeforeEach
    fun reset() {
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws AppInputValidationException(
            inputErrorMap
        )
    }

    @Test
    fun `update funds - everything should be fine`() {
        val toUpdate = ProgrammeFund(
            id = idExistingToUpdate,
            selected = true,
            abbreviation = setOf(InputTranslation(language = EN, translation = "EN to update abbr"))
        )
        val toDelete = ProgrammeFund(
            id = idExistingToDelete,
            selected = false,
            abbreviation = setOf(InputTranslation(language = SK, translation = "SK to delete abbr"))
        )

        every { persistence.getMax20Funds() } returns listOf(toDelete, toUpdate)
        every { isProgrammeSetupLocked.isLocked() } returns false

        val slotToDeleteIds = slot<Set<Long>>()
        val slotFunds = slot<Set<ProgrammeFund>>()
        every { persistence.updateFunds(capture(slotToDeleteIds), capture(slotFunds)) } returns listOf(
            toUpdate.copy(selected = !toUpdate.selected), fundToCreate,
        )

        assertThat(
            updateFunds.update(listOf(toUpdate.copy(selected = !toUpdate.selected), fundToCreate))
        ).containsExactlyInAnyOrder(toUpdate.copy(selected = !toUpdate.selected), fundToCreate)

        assertThat(slotToDeleteIds.captured).containsExactly(idExistingToDelete)
        assertThat(slotFunds.captured).containsExactly(
            toUpdate.copy(selected = !toUpdate.selected),
            fundToCreate,
        )

        val event = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(event)) }
        with(event.captured) {
            assertThat(this.auditCandidate.action).isEqualTo(AuditAction.PROGRAMME_FUNDS_CHANGED)
            assertThat(this.auditCandidate.description).isEqualTo(
                "Programme funds has been set to:\n" +
                    "[selected=false, EN=EN to update abbr],\n" +
                    "[selected=true, EN=EN to create abbr, SK=SK to create abbr]"
            )
        }
    }

    @Test
    fun `update funds - check if amount of funds is not over limit`() {
        every { mockedList.size } returns MAX_NUMBER_OF_FUNDS + 1
        every { generalValidator.maxSize(mockedList, MAX_NUMBER_OF_FUNDS, "funds") } returns inputErrorMap
        assertThrows<AppInputValidationException> { updateFunds.update(mockedList) }
        verify(exactly = 1) { generalValidator.maxSize(mockedList, MAX_NUMBER_OF_FUNDS, "funds") }
    }

    @Test
    fun `update funds - check if abbreviation length is validated`() {
        val abbreviation = setOf(InputTranslation(SK, getStringOfLength(MAX_FUND_ABBREVIATION_LENGTH + 1)))
        every {
            generalValidator.maxLength(abbreviation, MAX_FUND_ABBREVIATION_LENGTH, "abbreviation")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            updateFunds.update(listOf(fundToCreate.copy(abbreviation = abbreviation)))
        }
        verify(exactly = 1) { generalValidator.maxLength(abbreviation, MAX_FUND_ABBREVIATION_LENGTH, "abbreviation") }
    }

    @Test
    fun `update funds - check if description length is validated`() {
        val description = setOf(InputTranslation(EN, getStringOfLength(MAX_FUND_DESCRIPTION_LENGTH + 1)))
        every {
            generalValidator.maxLength(description, MAX_FUND_DESCRIPTION_LENGTH, "description")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            updateFunds.update(
                listOf(fundToCreate.copy(description = description), fundToCreate.copy(description = description))
            )
        }
        verify(exactly = 2) { generalValidator.maxLength(description, MAX_FUND_DESCRIPTION_LENGTH, "description") }

    }

    @Test
    fun `update funds - should throw exception when deleting funds and programme setup is restricted`() {
        val toDelete = ProgrammeFund(
            id = idExistingToDelete, selected = false, abbreviation = setOf(
                InputTranslation(language = SK, translation = "SK to delete abbr")
            )
        )
        every { persistence.getMax20Funds() } returns listOf(toDelete)
        every { isProgrammeSetupLocked.isLocked() } returns true

        assertThrows<ChangesAreNotAllowedException> { updateFunds.update(emptyList()) }
    }

    @Test
    fun `update funds - should throw exception when updating funds and programme setup is restricted`() {
        val toUpdate = ProgrammeFund(
            id = idExistingToUpdate, selected = true, abbreviation = setOf(
                InputTranslation(language = EN, translation = "EN to update abbr")
            )
        )
        every { persistence.getMax20Funds() } returns listOf(toUpdate)
        every { isProgrammeSetupLocked.isLocked() } returns true

        assertThrows<ChangesAreNotAllowedException> {
            updateFunds.update(listOf(toUpdate.copy(selected = !toUpdate.selected)))
        }
    }

    @Test
    fun `update funds - should throw exception when updating fund which does not exist`() {
        val toUpdate = ProgrammeFund(
            id = idExistingToUpdate,
            selected = true,
            abbreviation = setOf(InputTranslation(language = EN, translation = "EN to update abbr"))
        )

        every { persistence.getMax20Funds() } returns emptyList()
        every { isProgrammeSetupLocked.isLocked() } returns false

        assertThrows<FundNotFoundException> {
            updateFunds.update(listOf(toUpdate.copy(selected = !toUpdate.selected)))
        }
    }


    @ParameterizedTest
    @MethodSource("providePreDefinedProgramFundTypes")
    fun `should throw exception when new program fund under predefined types is being created`(type: ProgrammeFundType) {
        assertThrows<CreationOfFundUnderPreDefinedTypesIsNotAllowedException> {
            updateFunds.update(
                listOf(ProgrammeFund(id = 0, type = type, selected = true))
            )
        }
    }

    private fun providePreDefinedProgramFundTypes() =
        ProgrammeFundType.values().filter { it != ProgrammeFundType.OTHER }

}
