package io.cloudflight.jems.api.programme.validator

import io.cloudflight.jems.api.programme.dto.InputProgrammePriorityCreate
import io.cloudflight.jems.api.programme.dto.InputProgrammePriorityUpdate
import io.cloudflight.jems.api.programme.dto.ProgrammeObjective
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.ConstraintValidatorContext

internal class PriorityCodeAndTitleValidatorTest {

    @MockK
    lateinit var uniqueProgrammePriorityCodeAndTitleValidator: UniqueProgrammePriorityCodeAndTitleValidator
    @MockK
    lateinit var validatorContext: ConstraintValidatorContext

    private lateinit var priorityCodeAndTitleValidator: PriorityCodeAndTitleValidator

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        priorityCodeAndTitleValidator = PriorityCodeAndTitleValidator(uniqueProgrammePriorityCodeAndTitleValidator)
    }

    @Test
    fun `is valid create objective priority code and title`() {
        val programmePriority = InputProgrammePriorityCreate("code", "title", ProgrammeObjective.PO1, null)
        every { uniqueProgrammePriorityCodeAndTitleValidator.isValid(null, programmePriority.code!!, programmePriority.title!!) } returns true

        assertThat(priorityCodeAndTitleValidator.isValid(programmePriority, validatorContext)).isTrue()
    }

    @Test
    fun `is not valid update objective priority code and title`() {
        val programmePriority = InputProgrammePriorityUpdate(1, "code", "title", ProgrammeObjective.PO1, null)
        every { uniqueProgrammePriorityCodeAndTitleValidator.isValid(1, programmePriority.code!!, programmePriority.title!!) } returns false

        assertThat(priorityCodeAndTitleValidator.isValid(programmePriority, validatorContext)).isFalse()
    }
}
