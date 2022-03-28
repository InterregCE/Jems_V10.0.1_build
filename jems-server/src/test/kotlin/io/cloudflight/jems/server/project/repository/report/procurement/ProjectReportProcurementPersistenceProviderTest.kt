package io.cloudflight.jems.server.project.repository.report.procurement

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementTranslEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementUpdate
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportProcurementPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 223L

        private const val ID_TO_STAY = 50L
        private const val ID_TO_DELETE = 51L
        private const val ID_TO_UPDATE = 52L
        private const val ID_TO_ADD_1 = 0L
        private const val ID_TO_ADD_2 = 0L

        private fun dummyEntity(reportEntity: ProjectPartnerReportEntity) = ProjectPartnerReportProcurementEntity(
            id = 14L,
            reportEntity = reportEntity,
            contractId = "contractId",
            contractAmount = BigDecimal.TEN,
            supplierName = "supplierName",
        ).apply {
            translatedValues.add(
                ProjectPartnerReportProcurementTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    comment = "comment EN",
                    contractType = "contractType EN",
                )
            )
        }

        private fun expectedProcurement(reportId: Long, reportNumber: Int) = ProjectPartnerReportProcurement(
            id = 14L,
            reportId = reportId,
            reportNumber = reportNumber,
            createdInThisReport = false /* default */,
            contractId = "contractId",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN")),
            contractAmount = BigDecimal.TEN,
            supplierName = "supplierName",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
        )

        private fun updateDto(id: Long) = ProjectPartnerReportProcurementUpdate(
            id = id,
            contractId = "contractId NEW",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN NEW")),
            contractAmount = BigDecimal.ONE,
            supplierName = "supplierName NEW",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN NEW")),
        )
    }

    @MockK
    lateinit var reportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var reportProcurementRepository: ProjectPartnerReportProcurementRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportProcurementPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportRepository)
        clearMocks(reportProcurementRepository)
    }

    @Test
    fun getProcurementIdsForReport() {
        every { reportProcurementRepository.findProcurementIdsForReport(PARTNER_ID, reportId = 18L) } returns setOf(24L)
        assertThat(persistence.getProcurementIdsForReport(PARTNER_ID, reportId = 18L))
            .containsExactly(24L)
    }

    @Test
    fun getProcurementsForReportIds() {
        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns  75L
        every { report.number } returns 1

        every { reportProcurementRepository.findTop50ByReportEntityIdInOrderByReportEntityIdDescIdDesc(setOf(22L)) } returns
            listOf(dummyEntity(report))
        assertThat(persistence.getProcurementsForReportIds(setOf(22L)))
            .containsExactly(expectedProcurement(reportId = 75L, reportNumber = 1))
    }

    @Test
    fun getProcurementContractIdsForReportIds() {
        every { reportProcurementRepository.findProcurementContractIdsForReportsIn(setOf(25L)) } returns setOf("contractId")
        assertThat(persistence.getProcurementContractIdsForReportIds(setOf(25L)))
            .containsExactly("contractId")
    }

    @Test
    fun countProcurementsForReportIds() {
        every { reportProcurementRepository.countByReportEntityIdIn(setOf(27L)) } returns 650L
        assertThat(persistence.countProcurementsForReportIds(setOf(27L))).isEqualTo(650L)
    }

    @Test
    fun updatePartnerReportProcurement() {
        val reportId = 30L
        val report = mockk<ProjectPartnerReportEntity>()
        every { reportRepository.findByIdAndPartnerId(partnerId = PARTNER_ID, id = reportId) } returns report
        every { reportProcurementRepository.findProcurementIdsForReport(PARTNER_ID, reportId = reportId) } returns
            setOf(ID_TO_STAY, ID_TO_DELETE, ID_TO_UPDATE)

        val slotDelete = slot<Iterable<Long>>()
        every { reportProcurementRepository.deleteAllById(capture(slotDelete)) } answers { }
        val slotSave = slot<Iterable<ProjectPartnerReportProcurementEntity>>()
        every { reportProcurementRepository.saveAll(capture(slotSave)) } returnsArgument 0

        persistence.updatePartnerReportProcurement(PARTNER_ID, reportId = reportId, listOf(
            updateDto(id = ID_TO_STAY),
            updateDto(id = ID_TO_UPDATE),
            updateDto(id = ID_TO_ADD_1),
            updateDto(id = ID_TO_ADD_2),
        ))

        assertThat(slotDelete.captured).containsExactly(ID_TO_DELETE)
        assertThat(slotSave.captured.map { it.id }).containsExactly(
            // order is important, because not-yet-existing elements will get ID based on insertion order
            ID_TO_ADD_2, ID_TO_ADD_1, ID_TO_UPDATE, ID_TO_STAY,
        )
        slotSave.captured.forEach {
            assertThat(it.reportEntity).isEqualTo(report)
            assertThat(it.contractId).isEqualTo("contractId NEW")
            assertThat(it.contractAmount).isEqualByComparingTo(BigDecimal.ONE)
            assertThat(it.supplierName).isEqualTo("supplierName NEW")
            assertThat(it.translatedValues.first().comment).isEqualTo("comment EN NEW")
            assertThat(it.translatedValues.first().contractType).isEqualTo("contractType EN NEW")
        }
    }

}
