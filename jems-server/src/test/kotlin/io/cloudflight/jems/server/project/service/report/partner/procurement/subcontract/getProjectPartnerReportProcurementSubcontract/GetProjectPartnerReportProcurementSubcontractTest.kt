package io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.getProjectPartnerReportProcurementSubcontract

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.ProjectReportProcurementSubcontractPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

internal class GetProjectPartnerReportProcurementSubcontractTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 5776L
        private val YEARS_AGO_50 = LocalDate.now().minusYears(50)

        private val subcontract1 = ProjectPartnerReportProcurementSubcontract(
            id = 100L,
            reportId = 218L,
            createdInThisReport = false,
            contractName = "contractName",
            referenceNumber = "referenceNumber",
            contractDate = YEARS_AGO_50,
            contractAmount = BigDecimal.ONE,
            currencyCode = "PLN",
            supplierName = "supplierName",
            vatNumber = "vatNumber 100",
        )
        private val subcontract2 = ProjectPartnerReportProcurementSubcontract(
            id = 101L,
            reportId = 598L,
            createdInThisReport = false,
            contractName = "contractName",
            referenceNumber = "referenceNumber",
            contractDate = YEARS_AGO_50,
            contractAmount = BigDecimal.ONE,
            currencyCode = "PLN",
            supplierName = "supplierName",
            vatNumber = "vatNumber 101",
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence

    @MockK
    lateinit var reportProcurementSubcontractPersistence: ProjectReportProcurementSubcontractPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectPartnerReportProcurementSubcontract

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, reportProcurementPersistence, reportProcurementSubcontractPersistence)
    }

    @Test
    fun getSubcontract() {
        val reportId = 598L
        val procurementId = 145L

        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns reportId
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        val procurement = mockk<ProjectPartnerReportProcurement>()
        every { procurement.id } returns procurementId
        every { reportProcurementPersistence.getById(PARTNER_ID, procurementId = procurementId) } returns procurement

        every { reportProcurementSubcontractPersistence.getSubcontractBeforeAndIncludingReportId(procurementId, reportId) } returns
            listOf(subcontract1, subcontract2)

        assertThat(interactor.getSubcontract(PARTNER_ID, reportId = reportId, procurementId = procurementId))
            .containsExactly(
                subcontract1.copy(createdInThisReport = false),
                subcontract2.copy(createdInThisReport = true),
            )
    }

}
