package io.cloudflight.jems.server.project.service.report.partner.expenditure.reincludeParkedExpenditure

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class ReIncludeParkedExpenditureTest : UnitTest() {

    @MockK
    private lateinit var reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence
    @MockK
    private lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence

    @InjectMockKs
    private lateinit var interactor: ReIncludeParkedExpenditure

    @Test
    fun reIncludeParkedExpenditure() {
        every { reportExpenditurePersistence.reIncludeParkedExpenditure(4L, 40L, 400L) } returns mockk()
        every { reportParkedExpenditurePersistence.unParkExpenditures(setOf(400L)) } answers { }

        interactor.reIncludeParkedExpenditure(partnerId = 4L, reportId = 40L, expenditureId = 400L)

        verify(exactly = 1) { reportParkedExpenditurePersistence.unParkExpenditures(setOf(400L)) }
    }

}
