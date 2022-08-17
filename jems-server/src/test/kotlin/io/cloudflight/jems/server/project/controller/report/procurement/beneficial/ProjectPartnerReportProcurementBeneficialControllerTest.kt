package io.cloudflight.jems.server.project.controller.report.procurement.beneficial

import io.cloudflight.jems.api.project.dto.report.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChangeDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChange
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.getProjectPartnerReportProcurementBeneficial.GetProjectPartnerReportProcurementBeneficialInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.updateProjectPartnerReportProcurement.UpdateProjectPartnerReportProcurementBeneficialInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectPartnerReportProcurementBeneficialControllerTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 820L
        private val YEARS_AGO_10 = LocalDate.now().minusYears(10)

        private fun dummyBeneficialOwner(reportId: Long) = ProjectPartnerReportProcurementBeneficialOwner(
            id = 270,
            reportId = reportId,
            createdInThisReport = false,
            firstName = "firstName 270",
            lastName = "lastName 270",
            birth = YEARS_AGO_10,
            vatNumber = "vat number 270",
        )

        private fun expectedBeneficialOwner(reportId: Long) = ProjectPartnerReportProcurementBeneficialDTO(
            id = 270,
            reportId = reportId,
            createdInThisReport = false,
            firstName = "firstName 270",
            lastName = "lastName 270",
            birth = YEARS_AGO_10,
            vatNumber = "vat number 270",
        )

        private val dummyUpdateBeneficial = ProjectPartnerReportProcurementBeneficialChangeDTO(
            id = 275,
            firstName = "firstName 275 NEW",
            lastName = "lastName 275 NEW",
            birth = YEARS_AGO_10.minusDays(1),
            vatNumber = "vat number 275 NEW",
        )

        private val dummyUpdateBeneficialModel = ProjectPartnerReportProcurementBeneficialChange(
            id = 275,
            firstName = "firstName 275 NEW",
            lastName = "lastName 275 NEW",
            birth = YEARS_AGO_10.minusDays(1),
            vatNumber = "vat number 275 NEW",
        )

    }

    @MockK
    lateinit var getBeneficialOwner: GetProjectPartnerReportProcurementBeneficialInteractor
    @MockK
    lateinit var updateBeneficialOwner: UpdateProjectPartnerReportProcurementBeneficialInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportProcurementBeneficialController

    @Test
    fun getProcurement() {
        every { getBeneficialOwner.getBeneficialOwner(PARTNER_ID, reportId = 15L, procurementId = 80L) } returns
            listOf(dummyBeneficialOwner(reportId = 15L))
        assertThat(controller.getBeneficialOwners(partnerId = PARTNER_ID, reportId = 15L, procurementId = 80L))
            .containsExactly(expectedBeneficialOwner(reportId = 15L))
    }

    @Test
    fun updateBeneficialOwners() {
        val ownersListSlot = slot<List<ProjectPartnerReportProcurementBeneficialChange>>()
        every { updateBeneficialOwner.update(PARTNER_ID, reportId = 17L, procurementId = 45L, capture(ownersListSlot)) } returns
            listOf(dummyBeneficialOwner(reportId = 15L))

        assertThat(controller.updateBeneficialOwners(PARTNER_ID, reportId = 17L, procurementId = 45L, listOf(dummyUpdateBeneficial)))
            .containsExactly(expectedBeneficialOwner(reportId = 15L))

        assertThat(ownersListSlot.captured).containsExactly(dummyUpdateBeneficialModel)
    }

}
