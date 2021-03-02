package io.cloudflight.jems.server.programme.repository.fund

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundTranslationEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundTranslationId
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundTranslatedValue
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProgrammeFundPersistenceTest : UnitTest() {

    companion object {
        private val ID = 1L

        private val fundEntity = ProgrammeFundEntity(id = ID, selected = true).apply {
            translatedValues.addAll(
                setOf(
                    ProgrammeFundTranslationEntity(
                        translationId = ProgrammeFundTranslationId(fund = this, language = EN),
                        abbreviation = "EN abbr",
                        description = "EN desc"
                    ),
                    ProgrammeFundTranslationEntity(
                        translationId = ProgrammeFundTranslationId(fund = this, language = SK),
                        abbreviation = "SK abbr",
                        description = "SK desc"
                    ),
                )
            )
        }

        private val fund = ProgrammeFund(
            id = ID,
            selected = true,
            translatedValues = setOf(
                ProgrammeFundTranslatedValue(language = EN, abbreviation = "EN abbr", description = "EN desc"),
                ProgrammeFundTranslatedValue(language = SK, abbreviation = "SK abbr", description = "SK desc"),
            )
        )

    }

    @MockK
    lateinit var repository: ProgrammeFundRepository

    @MockK
    lateinit var callRepository: CallRepository

    @InjectMockKs
    private lateinit var persistence: ProgrammeFundPersistenceProvider

    @Test
    fun getMax20Statuses() {
        every { repository.findTop20ByOrderById() } returns listOf(fundEntity)
        assertThat(persistence.getMax20Funds()).containsExactly(fund)
    }

    @Test
    fun updateFunds() {
        val toBeRemoved = listOf(
            ProgrammeFundEntity(id = 14),
            ProgrammeFundEntity(id = 15),
        )

        every { repository.findAllById(setOf(14, 15)) } returns toBeRemoved
        every { repository.deleteInBatch(any()) } answers { }
        every { repository.saveAll(any()) } returnsArgument 0

        val funds = persistence.updateFunds(
            toDeleteIds = setOf(14, 15),
            funds = setOf(fund.copy(id = 0))
        )
        assertThat(funds).hasSize(1)
        assertThat(funds[0].selected).isTrue()
        assertThat(funds[0].translatedValues).containsExactlyInAnyOrderElementsOf(fund.translatedValues)

        verify(exactly = 1) { repository.deleteInBatch(toBeRemoved) }
    }

}
