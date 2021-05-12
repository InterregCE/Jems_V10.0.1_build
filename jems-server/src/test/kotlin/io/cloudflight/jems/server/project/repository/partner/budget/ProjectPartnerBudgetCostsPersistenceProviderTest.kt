package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralRow
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostRow
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelCostRow
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/**
 * tests implementation of ProjectPartnerBudgetCostsPersistence including mappings and projectVersionUtils.
 */
class ProjectPartnerBudgetCostsPersistenceProviderTest : ProjectPartnerBudgetCostsPersistenceProviderTestBase() {

    @Test
    fun `should return current version of budget staff costs`() {
        val entity = projectPartnerBudgetStaffCostEntity()
        every { budgetStaffCostRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId) } returns listOf(
            entity
        )
        assertThat(persistence.getBudgetStaffCosts(partnerId))
            .containsExactly(budgetStaffCostEntry(entity))
    }

    @Test
    fun `should return previous version of budget staff costs`() {
        val mockedRow = mockStaffCostRow()
        every {
            budgetStaffCostRepository.findAllByPartnerIdAsOfTimestamp(
                partnerId, timestamp, ProjectPartnerBudgetStaffCostRow::class.java
            )
        } returns listOf(mockedRow)
        assertThat(persistence.getBudgetStaffCosts(partnerId, version))
            .containsExactly(budgetStaffCostEntry(mockedRow))
    }

    @Test
    fun `get budget staff costs total`() {
        every { budgetStaffCostRepository.sumTotalForPartner(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetStaffCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)
        verify { budgetStaffCostRepository.sumTotalForPartner(partnerId) }

        every { budgetStaffCostRepository.sumTotalForPartner(0) } returns null
        assertThat(persistence.getBudgetStaffCostTotal(0)).isEqualTo(BigDecimal.ZERO)
        verify { budgetStaffCostRepository.sumTotalForPartner(0) }
    }

    @Test
    fun `should return current version of travel and accommodation costs`() {
        val entity = projectPartnerBudgetTravelEntity()
        every { budgetTravelRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId) } returns listOf(entity)
        assertThat(persistence.getBudgetTravelAndAccommodationCosts(partnerId))
            .containsExactly(budgetTravelAndAccommodationCostEntry(entity))
    }

    @Test
    fun `should return previous version of travel and accommodation costs`() {
        val mockedRow = mockTravelCostRow()
        every {
            budgetTravelRepository.findAllByPartnerIdAsOfTimestamp(
                partnerId, timestamp, ProjectPartnerBudgetTravelCostRow::class.java
            )
        } returns listOf(mockedRow)
        assertThat(persistence.getBudgetTravelAndAccommodationCosts(partnerId, version))
            .containsExactly(budgetTravelAndAccommodationCostEntry(mockedRow))
    }

    @Test
    fun `get budget travel and accommodation costs total`() {
        every { budgetTravelRepository.sumTotalForPartner(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetTravelAndAccommodationCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)

        every { budgetTravelRepository.sumTotalForPartner(0) } returns null
        assertThat(persistence.getBudgetTravelAndAccommodationCostTotal(0)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `should return current version of infrastructure and works costs`() {
        val entity = projectPartnerBudgetInfrastructureEntity()
        every { budgetInfrastructureRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId) } returns listOf(
            entity
        )
        assertThat(persistence.getBudgetInfrastructureAndWorksCosts(partnerId))
            .containsExactly(budgetGeneralCostEntry(entity))
    }

    @Test
    fun `should return previous version of infrastructure and works`() {
        val mockedRow = mockProjectPartnerBudgetGeneralRow()
        every {
            budgetInfrastructureRepository.findAllByPartnerIdAsOfTimestamp(
                partnerId, timestamp, ProjectPartnerBudgetGeneralRow::class.java
            )
        } returns listOf(mockedRow)
        assertThat(persistence.getBudgetInfrastructureAndWorksCosts(partnerId, version))
            .containsExactly(budgetGeneralCostEntry(mockedRow))
    }

    @Test
    fun `get budget infrastructure and works costs total`() {
        every { budgetInfrastructureRepository.sumTotalForPartner(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)
        every { budgetInfrastructureRepository.sumTotalForPartner(0) } returns null
        assertThat(persistence.getBudgetInfrastructureAndWorksCostTotal(0)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `should return current version of budget external costs`() {
        val entity = projectPartnerBudgetExternalEntity()
        every { budgetExternalRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId) } returns listOf(entity)
        assertThat(persistence.getBudgetExternalExpertiseAndServicesCosts(partnerId))
            .containsExactly(budgetGeneralCostEntry(entity))
    }

    @Test
    fun `should return previous version of budget external costs`() {
        val mockedRow = mockProjectPartnerBudgetGeneralRow()
        every {
            budgetExternalRepository.findAllByPartnerIdAsOfTimestamp(
                partnerId, timestamp, ProjectPartnerBudgetGeneralRow::class.java
            )
        } returns listOf(mockedRow)
        assertThat(persistence.getBudgetExternalExpertiseAndServicesCosts(partnerId, version))
            .containsExactly(budgetGeneralCostEntry(mockedRow))
    }

    @Test
    fun `get budget external costs total`() {
        every { budgetExternalRepository.sumTotalForPartner(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)

        every { budgetExternalRepository.sumTotalForPartner(0) } returns null
        assertThat(persistence.getBudgetExternalExpertiseAndServicesCostTotal(0)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `should return current version of budget equipment costs`() {
        val entity = projectPartnerBudgetEquipmentEntity()
        every { budgetEquipmentRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId) } returns listOf(
            entity
        )
        assertThat(persistence.getBudgetEquipmentCosts(partnerId))
            .containsExactly(budgetGeneralCostEntry(entity))
    }

    @Test
    fun `should return previous version of budget equipment costs`() {
        val mockedRow = mockProjectPartnerBudgetGeneralRow()
        every {
            budgetEquipmentRepository.findAllByPartnerIdAsOfTimestamp(
                partnerId, timestamp, ProjectPartnerBudgetGeneralRow::class.java
            )
        } returns listOf(mockedRow)
        assertThat(persistence.getBudgetEquipmentCosts(partnerId, version))
            .containsExactly(budgetGeneralCostEntry(mockedRow))
    }


    @Test
    fun `get budget equipment costs total`() {
        every { budgetEquipmentRepository.sumTotalForPartner(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetEquipmentCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)

        every { budgetEquipmentRepository.sumTotalForPartner(0) } returns null
        assertThat(persistence.getBudgetEquipmentCostTotal(0)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `should return current version of budget unit costs`() {
        val entity = projectPartnerBudgetUnitCostEntity()
        every { budgetUnitCostRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId) } returns listOf(entity)
        assertThat(persistence.getBudgetUnitCosts(partnerId))
            .containsExactly(budgetUnitCostEntry(entity))
    }

    @Test
    fun `should return previous version of budget unit costs`() {
        val mockedRow = mockProjectPartnerBudgetUnitCostRow()
        every {
            budgetUnitCostRepository.findAllByPartnerIdAsOfTimestamp(partnerId, timestamp)
        } returns listOf(mockedRow)
        assertThat(persistence.getBudgetUnitCosts(partnerId, version))
            .containsExactly(budgetGeneralCostEntry(mockedRow))
    }

    @Test
    fun `get budget unit costs total`() {
        every { budgetUnitCostRepository.sumTotalForPartner(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetUnitCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)

        every { budgetUnitCostRepository.sumTotalForPartner(0) } returns null
        assertThat(persistence.getBudgetUnitCostTotal(0)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `get budget lump sums total`() {
        every { budgetLumpSumRepository.getPartnerLumpSumsTotal(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetLumpSumsCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)

        every { budgetLumpSumRepository.getPartnerLumpSumsTotal(0) } returns null
        assertThat(persistence.getBudgetLumpSumsCostTotal(0)).isEqualTo(BigDecimal.ZERO)
    }

}
