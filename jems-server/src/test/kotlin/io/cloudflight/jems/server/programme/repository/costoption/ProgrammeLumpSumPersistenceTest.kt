package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.EquipmentCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.ExternalCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
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

class ProgrammeLumpSumPersistenceTest {

    companion object {
        private val categoryStaff = ProgrammeLumpSumBudgetCategoryEntity(
            id = 10,
            programmeLumpSumId = 1,
            category = StaffCosts,
        )
        private val categoryOffice = ProgrammeLumpSumBudgetCategoryEntity(
            id = 11,
            programmeLumpSumId = 1,
            category = OfficeAndAdministrationCosts,
        )
    }

    private lateinit var testLumpSum: ProgrammeLumpSumEntity
    private lateinit var expectedLumpSum: ProgrammeLumpSum

    @MockK
    lateinit var repository: ProgrammeLumpSumRepository

    @MockK
    lateinit var callRepository: CallRepository

    private lateinit var programmeLumpSumPersistence: ProgrammeLumpSumPersistence

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        programmeLumpSumPersistence = ProgrammeLumpSumPersistenceProvider(repository, callRepository)

        testLumpSum = ProgrammeLumpSumEntity(
            id = 1,
            translatedValues = combineLumpSumTranslatedValues(
                programmeLumpSumId = 1,
                name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
                description = setOf(InputTranslation(SystemLanguage.EN, "test lump sum 1"))
            ),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = ProgrammeLumpSumPhase.Implementation,
            categories = mutableSetOf(categoryStaff, categoryOffice),
        )
        expectedLumpSum = ProgrammeLumpSum(
            id = testLumpSum.id,
            name = testLumpSum.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
            description = testLumpSum.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) },
            cost = testLumpSum.cost,
            splittingAllowed = testLumpSum.splittingAllowed,
            phase = testLumpSum.phase,
            categories = setOf(categoryStaff.category, categoryOffice.category),
        )
    }

    @Test
    fun getLumpSums() {
        every { repository.findTop25ByOrderById() } returns listOf(testLumpSum)
        assertThat(programmeLumpSumPersistence.getLumpSums()).containsExactly(
            expectedLumpSum
        )
    }

    @Test
    fun getLumpSum() {
        every { repository.findById(1L) } returns Optional.of(testLumpSum)
        assertThat(programmeLumpSumPersistence.getLumpSum(1L)).isEqualTo(expectedLumpSum)
    }

    @Test
    fun `getLumpSum - not existing`() {
        every { repository.findById(-1L) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { programmeLumpSumPersistence.getLumpSum(-1L) }
    }

    @Test
    fun createLumpSum() {
        val entitySaved = slot<ProgrammeLumpSumEntity>()
        every { repository.save(capture(entitySaved)) } returnsArgument 0

        val toSave = expectedLumpSum.copy(id = 0L)

        val result = programmeLumpSumPersistence.createLumpSum(toSave)

        assertThat(entitySaved.captured).isEqualTo(
            testLumpSum.copy(
                id = 0,
                translatedValues = combineLumpSumTranslatedValues(
                    programmeLumpSumId = 0,
                    name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
                    description = setOf(InputTranslation(SystemLanguage.EN, "test lump sum 1"))
                ),
                categories = mutableSetOf(
                    ProgrammeLumpSumBudgetCategoryEntity(programmeLumpSumId = 0, category = StaffCosts),
                    ProgrammeLumpSumBudgetCategoryEntity(programmeLumpSumId = 0, category = OfficeAndAdministrationCosts),
                ),
            )
        )
        assertThat(result).isEqualTo(expectedLumpSum.copy(id = 0))
    }

    @Test
    fun updateLumpSum() {
        val toBeUpdated = ProgrammeLumpSum(
            id = testLumpSum.id,
            name = setOf(InputTranslation(SystemLanguage.EN, "new name")),
            description = setOf(InputTranslation(SystemLanguage.EN, "new description")),
            cost = BigDecimal.TEN,
            splittingAllowed = false,
            phase = ProgrammeLumpSumPhase.Closure,
            categories = setOf(ExternalCosts, EquipmentCosts),
        )
        every { repository.existsById(testLumpSum.id) } returns true
        val translations = combineLumpSumTranslatedValues(toBeUpdated.id!!, toBeUpdated.name, toBeUpdated.description)
        every { repository.save(any()) } returns toBeUpdated.toEntity().copy(translatedValues = translations)


        assertThat(programmeLumpSumPersistence.updateLumpSum(toBeUpdated)).isEqualTo(ProgrammeLumpSum(
            id = testLumpSum.id,
            name = setOf(InputTranslation(SystemLanguage.EN, "new name")),
            description = setOf(InputTranslation(SystemLanguage.EN, "new description")),
            cost = BigDecimal.TEN,
            splittingAllowed = false,
            phase = ProgrammeLumpSumPhase.Closure,
            categories = setOf(ExternalCosts, EquipmentCosts),
        ))
    }

    @Test
    fun `updateLumpSum - not existing`() {
        every { repository.existsById(testLumpSum.id) } returns false

        val toBeUpdated = ProgrammeLumpSum(
            id = testLumpSum.id,
            name = setOf(InputTranslation(SystemLanguage.EN, "new name")),
            description = setOf(InputTranslation(SystemLanguage.EN, "new description")),
            cost = BigDecimal.TEN,
            splittingAllowed = false,
            phase = ProgrammeLumpSumPhase.Closure,
            categories = setOf(ExternalCosts, EquipmentCosts),
        )

        assertThrows<ResourceNotFoundException> { programmeLumpSumPersistence.updateLumpSum(toBeUpdated) }
    }

    @Test
    fun deleteLumpSum() {
        val ID = 555L
        every { repository.findById(ID) } returns Optional.of(testLumpSum.copy(id = ID))
        every { repository.delete(any()) } answers {}
        programmeLumpSumPersistence.deleteLumpSum(ID)
        verify { repository.delete(testLumpSum.copy(id = ID)) }
    }

    @Test
    fun `deleteLumpSum - not existing`() {
        every { repository.findById(-1) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { programmeLumpSumPersistence.deleteLumpSum(-1) }
    }

}
