package io.cloudflight.jems.api.programme.validator

import io.cloudflight.jems.api.programme.dto.InputProgrammeFundWrapper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.ConstraintValidatorContext

internal class ProgrammeFundValidatorTest {
    @MockK
    lateinit var programmeFundInputValidator: ProgrammeFundInputValidator
    @MockK
    lateinit var validatorContext: ConstraintValidatorContext

    private lateinit var programmeFundValidator: ProgrammeFundValidator

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        programmeFundValidator = ProgrammeFundValidator(programmeFundInputValidator)
    }

    @Test
    fun isValid() {
        val inputFunds = InputProgrammeFundWrapper(emptyList())
        every { programmeFundInputValidator.isProgrammeFundFilledInCorrectly(inputFunds, validatorContext) } returns true

        assertThat(programmeFundValidator.isValid(inputFunds, validatorContext)).isTrue()
    }
}
