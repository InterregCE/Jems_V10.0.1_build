package io.cloudflight.jems.server.programme.service.validators

import io.cloudflight.jems.api.programme.dto.InputProgrammeLegalStatus
import io.cloudflight.jems.api.programme.dto.InputProgrammeLegalStatusWrapper
import io.cloudflight.jems.api.programme.validator.ProgrammeLegalStatusInputValidator
import io.cloudflight.jems.server.programme.service.validator.ProgrammeLegalStatusInputValidatorImpl
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.ConstraintValidatorContext

class ProgrammeLegalStatusInputValidatorTest {

    lateinit var programmeLegalStatusInputValidator: ProgrammeLegalStatusInputValidator

    @RelaxedMockK
    lateinit var validatorContext: ConstraintValidatorContext

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        programmeLegalStatusInputValidator = ProgrammeLegalStatusInputValidatorImpl()
    }

    @Test
    fun isValid() {
        val inputLegalStatus = InputProgrammeLegalStatusWrapper(
            toPersist = listOf(
                InputProgrammeLegalStatus(
                    id = 1L
                ),
                InputProgrammeLegalStatus(
                    description = "to be created"
                )
            ),
            toDelete = emptyList()
        )

        assertThat(
            programmeLegalStatusInputValidator.isProgrammeLegalStatusFilledInCorrectly(
                inputLegalStatus,
                validatorContext
            )
        )
            .overridingErrorMessage("when only id or only status is filled in everything is OK (we can distinguish between creation and update of flag)")
            .isTrue()
    }

    @Test
    fun `isNotValid reached maximum of legalStatuses`() {
        val statuses = InputProgrammeLegalStatusWrapper(Array(21) { InputProgrammeLegalStatus() }.toList(), emptyList())

        assertThat(
            programmeLegalStatusInputValidator.isProgrammeLegalStatusFilledInCorrectly(
                statuses,
                validatorContext
            )
        )
            .overridingErrorMessage("As ProgrammeLegalStatus is not paged, we allow only 20 statuses, so it makes sense to not allow them also as input to be processed more of them")
            .isFalse()

        verify {
            validatorContext.buildConstraintViolationWithTemplate("programme.legal.status.wrong.size")
        }
    }
}
