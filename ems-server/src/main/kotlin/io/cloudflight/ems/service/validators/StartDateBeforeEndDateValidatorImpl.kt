package io.cloudflight.ems.service.validators

import io.cloudflight.ems.api.validators.StartDateBeforeEndDateValidator
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class StartDateBeforeEndDateValidatorImpl : StartDateBeforeEndDateValidator {

    override fun isEndNotBeforeStart(start: ZonedDateTime, end: ZonedDateTime): Boolean {
        return !end.isBefore(start)
    }

}
