package io.cloudflight.ems.programme.service.validators

import io.cloudflight.ems.api.programme.dto.OutputProgrammePriority
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective.PO1
import io.cloudflight.ems.api.programme.validator.UniqueProgrammePriorityCodeAndTitleValidator
import io.cloudflight.ems.programme.service.ProgrammePriorityService
import io.cloudflight.ems.programme.service.validator.UniqueProgrammePriorityCodeAndTitleValidatorImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UniqueProgrammePriorityCodeAndTitleValidatorTest {

    @MockK
    lateinit var programmePriorityService: ProgrammePriorityService

    lateinit var uniqueProgrammePriorityCodeAndTitleValidator: UniqueProgrammePriorityCodeAndTitleValidator

    private val programmePriority = OutputProgrammePriority(
        id = 0,
        code = "",
        title = "",
        objective = PO1,
        programmePriorityPolicies = emptyList()
    )

    private fun getExistingPriorityWithId(id: Long) = programmePriority.copy(id = id)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        uniqueProgrammePriorityCodeAndTitleValidator =
            UniqueProgrammePriorityCodeAndTitleValidatorImpl(programmePriorityService)
    }

    @Test
    fun `update existing priority code with existing code`() {
        every { programmePriorityService.getByCode("existingCode") } returns getExistingPriorityWithId(78)
        every { programmePriorityService.getByTitle("notExistingTitle") } returns null

        assertFalse(
            uniqueProgrammePriorityCodeAndTitleValidator.isValid(4, "existingCode", "notExistingTitle"),
            "we cannot change code, because this code is already in use by other priority"
        )
    }

    @Test
    fun `update existing priority title with existing title`() {
        every { programmePriorityService.getByCode("notExistingCode") } returns getExistingPriorityWithId(42)
        every { programmePriorityService.getByTitle("existingTitle") } returns null

        assertFalse(
            uniqueProgrammePriorityCodeAndTitleValidator.isValid(17, "notExistingCode", "existingTitle"),
            "we cannot change title, because this title is already in use by other priority"
        )
    }

    @Test
    fun `update existing priority code with not existing code and not existing title`() {
        every { programmePriorityService.getByCode("notExistingCode") } returns null
        every { programmePriorityService.getByTitle("notExistingTitle") } returns null

        assertTrue(
            uniqueProgrammePriorityCodeAndTitleValidator.isValid(1, "notExistingCode", "notExistingTitle"),
            "we can change code and title because those are unique"
        )
    }

    @Test
    fun `update priority code and title without change`() {
        val ID = 407L
        every { programmePriorityService.getByCode("existingSameCode") } returns getExistingPriorityWithId(ID)
        every { programmePriorityService.getByTitle("existingSameTitle") } returns getExistingPriorityWithId(ID)

        assertTrue(
            uniqueProgrammePriorityCodeAndTitleValidator.isValid(ID, "existingSameCode", "existingSameTitle"),
            "we can mark this change as valid, as it is actually no-change"
        )
    }

    @Test
    fun `create new priority with not existing code and not existing title`() {
        every { programmePriorityService.getByCode("notExistingCode") } returns null
        every { programmePriorityService.getByTitle("notExistingTitle") } returns null

        assertTrue(
            uniqueProgrammePriorityCodeAndTitleValidator.isValid(null, "notExistingCode", "notExistingTitle"),
            "we can create priority with this code and title because those are unique"
        )
    }

    @Test
    fun `create new priority with existing code`() {
        every { programmePriorityService.getByCode("existingCode") } returns getExistingPriorityWithId(16)
        every { programmePriorityService.getByTitle("notExistingTitle") } returns null

        assertFalse(
            uniqueProgrammePriorityCodeAndTitleValidator.isValid(null, "existingCode", "notExistingTitle"),
            "we cannot create priority when this code is already in use"
        )
    }

    @Test
    fun `create new priority with existing title`() {
        every { programmePriorityService.getByCode("notExistingCode") } returns null
        every { programmePriorityService.getByTitle("existingTitle") } returns getExistingPriorityWithId(22)

        assertFalse(
            uniqueProgrammePriorityCodeAndTitleValidator.isValid(null, "notExistingCode", "existingTitle"),
            "we cannot create priority when this title is already in use"
        )
    }

}
