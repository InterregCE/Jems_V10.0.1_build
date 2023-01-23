package io.cloudflight.jems.server.project.service.report.partner.expenditure.deleteParkedExpenditure

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DeleteParkedExpenditureTest : UnitTest() {

    @MockK
    private lateinit var reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence

    @InjectMockKs
    lateinit var interactor: DeleteParkedExpenditure

    @BeforeEach
    fun reset() {
        clearMocks(reportParkedExpenditurePersistence)
    }

    @Test
    fun deleteParkedExpenditure() {
        every { reportParkedExpenditurePersistence.getParkedExpendituresByIdForPartner(47L, ReportStatus.Certified) } returns mapOf(845L to mockk())
        every { reportParkedExpenditurePersistence.unParkExpenditures(setOf(845L)) } answers { }
        interactor.deleteParkedExpenditure(47L, expenditureId = 845L)
        verify(exactly = 1) { reportParkedExpenditurePersistence.unParkExpenditures(setOf(845L)) }
    }

    @Test
    fun `deleteParkedExpenditure - not existing`() {
        every { reportParkedExpenditurePersistence.getParkedExpendituresByIdForPartner(48L, ReportStatus.Certified) } returns emptyMap()
        interactor.deleteParkedExpenditure(48L, expenditureId = -1L)
        verify(exactly = 0) { reportParkedExpenditurePersistence.unParkExpenditures(any()) }
    }

}
