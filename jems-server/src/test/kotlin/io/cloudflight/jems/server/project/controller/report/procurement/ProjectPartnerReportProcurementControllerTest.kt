package io.cloudflight.jems.server.project.controller.report.procurement

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.UpdateProjectPartnerReportProcurementDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.project.controller.report.dummyFile
import io.cloudflight.jems.server.project.controller.report.dummyFileDto
import io.cloudflight.jems.server.project.controller.report.dummyFileExpected
import io.cloudflight.jems.server.project.controller.report.dummyMultipartFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementUpdate
import io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement.GetProjectPartnerReportProcurementInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement.UpdateProjectPartnerReportProcurementInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.uploadFileToProjectPartnerReportProcurement.UploadFileToProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.partner.procurement.uploadFileToProjectPartnerReportProcurement.UploadFileToProjectPartnerReportProcurementInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.ZonedDateTime

class ProjectPartnerReportProcurementControllerTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 800L
        private val UPLOADED = ZonedDateTime.now().minusWeeks(1)

        private fun dummyProcurement(reportId: Long) = ProjectPartnerReportProcurement(
            id = 265,
            reportId = reportId,
            reportNumber = 1,
            createdInThisReport = false,
            contractId = "contractId",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN")),
            contractAmount = BigDecimal.TEN,
            currencyCode = "PLN",
            supplierName = "supplierName",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
            attachment = ProjectReportFileMetadata(500L, "file.txt", UPLOADED),
        )

        private fun expectedProcurement(reportId: Long) = ProjectPartnerReportProcurementDTO(
            id = 265,
            reportId = reportId,
            reportNumber = 1,
            createdInThisReport = false,
            contractId = "contractId",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN")),
            contractAmount = BigDecimal.TEN,
            currencyCode = "PLN",
            supplierName = "supplierName",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
            attachment = ProjectReportFileMetadataDTO(500L, "file.txt", UPLOADED),
        )

        private val dummyUpdateProcurement = UpdateProjectPartnerReportProcurementDTO(
            id = 265,
            contractId = "contractId",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN")),
            contractAmount = BigDecimal.TEN,
            currencyCode = "PLN",
            supplierName = "supplierName",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
        )

        private val expectedUpdateProcurement = ProjectPartnerReportProcurementUpdate(
            id = 265,
            contractId = "contractId",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN")),
            contractAmount = BigDecimal.TEN,
            currencyCode = "PLN",
            supplierName = "supplierName",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
        )

    }

    @MockK
    lateinit var getProcurement: GetProjectPartnerReportProcurementInteractor

    @MockK
    lateinit var updateProcurement: UpdateProjectPartnerReportProcurementInteractor

    @MockK
    lateinit var uploadFileToProcurement: UploadFileToProjectPartnerReportProcurementInteractor

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

    @Test
    fun uploadFileToProcurement() {
        val slotFile = slot<ProjectFile>()
        every { uploadFileToProcurement.uploadToProcurement(PARTNER_ID, reportId = 35L, 75L, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadFileToProcurement(PARTNER_ID, 35L, 75L, dummyMultipartFile())).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

}
