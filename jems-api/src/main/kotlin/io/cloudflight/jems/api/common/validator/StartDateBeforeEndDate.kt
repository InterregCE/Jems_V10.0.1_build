package io.cloudflight.jems.api.common.validator

import io.cloudflight.jems.api.programme.dto.InputProgrammeData
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Constraint(validatedBy = [StartBeforeEndValidator::class])
annotation class StartDateBeforeEndDate(
    val message: String = "endDate.is.before.startDate",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class StartBeforeEndValidator(private val startBeforeEndValidator: StartDateBeforeEndDateValidator) :
    ConstraintValidator<StartDateBeforeEndDate, Any> {
    override fun isValid(dtoWithTimeRange: Any?, context: ConstraintValidatorContext?): Boolean {

        var start: ZonedDateTime? = null
        var end: ZonedDateTime? = null

        if (dtoWithTimeRange is InputProgrammeData) {
            start = toYear(dtoWithTimeRange.firstYear)
            end = toYear(dtoWithTimeRange.lastYear)
        }

        if (start == null || end == null) return true

        return startBeforeEndValidator.isEndNotBeforeStart(start = start, end = end)
    }

    private fun toYear(year: Int?): ZonedDateTime? {
        if (year == null)
            return null
        return ZonedDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC").normalized())
    }
}

interface StartDateBeforeEndDateValidator {

    fun isEndNotBeforeStart(start: ZonedDateTime, end: ZonedDateTime): Boolean

}
