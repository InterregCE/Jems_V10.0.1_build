package io.cloudflight.jems.server.project.repository.report.procurement.beneficial

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.procurement.beneficial.ProjectPartnerReportProcurementBeneficialEntity
import io.cloudflight.jems.server.project.repository.report.procurement.ProjectPartnerReportProcurementRepository
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChange
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectReportProcurementBeneficialPersistenceProviderTest : UnitTest() {

    companion object {
        private val YEARS_AGO_20 = LocalDate.now().minusYears(20)

        private fun dummyEntity(
            procurement: ProjectPartnerReportProcurementEntity,
            id: Long = 14L,
            createdIn: Long = 114L,
        ) = ProjectPartnerReportProcurementBeneficialEntity(
            id = id,
            procurement = procurement,
            createdInReportId = createdIn,
            firstName = "firstName",
            lastName = "lastName",
            birth = YEARS_AGO_20,
            vatNumber = "vatNumber",
        )

        private fun dummyEntityNew(
            procurement: ProjectPartnerReportProcurementEntity,
            id: Long,
            createdIn: Long,
        ) = ProjectPartnerReportProcurementBeneficialEntity(
            id = id,
            procurement = procurement,
            createdInReportId = createdIn,
            firstName = "firstName NEW",
            lastName = "lastName NEW",
            birth = YEARS_AGO_20.minusMonths(1),
            vatNumber = "vatNumber NEW",
        )

        private fun expectedBeneficialOwner(reportId: Long) = ProjectPartnerReportProcurementBeneficialOwner(
            id = 14L,
            reportId = reportId,
            createdInThisReport = false,
            firstName = "firstName",
            lastName = "lastName",
            birth = YEARS_AGO_20,
            vatNumber = "vatNumber",
        )

        private fun beneficialOwnerNew(id: Long) = ProjectPartnerReportProcurementBeneficialChange(
            id = id,
            firstName = "firstName NEW",
            lastName = "lastName NEW",
            birth = YEARS_AGO_20.minusMonths(1),
            vatNumber = "vatNumber NEW",
        )

        private fun expectedBeneficialOwnerNew(id: Long, reportId: Long) = ProjectPartnerReportProcurementBeneficialOwner(
            id = id,
            reportId = reportId,
            firstName = "firstName NEW",
            lastName = "lastName NEW",
            birth = YEARS_AGO_20.minusMonths(1),
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
    lateinit var reportProcurementBeneficialRepository: ProjectPartnerReportProcurementBeneficialRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportProcurementBeneficialPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportProcurementRepository)
        clearMocks(reportProcurementBeneficialRepository)
    }

    @Test
    fun getBeneficialOwnersBeforeAndIncludingReportId() {
        val procurementId = 14L
        val reportId = 114L

        every { reportProcurementBeneficialRepository
            .findTop10ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(
                procurementId = procurementId,
                reportId = reportId,
            )
        } returns listOf(dummyEntity(mockk()))

        assertThat(persistence.getBeneficialOwnersBeforeAndIncludingReportId(procurementId, reportId = reportId))
            .containsExactly(expectedBeneficialOwner(reportId))
    }

    @Test
    fun updateBeneficialOwners() {
        val partnerId = 999L
        val reportId = 116L
        val procurement = procurement(id = 16L)

        every { reportProcurementRepository.findByReportEntityPartnerIdAndId(partnerId, procurement.id) } returns procurement

        val toDeleteEntity = dummyEntity(procurement, id = 20L, createdIn = reportId)
        val updateEntity = dummyEntity(procurement, id = 25L, createdIn = reportId)
        every { reportProcurementBeneficialRepository
            .findTop10ByProcurementAndCreatedInReportIdOrderByCreatedInReportIdAscIdAsc(
                procurement = procurement,
                reportId = reportId,
            )
        } returns listOf(toDeleteEntity, updateEntity)

        val deletedSlot = slot<Iterable<ProjectPartnerReportProcurementBeneficialEntity>>()
        every { reportProcurementBeneficialRepository.deleteAll(capture(deletedSlot)) } answers { }
        every { reportProcurementBeneficialRepository.save(any()) } returnsArgument 0

        every { reportProcurementBeneficialRepository
            .findTop10ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(
                procurementId = procurement.id,
                reportId = reportId,
            )
        } returns listOf(dummyEntityNew(procurement, id = 25L, createdIn = reportId), dummyEntityNew(procurement, id = 0L, createdIn = reportId))

        val ownersList = listOf(
            beneficialOwnerNew(25L) /* to be updated */,
            beneficialOwnerNew(0L) /* to be created */,
        )
        assertThat(persistence.updateBeneficialOwners(partnerId, reportId = reportId, procurementId = procurement.id, ownersList))
            .containsExactly(
                expectedBeneficialOwnerNew(25L, reportId = reportId),
                expectedBeneficialOwnerNew(0L, reportId = reportId),
            )

        assertThat(deletedSlot.captured.map { it.id }).containsExactly(20L /* deleted entity id */)
        assertThat(procurement.lastChanged).isAfter(ZonedDateTime.now().minusMinutes(1))

        assertThat(updateEntity.firstName).isEqualTo("firstName NEW")
        assertThat(updateEntity.lastName).isEqualTo("lastName NEW")
        assertThat(updateEntity.birth).isEqualTo(YEARS_AGO_20.minusMonths(1))
        assertThat(updateEntity.vatNumber).isEqualTo("vatNumber NEW")
    }

}
