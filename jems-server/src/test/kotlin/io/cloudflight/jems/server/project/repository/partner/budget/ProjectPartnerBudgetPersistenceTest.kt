package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.project.repository.budget.ProjectLumpSumRepository
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import io.cloudflight.jems.server.project.service.partner.model.StaffCostType
import io.cloudflight.jems.server.project.service.partner.model.StaffCostUnitType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.util.Optional

@ExtendWith(MockKExtension::class)
class ProjectPartnerBudgetPersistenceTest {

    companion object {
        private const val PARTNER_ID = 1L

        val staffCostEntry = BudgetStaffCostEntry(
            id = 1,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.TEN,
            rowSum = BigDecimal.TEN,
            type = StaffCostType.REAL_COST,
            unitType = StaffCostUnitType.HOUR)

        val costEntry = BudgetGeneralCostEntry(
            id = 1,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.TEN,
            rowSum = BigDecimal.TEN)

        val travelCostEntry = BudgetTravelAndAccommodationCostEntry(
            id = 1,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.TEN,
            rowSum = BigDecimal.TEN,
            unitType = emptySet()
        )

        val unitCostEntry = BudgetUnitCostEntry(
            id = 1,
            unitCostId = 1,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.TEN
        )
        val programmeUnitCostEntity = ProgrammeUnitCostEntity(
            id = 1,
            name = "test",
            costPerUnit = BigDecimal.TEN,
            type = "test"
        )
    }

    @RelaxedMockK
    lateinit var budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository
    @RelaxedMockK
    lateinit var budgetTravelRepository: ProjectPartnerBudgetTravelRepository
    @RelaxedMockK
    lateinit var budgetExternalRepository: ProjectPartnerBudgetExternalRepository
    @RelaxedMockK
    lateinit var budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository
    @RelaxedMockK
    lateinit var budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository
    @RelaxedMockK
    lateinit var budgetUnitCostRepository: ProjectPartnerBudgetUnitCostRepository
    @RelaxedMockK
    lateinit var budgetLumpSumRepository: ProjectLumpSumRepository
    @RelaxedMockK
    lateinit var programmeUnitCostRepository: ProgrammeUnitCostRepository

    @InjectMockKs
    private lateinit var persistence: ProjectPartnerBudgetPersistenceProvider

    @Test
    fun `deletes all not excluded staff costs`() {
        val keepIds = listOf(2L)
        every {  budgetStaffCostRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(PARTNER_ID, keepIds) } returns Unit
        persistence.deleteAllBudgetStaffCostsExceptFor(PARTNER_ID, keepIds)
        verify{ budgetStaffCostRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(PARTNER_ID, keepIds) }
    }

    @Test
    fun `deletes all not excluded equipment costs`() {
        val keepIds = listOf(2L)
        every {  budgetEquipmentRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(PARTNER_ID, keepIds) } returns Unit
        persistence.deleteAllBudgetEquipmentCostsExceptFor(PARTNER_ID, keepIds)
        verify{ budgetEquipmentRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(PARTNER_ID, keepIds) }
    }

    @Test
    fun `deletes all not excluded external costs`() {
        val keepIds = listOf(2L)
        every {  budgetExternalRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(PARTNER_ID, keepIds) } returns Unit
        persistence.deleteAllBudgetExternalExpertiseAndServicesCostsExceptFor(PARTNER_ID, keepIds)
        verify{ budgetExternalRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(PARTNER_ID, keepIds) }
    }

    @Test
    fun `deletes all not excluded infrastructure costs`() {
        val keepIds = listOf(2L)
        every {  budgetInfrastructureRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(PARTNER_ID, keepIds) } returns Unit
        persistence.deleteAllBudgetInfrastructureAndWorksCostsExceptFor(PARTNER_ID, keepIds)
        verify{ budgetInfrastructureRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(PARTNER_ID, keepIds) }
    }

    @Test
    fun `deletes all not excluded travel costs`() {
        val keepIds = listOf(2L)
        every {  budgetTravelRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(PARTNER_ID, keepIds) } returns Unit
        persistence.deleteAllBudgetTravelAndAccommodationCostsExceptFor(PARTNER_ID, keepIds)
        verify{ budgetTravelRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(PARTNER_ID, keepIds) }
    }

    @Test
    fun `deletes all not excluded unit costs`() {
        val keepIds = setOf(2L)
        every {  budgetUnitCostRepository.deleteAllByPartnerIdAndIdNotIn(PARTNER_ID, keepIds) } returns Unit
        persistence.deleteAllUnitCostsExceptFor(PARTNER_ID, keepIds)
        verify{ budgetUnitCostRepository.deleteAllByPartnerIdAndIdNotIn(PARTNER_ID, keepIds) }
    }

    @Test
    fun `updates staff costs`() {
        val partnerBudgetEntity = staffCostEntry.toProjectPartnerBudgetStaffCostEntity(PARTNER_ID)
        every { budgetStaffCostRepository.saveAll(listOf(partnerBudgetEntity)) } returns listOf(partnerBudgetEntity)
        val result = persistence.createOrUpdateBudgetStaffCosts(PARTNER_ID, listOf(staffCostEntry))
        assertThat(result).isEqualTo(listOf(staffCostEntry))
    }

    @Test
    fun `updates equipment costs`() {
        val partnerBudgetEntity = costEntry.toProjectPartnerBudgetEquipmentEntity(PARTNER_ID)
        every { budgetEquipmentRepository.saveAll(listOf(partnerBudgetEntity)) } returns listOf(partnerBudgetEntity)
        val result = persistence.createOrUpdateBudgetEquipmentCosts(PARTNER_ID, listOf(costEntry))
        assertThat(result).isEqualTo(listOf(costEntry))
    }

    @Test
    fun `updates external and service costs`() {
        val partnerBudgetEntity = costEntry.toProjectPartnerBudgetExternalEntity(PARTNER_ID)
        every { budgetExternalRepository.saveAll(listOf(partnerBudgetEntity)) } returns listOf(partnerBudgetEntity)
        val result = persistence.createOrUpdateBudgetExternalExpertiseAndServicesCosts(PARTNER_ID, listOf(costEntry))
        assertThat(result).isEqualTo(listOf(costEntry))
    }

    @Test
    fun `updates infrastructure costs`() {
        val partnerBudgetEntity = costEntry.toProjectPartnerBudgetInfrastructureEntity(PARTNER_ID)
        every { budgetInfrastructureRepository.saveAll(listOf(partnerBudgetEntity)) } returns listOf(partnerBudgetEntity)
        val result = persistence.createOrUpdateBudgetInfrastructureAndWorksCosts(PARTNER_ID, listOf(costEntry))
        assertThat(result).isEqualTo(listOf(costEntry))
    }

    @Test
    fun `updates travel costs`() {
        val partnerBudgetEntity = travelCostEntry.toProjectPartnerBudgetTravelEntity(PARTNER_ID)
        every { budgetTravelRepository.saveAll(listOf(partnerBudgetEntity)) } returns listOf(partnerBudgetEntity)
        val result = persistence.createOrUpdateBudgetTravelAndAccommodationCosts(PARTNER_ID, listOf(travelCostEntry))
        assertThat(result).isEqualTo(listOf(travelCostEntry))
    }

    @Test
    fun `updates unit costs`() {
        every { programmeUnitCostRepository.findById(programmeUnitCostEntity.id) } returns Optional.of(programmeUnitCostEntity)
        val partnerBudgetEntities = listOf(unitCostEntry).toEntity(PARTNER_ID) { programmeUnitCostEntity }
        every { budgetUnitCostRepository.saveAll(partnerBudgetEntities) } returns partnerBudgetEntities

        val result = persistence.createOrUpdateBudgetUnitCosts(PARTNER_ID, listOf(unitCostEntry))
        assertThat(result).isEqualTo(listOf(unitCostEntry))
    }

}
