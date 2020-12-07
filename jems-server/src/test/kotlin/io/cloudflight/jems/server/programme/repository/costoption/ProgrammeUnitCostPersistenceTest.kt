package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.ExternalCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.EquipmentCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.TravelAndAccommodationCosts
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.Optional

class ProgrammeUnitCostPersistenceTest {

    companion object {
        private val categoryEquipment = ProgrammeUnitCostBudgetCategoryEntity(
            id = 10,
            programmeUnitCostId = 1,
            category = EquipmentCosts,
        )
        private val categoryTravel = ProgrammeUnitCostBudgetCategoryEntity(
            id = 11,
            programmeUnitCostId = 1,
            category = TravelAndAccommodationCosts,
        )
    }

    private lateinit var testUnitCost: ProgrammeUnitCostEntity
    private lateinit var expectedUnitCost: ProgrammeUnitCost

    @MockK
    lateinit var repository: ProgrammeUnitCostRepository

    private lateinit var programmeUnitCostPersistence: ProgrammeUnitCostPersistence

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        programmeUnitCostPersistence = ProgrammeUnitCostPersistenceProvider(repository)

        testUnitCost = ProgrammeUnitCostEntity(
            id = 1,
            name = "UC1",
            description = "test unit cost 1",
            type = "type 1",
            costPerUnit = BigDecimal.ONE,
            categories = mutableSetOf(categoryEquipment, categoryTravel),
        )
        expectedUnitCost = ProgrammeUnitCost(
            id = testUnitCost.id,
            name = testUnitCost.name,
            description = testUnitCost.description,
            type = testUnitCost.type,
            costPerUnit = testUnitCost.costPerUnit,
            categories = setOf(categoryEquipment.category, categoryTravel.category),
        )
    }

    @Test
    fun getUnitCosts() {
        every { repository.findAll(any<Pageable>()) } returns PageImpl(listOf(testUnitCost))
        assertThat(programmeUnitCostPersistence.getUnitCosts(Pageable.unpaged()).content).containsExactly(
            expectedUnitCost
        )
    }

    @Test
    fun getUnitCost() {
        every { repository.findById(1L) } returns Optional.of(testUnitCost)
        assertThat(programmeUnitCostPersistence.getUnitCost(1L)).isEqualTo(expectedUnitCost)
    }

    @Test
    fun `getUnitCost - not existing`() {
        every { repository.findById(-1L) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { programmeUnitCostPersistence.getUnitCost(-1L) }
    }

    @Test
    fun createUnitCost() {
        val entitySaved = slot<ProgrammeUnitCostEntity>()
        every { repository.save(capture(entitySaved)) } returnsArgument 0

        val toSave = expectedUnitCost.copy(
            id = null,
            categories = setOf(ExternalCosts, OfficeAndAdministrationCosts),
        )

        val result = programmeUnitCostPersistence.createUnitCost(toSave)

        assertThat(entitySaved.captured).isEqualTo(
            testUnitCost.copy(
                id = 0,
                categories = mutableSetOf(
                    ProgrammeUnitCostBudgetCategoryEntity(programmeUnitCostId = 0, category = ExternalCosts),
                    ProgrammeUnitCostBudgetCategoryEntity(programmeUnitCostId = 0, category = OfficeAndAdministrationCosts),
                ),
            )
        )
        assertThat(result).isEqualTo(
            expectedUnitCost.copy(id = 0, categories = setOf(ExternalCosts, OfficeAndAdministrationCosts))
        )
    }

    @Test
    fun updateUnitCost() {
        every { repository.findById(testUnitCost.id) } returns Optional.of(testUnitCost)

        val toBeUpdated = ProgrammeUnitCost(
            id = testUnitCost.id,
            name = "new name",
            description = "new description",
            type = "new type",
            costPerUnit = BigDecimal.TEN,
            categories = setOf(ExternalCosts, EquipmentCosts),
        )

        assertThat(programmeUnitCostPersistence.updateUnitCost(toBeUpdated)).isEqualTo(ProgrammeUnitCost(
            id = testUnitCost.id,
            name = "new name",
            description = "new description",
            type = "new type",
            costPerUnit = BigDecimal.TEN,
            categories = setOf(ExternalCosts, EquipmentCosts),
        ))
    }

    @Test
    fun `updateUnitCost - not existing`() {
        every { repository.findById(testUnitCost.id) } returns Optional.empty()

        val toBeUpdated = ProgrammeUnitCost(
            id = testUnitCost.id,
            name = "new name",
            description = "new description",
            type = "new type",
            costPerUnit = BigDecimal.TEN,
            categories = setOf(ExternalCosts, EquipmentCosts),
        )

        assertThrows<ResourceNotFoundException> { programmeUnitCostPersistence.updateUnitCost(toBeUpdated) }
    }

    @Test
    fun deleteUnitCost() {
        val ID = 555L
        every { repository.findById(ID) } returns Optional.of(testUnitCost.copy(id = ID))
        every { repository.delete(any()) } answers {}
        programmeUnitCostPersistence.deleteUnitCost(ID)
        verify { repository.delete(testUnitCost.copy(id = ID)) }
    }

    @Test
    fun `deleteUnitCost - not existing`() {
        every { repository.findById(-1) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { programmeUnitCostPersistence.deleteUnitCost(-1) }
    }

}
