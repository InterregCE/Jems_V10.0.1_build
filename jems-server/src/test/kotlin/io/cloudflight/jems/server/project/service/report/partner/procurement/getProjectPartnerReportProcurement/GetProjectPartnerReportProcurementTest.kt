package io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class GetProjectPartnerReportProcurementTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 5776L

        private val procurementFrom53 = ProjectPartnerReportProcurement(
            id = 100L,
            reportId = 53L,
            reportNumber = 1,
            createdInThisReport = false,
            contractId = "contractId 100",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN")),
            contractAmount = BigDecimal.TEN,
            supplierName = "supplierName",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
            attachment = null,
        )
        private val procurementFrom54 = ProjectPartnerReportProcurement(
            id = 101L,
            reportId = 54L,
            reportNumber = 2,
            createdInThisReport = false,
            contractId = "contractId 101",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN")),
            contractAmount = BigDecimal.TEN,
            supplierName = "supplierName",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
            attachment = ProjectReportFileMetadata(45L, "file.txt", ZonedDateTime.now()),
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectPartnerReportProcurement

    @Test
    fun getProcurement() {
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 54) } returns setOf(53L)
        every { reportProcurementPersistence.getProcurementsForReportIds(setOf(53L, 54L)) } returns
            listOf(procurementFrom54, procurementFrom53)

        assertThat(interactor.getProcurement(PARTNER_ID, reportId = 54L))
            .containsExactly(
                procurementFrom54.copy(createdInThisReport = true),
                procurementFrom53.copy(createdInThisReport = false),
            )
    }

    @Test
    fun getProcurementsForSelector() {
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 54) } returns setOf(53L)
        every { reportProcurementPersistence.getProcurementsForReportIds(setOf(53L, 54L)) } returns
            listOf(procurementFrom54, procurementFrom53)

        assertThat(interactor.getProcurementsForSelector(PARTNER_ID, reportId = 54L))
            .containsExactly(
                IdNamePair(101L, "contractId 101"),
                IdNamePair(100L, "contractId 100"),
            )
    }

}
