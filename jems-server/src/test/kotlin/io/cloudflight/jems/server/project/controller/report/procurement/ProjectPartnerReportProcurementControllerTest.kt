package io.cloudflight.jems.server.project.controller.report.procurement

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementChangeDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementSummaryDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementChange
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementSummary
import io.cloudflight.jems.server.project.service.report.partner.procurement.createProjectPartnerReportProcurement.CreateProjectPartnerReportProcurementInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.deleteProjectPartnerReportProcurement.DeleteProjectPartnerReportProcurementInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement.GetProjectPartnerReportProcurementInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement.UpdateProjectPartnerReportProcurementInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectPartnerReportProcurementControllerTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 800L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)

        private fun dummyProcurement(reportId: Long) = ProjectPartnerReportProcurementSummary(
            id = 265,
            reportId = reportId,
            reportNumber = 1,
            createdInThisReport = false,
            lastChanged = YESTERDAY,
            contractName = "contractName 265",
            referenceNumber = "referenceNumber 100",
            contractDate = NEXT_WEEK,
            contractType = "contractType 265",
            contractAmount = BigDecimal.TEN,
            currencyCode = "PLN",
            supplierName = "supplierName 265",
            vatNumber = "vat number 265",
        )

        private fun dummyProcurementDetailNew(reportId: Long) = ProjectPartnerReportProcurement(
            id = 265L,
            reportId = reportId,
            reportNumber = 1,
            createdInThisReport = false,
            lastChanged = YESTERDAY,
            contractName = "contractName NEW",
            referenceNumber = "referenceNumber NEW",
            contractDate = NEXT_WEEK.plusDays(1),
            contractType = "contractType NEW",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "HUF",
            supplierName = "supplierName NEW",
            vatNumber = "vatNumber NEW",
            comment = "comment NEW",
        )

        private fun expectedProcurement(reportId: Long) = ProjectPartnerReportProcurementSummaryDTO(
            id = 265,
            reportId = reportId,
            reportNumber = 1,
            createdInThisReport = false,
            lastChanged = YESTERDAY,
            contractName = "contractName 265",
            referenceNumber = "referenceNumber 100",
            contractDate = NEXT_WEEK,
            contractType = "contractType 265",
            contractAmount = BigDecimal.TEN,
            currencyCode = "PLN",
            supplierName = "supplierName 265",
            vatNumber = "vat number 265",
        )

        private fun expectedUpdatedProcurementDto(reportId: Long) = ProjectPartnerReportProcurementDTO(
            id = 265,
            reportId = reportId,
            reportNumber = 1,
            createdInThisReport = false,
            lastChanged = YESTERDAY,
            contractName = "contractName NEW",
            referenceNumber = "referenceNumber NEW",
            contractDate = NEXT_WEEK.plusDays(1),
            contractType = "contractType NEW",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "HUF",
            supplierName = "supplierName NEW",
            vatNumber = "vatNumber NEW",
            comment = "comment NEW",
        )

        private val dummyUpdateProcurement = ProjectPartnerReportProcurementChangeDTO(
            id = 265,
            contractName = "contractName NEW",
            referenceNumber = "referenceNumber NEW",
            contractDate = NEXT_WEEK.plusDays(1),
            contractType = "contractType NEW",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "HUF",
            supplierName = "supplierName NEW",
            vatNumber = "vatNumber NEW",
            comment = "comment NEW",
        )

        private val expectedUpdatedProcurement = ProjectPartnerReportProcurementChange(
            id = 265,
            contractName = "contractName NEW",
            referenceNumber = "referenceNumber NEW",
            contractDate = NEXT_WEEK.plusDays(1),
            contractType = "contractType NEW",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "HUF",
            supplierName = "supplierName NEW",
            vatNumber = "vatNumber NEW",
            comment = "comment NEW",
        )

    }

    @MockK
    lateinit var getProcurement: GetProjectPartnerReportProcurementInteractor

    @MockK
    lateinit var updateProcurement: UpdateProjectPartnerReportProcurementInteractor

    @MockK
    lateinit var createProcurement: CreateProjectPartnerReportProcurementInteractor

    @MockK
    lateinit var deleteProcurement: DeleteProjectPartnerReportProcurementInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportProcurementController

    @Test
    fun getProcurement() {
        every { getProcurement.getProcurement(partnerId = PARTNER_ID, reportId = 10L, any()) } returns
            PageImpl(listOf(dummyProcurement(reportId = 10L)))
        assertThat(controller.getProcurement(partnerId = PARTNER_ID, reportId = 10L, Pageable.unpaged()).content)
            .containsExactly(expectedProcurement(reportId = 10L))
    }

    @Test
    fun getProcurementById() {
        every { getProcurement.getProcurementById(partnerId = PARTNER_ID, reportId = 11L, any()) } returns
            dummyProcurementDetailNew(11L)
        assertThat(controller.getProcurementById(partnerId = PARTNER_ID, reportId = 11L, procurementId = 265L))
            .isEqualTo(expectedUpdatedProcurementDto(reportId = 11L))
    }

    @Test
    fun addNewProcurement() {
        val slot = slot<ProjectPartnerReportProcurementChange>()
        every { createProcurement.create(partnerId = PARTNER_ID, reportId = 42L, capture(slot)) } returns
            dummyProcurementDetailNew(reportId = 42L)

        assertThat(controller.addNewProcurement(partnerId = PARTNER_ID, reportId = 42L, dummyUpdateProcurement))
            .isEqualTo(expectedUpdatedProcurementDto(reportId = 42L))

        assertThat(slot.captured).isEqualTo(expectedUpdatedProcurement)
    }

    @Test
    fun updateProcurement() {
        val slot = slot<ProjectPartnerReportProcurementChange>()
        every { updateProcurement.update(partnerId = PARTNER_ID, reportId = 30L, capture(slot)) } returns
            dummyProcurementDetailNew(reportId = 30L)

        assertThat(controller.updateProcurement(partnerId = PARTNER_ID, reportId = 30L, dummyUpdateProcurement))
            .isEqualTo(expectedUpdatedProcurementDto(reportId = 30L))

        assertThat(slot.captured).isEqualTo(expectedUpdatedProcurement)
    }

    @Test
    fun deleteProcurement() {
        every { deleteProcurement.delete(partnerId = PARTNER_ID, reportId = 42L, 94578L) } answers { }
        controller.deleteProcurement(partnerId = PARTNER_ID, reportId = 42L, 94578L)
        verify(exactly = 1) { deleteProcurement.delete(partnerId = PARTNER_ID, reportId = 42L, 94578L) }
    }

    @Test
    fun getProcurementsForSelector() {
        every { getProcurement.getProcurementsForSelector(partnerId = PARTNER_ID, reportId = 20L) } returns
            listOf(IdNamePair(id = 270L, "contractId"))
        assertThat(controller.getProcurementSelectorList(partnerId = PARTNER_ID, reportId = 20L))
            .containsExactly(IdNamePairDTO(270L, "contractId"))
    }

}
