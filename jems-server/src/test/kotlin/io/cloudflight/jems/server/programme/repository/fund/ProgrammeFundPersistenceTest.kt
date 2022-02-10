package io.cloudflight.jems.server.programme.repository.fund

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundTranslationEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
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
                        translationId = TranslationId(sourceEntity = this, language = EN),
                        abbreviation = "EN abbr",
                        description = "EN desc"
                    ),
                    ProgrammeFundTranslationEntity(
                        translationId = TranslationId(sourceEntity = this, language = SK),
                        abbreviation = "SK abbr",
                        description = "SK desc"
                    ),
                )
            )
        }

        private val fund = ProgrammeFund(
            id = ID,
            selected = true,
            type = ProgrammeFundType.OTHER,
            abbreviation = setOf(InputTranslation(EN, "EN abbr"), InputTranslation(SK, "SK abbr")),
            description = setOf(InputTranslation(EN, "EN desc"), InputTranslation(SK, "SK desc"))
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
        every { repository.deleteAllByIdInBatch(setOf(14, 15)) } answers { }
        every { repository.saveAll(any<List<ProgrammeFundEntity>>()) } returnsArgument 0

        val funds = persistence.updateFunds(
            toDeleteIds = setOf(14, 15),
            funds = setOf(fund.copy(id = 0))
        )
        assertThat(funds).hasSize(1)
        assertThat(funds[0].selected).isTrue()
        assertThat(funds[0].description).isEqualTo(fund.description)
        assertThat(funds[0].abbreviation).isEqualTo(fund.abbreviation)

        verify(exactly = 1) { repository.deleteAllByIdInBatch(setOf(14, 15)) }
    }

}
