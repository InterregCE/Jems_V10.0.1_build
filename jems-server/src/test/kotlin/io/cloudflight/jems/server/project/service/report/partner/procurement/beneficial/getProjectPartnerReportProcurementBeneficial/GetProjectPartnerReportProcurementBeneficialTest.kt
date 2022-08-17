package io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.getProjectPartnerReportProcurementBeneficial

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.ProjectReportProcurementBeneficialPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class GetProjectPartnerReportProcurementBeneficialTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 5776L
        private val YEARS_AGO_50 = LocalDate.now().minusYears(50)

        private val beneficialOwner1 = ProjectPartnerReportProcurementBeneficialOwner(
            id = 100L,
            reportId = 218L,
            createdInThisReport = false,
            firstName = "firstName 100",
            lastName = "lastName 100",
            birth = YEARS_AGO_50,
            vatNumber = "vatNumber 100",
        )
        private val beneficialOwner2 = ProjectPartnerReportProcurementBeneficialOwner(
            id = 101L,
            reportId = 598L,
            createdInThisReport = false,
            firstName = "firstName 101",
            lastName = "lastName 101",
            birth = YEARS_AGO_50,
            vatNumber = "vatNumber 101",
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence

    @MockK
    lateinit var reportProcurementBeneficialPersistence: ProjectReportProcurementBeneficialPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectPartnerReportProcurementBeneficial

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, reportProcurementPersistence, reportProcurementBeneficialPersistence)
    }

    @Test
    fun getBeneficialOwner() {
        val reportId = 598L
        val procurementId = 145L

        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns reportId
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        val procurement = mockk<ProjectPartnerReportProcurement>()
        every { procurement.id } returns procurementId
        every { reportProcurementPersistence.getById(PARTNER_ID, procurementId = procurementId) } returns procurement

        every { reportProcurementBeneficialPersistence.getBeneficialOwnersBeforeAndIncludingReportId(procurementId, reportId) } returns
            listOf(beneficialOwner1, beneficialOwner2)

        assertThat(interactor.getBeneficialOwner(PARTNER_ID, reportId = reportId, procurementId = procurementId))
            .containsExactly(
                beneficialOwner1.copy(createdInThisReport = false),
                beneficialOwner2.copy(createdInThisReport = true),
            )
    }

}
