package io.cloudflight.ems.programme.service.validators

import io.cloudflight.ems.api.programme.dto.InputProgrammeFund
import io.cloudflight.ems.api.programme.dto.InputProgrammeFundWrapper
import io.cloudflight.ems.api.programme.validator.ProgrammeFundInputValidator
import io.cloudflight.ems.programme.service.validator.ProgrammeFundInputValidatorImpl
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.ConstraintValidatorContext

class ProgrammeFundInputValidatorTest {

    lateinit var programmeFundInputValidator: ProgrammeFundInputValidator

    @RelaxedMockK
    lateinit var validatorContext: ConstraintValidatorContext

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        programmeFundInputValidator = ProgrammeFundInputValidatorImpl()
    }

    @Test
    fun isValid() {
        val inputFunds = InputProgrammeFundWrapper(funds = listOf(
            InputProgrammeFund(
                id = 1L,
                selected = true
            ),
            InputProgrammeFund(
                abbreviation = "to be created",
                selected = false
            )
        ))

        assertThat(programmeFundInputValidator.isProgrammeFundFilledInCorrectly(inputFunds, validatorContext))
            .overridingErrorMessage("when only id or only abbreviaiton is filled in everything is OK (we can distinguish between creation and update of flag)")
            .isTrue()
    }

    @Test
    fun `isNotValid id and abbreviation filled in`() {
        val inputFunds = InputProgrammeFundWrapper(funds = listOf(InputProgrammeFund(
                id = 1L,
                abbreviation = "this should be empty",
                selected = true
        )))

        assertThat(programmeFundInputValidator.isProgrammeFundFilledInCorrectly(inputFunds, validatorContext))
            .overridingErrorMessage("only id or only abbreviation should be filled in always to distinguish between update of flag and creation of Fund")
            .isFalse()
    }

    @Test
    fun `isNotValid reached maximum of Funds`() {
        val funds = InputProgrammeFundWrapper(Array(21) { InputProgrammeFund() }.toList())

        assertThat(programmeFundInputValidator.isProgrammeFundFilledInCorrectly(funds, validatorContext))
            .overridingErrorMessage("As ProgrammeFund is not paged, we allow only 20 funds, so it makes sense to not allow them also as input to be processed more of them")
            .isFalse()

        verify {
            validatorContext.buildConstraintViolationWithTemplate("programme.fund.wrong.size")
        }
    }
}
