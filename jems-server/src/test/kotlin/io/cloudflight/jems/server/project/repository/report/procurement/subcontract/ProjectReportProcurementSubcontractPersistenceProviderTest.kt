package io.cloudflight.jems.server.project.repository.report.procurement.subcontract

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.procurement.subcontract.ProjectPartnerReportProcurementSubcontractEntity
import io.cloudflight.jems.server.project.repository.report.procurement.ProjectPartnerReportProcurementRepository
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChange
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
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectReportProcurementSubcontractPersistenceProviderTest : UnitTest() {

    companion object {
        private val YEARS_AGO_25 = LocalDate.now().minusYears(25)

        private fun dummyEntity(
            procurement: ProjectPartnerReportProcurementEntity,
            id: Long = 14L,
            createdIn: Long = 114L,
        ) = ProjectPartnerReportProcurementSubcontractEntity(
            id = id,
            procurement = procurement,
            createdInReportId = createdIn,
            contractName = "contractName",
            referenceNumber = "referenceNumber",
            contractDate = YEARS_AGO_25,
            contractAmount = BigDecimal.ONE,
            currencyCode = "PLN",
            supplierName = "supplierName",
            vatNumber = "vatNumber",
        )

        private fun dummyEntityNew(
            procurement: ProjectPartnerReportProcurementEntity,
            id: Long,
            createdIn: Long,
        ) = ProjectPartnerReportProcurementSubcontractEntity(
            id = id,
            procurement = procurement,
            createdInReportId = createdIn,
            contractName = "contractName NEW",
            referenceNumber = "referenceNumber NEW",
            contractDate = YEARS_AGO_25.minusMonths(1),
            contractAmount = BigDecimal.TEN,
            currencyCode = "CZK",
            supplierName = "supplierName NEW",
            vatNumber = "vatNumber NEW",
        )

        private fun expectedSubcontract(reportId: Long) = ProjectPartnerReportProcurementSubcontract(
            id = 14L,
            reportId = reportId,
            createdInThisReport = false,
            contractName = "contractName",
            referenceNumber = "referenceNumber",
            contractDate = YEARS_AGO_25,
            contractAmount = BigDecimal.ONE,
            currencyCode = "PLN",
            supplierName = "supplierName",
            vatNumber = "vatNumber",
        )

        private fun subcontractNew(id: Long) = ProjectPartnerReportProcurementSubcontractChange(
            id = id,
            contractName = "contractName NEW",
            referenceNumber = "referenceNumber NEW",
            contractDate = YEARS_AGO_25.minusMonths(1),
            contractAmount = BigDecimal.TEN,
            currencyCode = "CZK",
            supplierName = "supplierName NEW",
            vatNumber = "vatNumber NEW",
        )

        private fun expectedSubcontractNew(id: Long, reportId: Long) = ProjectPartnerReportProcurementSubcontract(
            id = id,
            reportId = reportId,
            contractName = "contractName NEW",
            referenceNumber = "referenceNumber NEW",
            contractDate = YEARS_AGO_25.minusMonths(1),
            contractAmount = BigDecimal.TEN,
            currencyCode = "CZK",
            supplierName = "supplierName NEW",
            vatNumber = "vatNumber NEW",
        )

        private fun procurement(id: Long) = ProjectPartnerReportProcurementEntity(
            id = id,
            reportEntity = mockk(),
            contractName = "",
            referenceNumber = "",
            contractDate = LocalDate.now(),
            contractType = "",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "",
            supplierName = "",
            vatNumber = "",
            lastChanged = ZonedDateTime.now().minusYears(20),
            comment = "",
        )

    }

    @MockK
    lateinit var reportProcurementRepository: ProjectPartnerReportProcurementRepository
    @MockK
    lateinit var reportProcurementSubcontractRepository: ProjectPartnerReportProcurementSubcontractRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportProcurementSubcontractPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportProcurementRepository)
        clearMocks(reportProcurementSubcontractRepository)
    }

    @Test
    fun getSubcontractBeforeAndIncludingReportId() {
        val procurementId = 14L
        val reportId = 114L

        every { reportProcurementSubcontractRepository
            .findTop50ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(
                procurementId = procurementId,
                reportId = reportId,
            )
        } returns listOf(dummyEntity(mockk()))

        assertThat(persistence.getSubcontractBeforeAndIncludingReportId(procurementId, reportId = reportId))
            .containsExactly(expectedSubcontract(reportId))
    }

    @Test
    fun updateSubcontract() {
        val partnerId = 999L
        val reportId = 116L
        val procurement = procurement(id = 16L)

        every { reportProcurementRepository.findByReportEntityPartnerIdAndId(partnerId, procurement.id) } returns procurement

        val toDeleteEntity = dummyEntity(procurement, id = 20L, createdIn = reportId)
        val updateEntity = dummyEntity(procurement, id = 25L, createdIn = reportId)
        every { reportProcurementSubcontractRepository
            .findTop50ByProcurementAndCreatedInReportIdOrderByCreatedInReportIdAscIdAsc(
                procurement = procurement,
                reportId = reportId,
            )
        } returns listOf(toDeleteEntity, updateEntity)

        val deletedSlot = slot<Iterable<ProjectPartnerReportProcurementSubcontractEntity>>()
        every { reportProcurementSubcontractRepository.deleteAll(capture(deletedSlot)) } answers { }
        every { reportProcurementSubcontractRepository.save(any()) } returnsArgument 0

        every { reportProcurementSubcontractRepository
            .findTop50ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(
                procurementId = procurement.id,
                reportId = reportId,
            )
        } returns listOf(dummyEntityNew(procurement, id = 25L, createdIn = reportId), dummyEntityNew(procurement, id = 0L, createdIn = reportId))

        val subcontractList = listOf(
            subcontractNew(25L) /* to be updated */,
            subcontractNew(0L) /* to be created */,
        )
        assertThat(persistence.updateSubcontract(partnerId, reportId = reportId, procurementId = procurement.id, subcontractList))
            .containsExactly(
                expectedSubcontractNew(25L, reportId = reportId),
                expectedSubcontractNew(0L, reportId = reportId),
            )

        assertThat(deletedSlot.captured.map { it.id }).containsExactly(20L /* deleted entity id */)
        assertThat(procurement.lastChanged).isAfter(ZonedDateTime.now().minusMinutes(1))

        assertThat(updateEntity.contractName).isEqualTo("contractName NEW")
        assertThat(updateEntity.referenceNumber).isEqualTo("referenceNumber NEW")
        assertThat(updateEntity.contractDate).isEqualTo(YEARS_AGO_25.minusMonths(1))
        assertThat(updateEntity.contractAmount).isEqualTo(BigDecimal.TEN)
        assertThat(updateEntity.currencyCode).isEqualTo("CZK")
        assertThat(updateEntity.supplierName).isEqualTo("supplierName NEW")
        assertThat(updateEntity.vatNumber).isEqualTo("vatNumber NEW")
    }

}
