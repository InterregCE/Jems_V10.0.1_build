package io.cloudflight.jems.server.programme.service.costoption.get_lump_sum

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase.Implementation
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class GetLumpSumInteractorTest {

    companion object {

        private val testLumpSum = ProgrammeLumpSum(
            id = 1,
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test lump sum 1")),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
            categories = setOf(StaffCosts, OfficeAndAdministrationCosts),
            isFastTrack = false
        )

    }

    @MockK
    lateinit var persistence: ProgrammeLumpSumPersistence

    private lateinit var getLumpSumInteractor: GetLumpSumInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        getLumpSumInteractor = GetLumpSum(persistence)
    }

    @Test
    fun `get lump sums`() {
        every { persistence.getLumpSums() } returns listOf(testLumpSum)
        assertThat(getLumpSumInteractor.getLumpSums()).containsExactly(
            testLumpSum.copy()
        )
    }

    @Test
    fun `get lump sum`() {
        every { persistence.getLumpSum(1L) } returns testLumpSum
        assertThat(getLumpSumInteractor.getLumpSum(1L)).isEqualTo(testLumpSum.copy())
    }

    @Test
    fun `get lump sum - not existing`() {
        every { persistence.getLumpSum(-1L) } throws ResourceNotFoundException("programmeLumpSum")
        assertThrows<ResourceNotFoundException> { getLumpSumInteractor.getLumpSum(-1L) }
    }

}
