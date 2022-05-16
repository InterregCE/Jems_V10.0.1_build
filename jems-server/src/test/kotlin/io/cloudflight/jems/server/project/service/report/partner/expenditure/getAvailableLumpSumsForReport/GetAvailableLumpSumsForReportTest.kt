package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableLumpSumsForReport

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetAvailableLumpSumsForReportTest : UnitTest() {

    private val PARTNER_ID = 466L

    private val lumpSumZero = ProjectPartnerReportLumpSum(
        id = 1L,
        lumpSumProgrammeId = 45L,
        period = null,
        cost = BigDecimal.valueOf(0, 1),
        name = setOf(InputTranslation(SystemLanguage.EN, "first EN")),
    )
    private val lumpSumNonZero = ProjectPartnerReportLumpSum(
        id = 2L,
        lumpSumProgrammeId = 46L,
        period = null,
        cost = BigDecimal.valueOf(10, 1),
        name = setOf(InputTranslation(SystemLanguage.EN, "second EN")),
    )

    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence

    @InjectMockKs
    lateinit var interactor: GetAvailableLumpSumsForReport

    @Test
    fun getLumpSums() {
        every { reportExpenditurePersistence.getAvailableLumpSums(PARTNER_ID, 10L) } returns
            listOf(lumpSumZero, lumpSumNonZero)
        assertThat(interactor.getLumpSums(PARTNER_ID, 10L)).containsExactly(lumpSumNonZero)
    }

}
