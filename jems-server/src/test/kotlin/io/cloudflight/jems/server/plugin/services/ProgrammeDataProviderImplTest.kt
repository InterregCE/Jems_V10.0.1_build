package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.programme.ProgrammeInfoData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundTypeData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.model.ProgrammeData
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ProgrammeDataProviderImplTest : UnitTest() {
    @MockK
    lateinit var persistence: ProgrammeDataPersistence

    @MockK
    lateinit var fundPersistence: ProgrammeFundPersistence

    @InjectMockKs
    lateinit var programmeDataProviderImpl: ProgrammeDataProviderImpl

    @Test
    fun `should return programme data info`() {
        val date = LocalDate.now()
        val programmeData = ProgrammeData(
            2L, "cci", "title", "1.0", 2000, 2020, date, date, "123", LocalDate.now(), "321", date, "abb", true, 1L
        )
        val programmeFunds = listOf(
            ProgrammeFund(1L, true, ProgrammeFundType.ERDF, emptySet(), emptySet())
        )
        every { persistence.getProgrammeData() } returns programmeData
        every { fundPersistence.getMax20Funds() } returns programmeFunds
        assertThat(programmeDataProviderImpl.getProgrammeData()).isEqualTo(
            ProgrammeInfoData(
                2L, "cci", "title", "1.0", 2000, 2020, date, date, "123", date, "321", LocalDate.now(), "abb", true, 1L,
                listOf(ProgrammeFundData(1L, true, ProgrammeFundTypeData.ERDF, emptySet(), emptySet()))
            )
        )
    }
}
