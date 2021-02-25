package io.cloudflight.jems.server.programme.service.fund.get_fund

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundTranslatedValue
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetFundTest : UnitTest() {

    companion object {
        private val fund = ProgrammeFund(
            id = 14,
            selected = false,
            translatedValues = setOf(ProgrammeFundTranslatedValue(language = EN, abbreviation = "EN abbr", description = "EN desc"))
        )
    }

    @MockK
    lateinit var persistence: ProgrammeFundPersistence

    @InjectMockKs
    lateinit var getFund: GetFund

    @Test
    fun getLegalStatuses() {
        every { persistence.getMax20Funds() } returns listOf(fund)
        assertThat(getFund.getFunds()).containsExactly(fund)
    }

}
