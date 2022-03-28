package io.cloudflight.jems.server.project.controller.report.procurement

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.UpdateProjectPartnerReportProcurementDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementUpdate
import io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement.GetProjectPartnerReportProcurementInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement.UpdateProjectPartnerReportProcurementInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectPartnerReportProcurementControllerTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 800L

        private fun dummyProcurement(reportId: Long) = ProjectPartnerReportProcurement(
            id = 265,
            reportId = reportId,
            reportNumber = 1,
            createdInThisReport = false,
            contractId = "contractId",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN")),
            contractAmount = BigDecimal.TEN,
            supplierName = "supplierName",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
        )

        private fun expectedProcurement(reportId: Long) = ProjectPartnerReportProcurementDTO(
            id = 265,
            reportId = reportId,
            reportNumber = 1,
            createdInThisReport = false,
            contractId = "contractId",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN")),
            contractAmount = BigDecimal.TEN,
            supplierName = "supplierName",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
        )

        private val dummyUpdateProcurement = UpdateProjectPartnerReportProcurementDTO(
            id = 265,
            contractId = "contractId",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN")),
            contractAmount = BigDecimal.TEN,
            supplierName = "supplierName",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
        )

        private val expectedUpdateProcurement = ProjectPartnerReportProcurementUpdate(
            id = 265,
            contractId = "contractId",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN")),
            contractAmount = BigDecimal.TEN,
            supplierName = "supplierName",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
        )

    }

    @MockK
    lateinit var getProcurement: GetProjectPartnerReportProcurementInteractor

    @MockK
    lateinit var updateProcurement: UpdateProjectPartnerReportProcurementInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportProcurementController

    @Test
    fun getProcurement() {
        every { getProcurement.getProcurement(partnerId = PARTNER_ID, reportId = 10L) } returns
            listOf(dummyProcurement(reportId = 10L))
        assertThat(controller.getProcurement(partnerId = PARTNER_ID, reportId = 10L))
            .containsExactly(expectedProcurement(reportId = 10L))
    }

    @Test
    fun getProcurementsForSelector() {
        every { getProcurement.getProcurementsForSelector(partnerId = PARTNER_ID, reportId = 20L) } returns
            listOf(IdNamePair(id = 270L, "contractId"))
        assertThat(controller.getProcurementSelectorList(partnerId = PARTNER_ID, reportId = 20L))
            .containsExactly(IdNamePairDTO(270L, "contractId"))
    }

    @Test
    fun updateProcurement() {
        val slot = slot<List<ProjectPartnerReportProcurementUpdate>>()
        every { updateProcurement.update(partnerId = PARTNER_ID, reportId = 30L, capture(slot)) } returns
            listOf(dummyProcurement(reportId = 30L))

        assertThat(controller.updateProcurement(partnerId = PARTNER_ID, reportId = 30L, listOf(dummyUpdateProcurement)))
            .containsExactly(expectedProcurement(reportId = 30L))

        assertThat(slot.captured).hasSize(1)
        assertThat(slot.captured.first()).isEqualTo(expectedUpdateProcurement)
    }

}
