package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.EquipmentCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.ExternalCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.TravelAndAccommodationCosts
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.repository.CallRepository
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
import java.math.BigDecimal
import java.util.Optional
import kotlin.collections.HashSet
import kotlin.collections.listOf
import kotlin.collections.mapTo
import kotlin.collections.mutableSetOf
import kotlin.collections.setOf

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
    @MockK
    lateinit var callRepository: CallRepository

    private lateinit var programmeUnitCostPersistence: ProgrammeUnitCostPersistence

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        programmeUnitCostPersistence = ProgrammeUnitCostPersistenceProvider(repository)

        testUnitCost = ProgrammeUnitCostEntity(
            id = 1,
            translatedValues = combineUnitCostTranslatedValues(
                programmeUnitCostId = 1,
                name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
                description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
                type = setOf(InputTranslation(SystemLanguage.EN, "type 1"))
            ),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false,
            categories = mutableSetOf(categoryEquipment, categoryTravel),
        )
        expectedUnitCost = ProgrammeUnitCost(
            id = testUnitCost.id,
            name = testUnitCost.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
            description = testUnitCost.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) },
            type = testUnitCost.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.type) },
            costPerUnit = testUnitCost.costPerUnit,
            isOneCostCategory = false,
            categories = setOf(categoryEquipment.category, categoryTravel.category),
        )
    }

    @Test
    fun getUnitCosts() {
        every { repository.findTop100ByOrderById() } returns listOf(testUnitCost)
        assertThat(programmeUnitCostPersistence.getUnitCosts()).containsExactly(expectedUnitCost)
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
            id = 0,
            categories = setOf(ExternalCosts, OfficeAndAdministrationCosts),
        )

        val result = programmeUnitCostPersistence.createUnitCost(toSave)

        assertThat(entitySaved.captured).isEqualTo(
            testUnitCost.copy(
                id = 0,
                translatedValues = combineUnitCostTranslatedValues(
                    programmeUnitCostId = 0,
                    name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
                    description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
                    type = setOf(InputTranslation(SystemLanguage.EN, "type 1"))
                ),
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
        val toBeUpdated = ProgrammeUnitCost(
            id = testUnitCost.id,
            name = setOf(InputTranslation(SystemLanguage.EN, "new name")),
            description = setOf(InputTranslation(SystemLanguage.EN, "new description")),
            type = setOf(InputTranslation(SystemLanguage.EN, "new type")),
            costPerUnit = BigDecimal.TEN,
            isOneCostCategory = false,
            categories = setOf(ExternalCosts, EquipmentCosts),
        )
        every { repository.existsById(testUnitCost.id) } returns true
        val translations = combineUnitCostTranslatedValues(toBeUpdated.id, toBeUpdated.name, toBeUpdated.description, toBeUpdated.type)
        every { repository.save(any()) } returns toBeUpdated.toEntity().copy(translatedValues = translations)

        assertThat(programmeUnitCostPersistence.updateUnitCost(toBeUpdated)).isEqualTo(ProgrammeUnitCost(
            id = testUnitCost.id,
            name = setOf(InputTranslation(SystemLanguage.EN, "new name")),
            description = setOf(InputTranslation(SystemLanguage.EN, "new description")),
            type = setOf(InputTranslation(SystemLanguage.EN, "new type")),
            costPerUnit = BigDecimal.TEN,
            isOneCostCategory = false,
            categories = setOf(ExternalCosts, EquipmentCosts),
        ))
    }

    @Test
    fun `updateUnitCost - not existing`() {
        every { repository.existsById(testUnitCost.id) } returns false
        val toBeUpdated = ProgrammeUnitCost(
            id = testUnitCost.id,
            name = setOf(InputTranslation(SystemLanguage.EN, "new name")),
            description = setOf(InputTranslation(SystemLanguage.EN, "new description")),
            type = setOf(InputTranslation(SystemLanguage.EN, "new type")),
            costPerUnit = BigDecimal.TEN,
            isOneCostCategory = false,
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
