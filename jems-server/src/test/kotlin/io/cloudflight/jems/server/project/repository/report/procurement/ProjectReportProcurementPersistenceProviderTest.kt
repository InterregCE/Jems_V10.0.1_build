package io.cloudflight.jems.server.project.repository.report.procurement

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementTranslEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
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
import java.time.ZonedDateTime

class ProjectReportProcurementPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 223L

        private const val ID_TO_STAY = 50L
        private const val ID_TO_DELETE = 51L
        private const val ID_TO_UPDATE = 52L
        private const val ID_TO_ADD_1 = -1L
        private const val ID_TO_ADD_2 = -2L

        private val dummyAttachment = ReportProjectFileEntity(
            id = 970L,
            projectId = 4L,
            partnerId = PARTNER_ID,
            path = "",
            minioBucket = "minioBucket",
            minioLocation = "",
            name = "some_file.txt",
            type = mockk(),
            size = 1475,
            user = mockk(),
            uploaded = ZonedDateTime.now(),
        )

        private fun dummyEntity(reportEntity: ProjectPartnerReportEntity) = ProjectPartnerReportProcurementEntity(
            id = 14L,
            reportEntity = reportEntity,
            contractId = "contractId",
            contractAmount = BigDecimal.TEN,
            supplierName = "supplierName",
            attachment = dummyAttachment,
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
            attachment = ProjectReportFileMetadata(dummyAttachment.id, dummyAttachment.name, dummyAttachment.uploaded),
        )

        private fun updateDto(id: Long) = ProjectPartnerReportProcurementUpdate(
            id = id,
            contractId = "contractId NEW",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN NEW")),
            contractAmount = BigDecimal.ONE,
            supplierName = "supplierName NEW",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN NEW")),
        )

        private fun entity(id: Long, reportEntity: ProjectPartnerReportEntity) = ProjectPartnerReportProcurementEntity(
            id = id,
            reportEntity = reportEntity,
            contractId = "contractId",
            contractAmount = BigDecimal.TEN,
            supplierName = "supplierName",
            attachment = dummyAttachment,
        ).apply {
            translatedValues.add(
                ProjectPartnerReportProcurementTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    comment = "comment EN",
                    contractType = "contractType EN",
                )
            )
        }

    }

    @MockK
    lateinit var reportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var reportProcurementRepository: ProjectPartnerReportProcurementRepository

    @MockK
    lateinit var reportFileRepository: ProjectReportFileRepository

    @MockK
    lateinit var minioStorage: MinioStorage

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
    fun existsByProcurementId() {
        every { reportProcurementRepository.existsByReportEntityPartnerIdAndReportEntityIdAndId(PARTNER_ID, reportId = 18L, 45L) } returns false
        assertThat(persistence.existsByProcurementId(PARTNER_ID, reportId = 18L, 45L)).isFalse
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
        val entityToStay = entity(ID_TO_STAY, report)
        val entityToDelete = entity(ID_TO_DELETE, report)
        val entityToUpdate = entity(ID_TO_UPDATE, report)
        every { reportProcurementRepository.findByReportEntityOrderByIdDesc(report) } returns mutableListOf(
            entityToStay, entityToDelete, entityToUpdate
        )

        every { minioStorage.deleteFile(dummyAttachment.minioBucket, dummyAttachment.minioLocation) } answers { }
        every { reportFileRepository.delete(dummyAttachment) } answers { }
        val slotDelete = slot<Iterable<ProjectPartnerReportProcurementEntity>>()
        every { reportProcurementRepository.deleteAll(capture(slotDelete)) } answers { }
        val slotSave = mutableListOf<ProjectPartnerReportProcurementEntity>()
        every { reportProcurementRepository.save(capture(slotSave)) } returnsArgument 0

        persistence.updatePartnerReportProcurement(PARTNER_ID, reportId = reportId, listOf(
            updateDto(id = ID_TO_STAY),
            updateDto(id = ID_TO_UPDATE),
            updateDto(id = ID_TO_ADD_1),
            updateDto(id = ID_TO_ADD_2),
        ))

        assertThat(slotDelete.captured).containsExactly(entityToDelete)
        assertThat(slotSave.map { it.id }).containsExactly(
            // order is important, because not-yet-existing elements will get ID based on insertion order
            ID_TO_ADD_2, ID_TO_ADD_1,
        )
        slotSave.plus(listOf(entityToStay, entityToUpdate)).forEach {
            assertThat(it.reportEntity).isEqualTo(report)
            assertThat(it.contractId).isEqualTo("contractId NEW")
            assertThat(it.contractAmount).isEqualByComparingTo(BigDecimal.ONE)
            assertThat(it.supplierName).isEqualTo("supplierName NEW")
            assertThat(it.translatedValues.first().comment).isEqualTo("comment EN NEW")
            assertThat(it.translatedValues.first().contractType).isEqualTo("contractType EN NEW")
        }
    }

}
