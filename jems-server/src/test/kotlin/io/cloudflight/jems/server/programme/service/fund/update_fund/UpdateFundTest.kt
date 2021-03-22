package io.cloudflight.jems.server.programme.service.fund.update_fund

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundTranslatedValue
import io.cloudflight.jems.server.programme.service.fund.update_fund.UpdateFund.Companion.MAX_FUNDS
import io.cloudflight.jems.server.programme.service.fund.update_fund.UpdateFund.Companion.MAX_FUND_ABBREVIATION_LENGTH
import io.cloudflight.jems.server.programme.service.fund.update_fund.UpdateFund.Companion.MAX_FUND_DESCRIPTION_LENGTH
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateFundTest : UnitTest() {

    companion object {
        private const val ID_EXISTING_TO_NOT_TOUCH = 28L
        private const val ID_EXISTING_TO_UPDATE = 14L
        private const val ID_EXISTING_TO_DELETE = 31L

        private val fundToCreate = ProgrammeFund(
            id = 0,
            selected = true,
            translatedValues = setOf(
                ProgrammeFundTranslatedValue(language = EN, abbreviation = "EN to create abbr", description = "EN desc"),
                ProgrammeFundTranslatedValue(language = SK, abbreviation = "SK to create abbr", description = "SK desc"),
            )
        )
    }

    @MockK
    lateinit var persistence: ProgrammeFundPersistence

    @RelaxedMockK
    lateinit var auditService: AuditService

    @InjectMockKs
    lateinit var updateFund: UpdateFund

    @MockK
    lateinit var mockedList: List<ProgrammeFund>

    @Test
    fun `update funds - everything should be fine`() {
        val toNotTouch = ProgrammeFund(id = ID_EXISTING_TO_NOT_TOUCH, selected = true, translatedValues = setOf(
            ProgrammeFundTranslatedValue(language = EN, abbreviation = "EN to not touch abbr")
        ))
        val toUpdate = ProgrammeFund(id = ID_EXISTING_TO_UPDATE, selected = true, translatedValues = setOf(
            ProgrammeFundTranslatedValue(language = EN, abbreviation = "EN to update abbr")
        ))
        val toDelete = ProgrammeFund(id = ID_EXISTING_TO_DELETE, selected = false, translatedValues = setOf(
            ProgrammeFundTranslatedValue(language = SK, abbreviation = "SK to delete abbr")
        ))

        every { persistence.getMax20Funds() } returns listOf(
            toDelete,
            toUpdate,
        )
        every { persistence.isProgrammeSetupRestricted() } returns false

        val slotToDeleteIds = slot<Set<Long>>()
        val slotFunds = slot<Set<ProgrammeFund>>()
        every { persistence.updateFunds(capture(slotToDeleteIds), capture(slotFunds)) } returns listOf(
            toNotTouch,
            toUpdate.copy(selected = !toUpdate.selected),
            fundToCreate,
        )

        assertThat(updateFund.updateFunds(listOf(
            toUpdate.copy(selected = !toUpdate.selected),
            fundToCreate,
        ))).containsExactlyInAnyOrder(
            toNotTouch,
            toUpdate.copy(selected = !toUpdate.selected),
            fundToCreate,
        )

        assertThat(slotToDeleteIds.captured).containsExactly(ID_EXISTING_TO_DELETE)
        assertThat(slotFunds.captured).containsExactly(
            toUpdate.copy(selected = !toUpdate.selected),
            fundToCreate,
        )

        val event = slot<AuditCandidate>()
        verify(exactly = 1) { auditService.logEvent(capture(event)) }
        with(event.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_FUNDS_CHANGED)
            assertThat(description).isEqualTo("Programme funds has been set to:\n" +
                "[selected=true, EN=EN to not touch abbr],\n" +
                "[selected=false, EN=EN to update abbr],\n" +
                "[selected=true, EN=EN to create abbr, SK=SK to create abbr]"
            )
        }
    }

    @Test
    fun `update funds - check if amount of funds is not over limit`() {
        every { mockedList.size } returns MAX_FUNDS + 1
        assertThrows<MaxAllowedFundsReachedException> { updateFund.updateFunds(mockedList) }
    }

    @Test
    fun `update funds - check if abbreviation length is validated`() {
        assertThrows<FundAbbreviationTooLong> { updateFund.updateFunds(listOf(
            fundToCreate.copy(translatedValues = setOf(
                ProgrammeFundTranslatedValue(language = SK, abbreviation = getStringOfLength(MAX_FUND_ABBREVIATION_LENGTH + 1))
            )),
        )) }
    }

    @Test
    fun `update funds - check if description length is validated`() {
        assertThrows<FundDescriptionTooLong> { updateFund.updateFunds(listOf(
            fundToCreate.copy(translatedValues = setOf(
                ProgrammeFundTranslatedValue(language = SK, description = getStringOfLength(MAX_FUND_DESCRIPTION_LENGTH + 1))
            )),
        )) }
    }

    @Test
    fun `update funds - should throw exception when deleting funds and programme setup is restricted`() {
        val toDelete = ProgrammeFund(id = ID_EXISTING_TO_DELETE, selected = false, translatedValues = setOf(
            ProgrammeFundTranslatedValue(language = SK, abbreviation = "SK to delete abbr")
        ))

        every { persistence.getMax20Funds() } returns listOf(toDelete)
        every { persistence.isProgrammeSetupRestricted() } returns true

        assertThrows<MakingChangesWhenProgrammeSetupRestricted> { updateFund.updateFunds(emptyList()) }
    }

    @Test
    fun `update funds - should throw exception when updating funds and programme setup is restricted`() {
        val toUpdate = ProgrammeFund(id = ID_EXISTING_TO_UPDATE, selected = true, translatedValues = setOf(
            ProgrammeFundTranslatedValue(language = EN, abbreviation = "EN to update abbr")
        ))

        every { persistence.getMax20Funds() } returns listOf(toUpdate)
        every { persistence.isProgrammeSetupRestricted() } returns true

        assertThrows<MakingChangesWhenProgrammeSetupRestricted> { updateFund.updateFunds(listOf(
            toUpdate.copy(selected = !toUpdate.selected)
        )) }
    }

    @Test
    fun `update funds - should throw exception when updating fund which does not exist`() {
        val toUpdate = ProgrammeFund(id = ID_EXISTING_TO_UPDATE, selected = true, translatedValues = setOf(
            ProgrammeFundTranslatedValue(language = EN, abbreviation = "EN to update abbr")
        ))

        every { persistence.getMax20Funds() } returns emptyList()
        every { persistence.isProgrammeSetupRestricted() } returns false

        assertThrows<FundNotFound> { updateFund.updateFunds(listOf(
            toUpdate.copy(selected = !toUpdate.selected)
        )) }
    }

}
