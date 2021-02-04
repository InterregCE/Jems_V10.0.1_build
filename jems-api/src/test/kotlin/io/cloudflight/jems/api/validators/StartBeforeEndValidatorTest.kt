package io.cloudflight.jems.api.validators

import io.cloudflight.jems.api.call.dto.InputCallCreate
import io.cloudflight.jems.api.call.dto.InputCallUpdate
import io.cloudflight.jems.api.common.validator.StartBeforeEndValidator
import io.cloudflight.jems.api.common.validator.StartDateBeforeEndDateValidator
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.validation.ConstraintValidatorContext

internal class StartBeforeEndValidatorTest {

    @MockK
    lateinit var startBeforeEndDateValidator: StartDateBeforeEndDateValidator
    @MockK
    lateinit var validatorContext: ConstraintValidatorContext

    private lateinit var startBeforeEndValidator: StartBeforeEndValidator

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        startBeforeEndValidator = StartBeforeEndValidator(startBeforeEndDateValidator)
    }

    @Test
    fun `is valid create call date validation`() {
        val startDate = ZonedDateTime.of(2020, 1, 1, 7, 50, 11, 1, ZoneId.systemDefault())
        val endDate = ZonedDateTime.of(2020, 1, 1, 8, 25, 11, 1, ZoneId.systemDefault())
        val call = InputCallCreate("code", setOf(ProgrammeObjectivePolicy.AdvancedTechnologies),null,false,null, startDate, endDate, null, 12)
        every { startBeforeEndDateValidator.isEndNotBeforeStart(startDate, endDate) } returns true

        assertThat(startBeforeEndValidator.isValid(call, validatorContext)).isTrue()
    }

    @Test
    fun `is not valid update call date validation`() {
        val startDate = ZonedDateTime.of(2020, 1, 1, 7, 50, 11, 1, ZoneId.systemDefault())
        val endDate = ZonedDateTime.of(2020, 1, 1, 7, 25, 11, 1, ZoneId.systemDefault())
        val call = InputCallUpdate(1, "code", setOf(ProgrammeObjectivePolicy.AdvancedTechnologies), null, null, startDate, endDate, null, 12)
        every { startBeforeEndDateValidator.isEndNotBeforeStart(startDate, endDate) } returns false

        assertThat(startBeforeEndValidator.isValid(call, validatorContext)).isFalse()
    }
}
