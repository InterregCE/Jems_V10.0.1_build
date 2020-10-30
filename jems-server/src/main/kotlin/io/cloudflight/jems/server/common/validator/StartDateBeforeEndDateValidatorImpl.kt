package io.cloudflight.jems.server.common.validator

import io.cloudflight.jems.api.common.validator.StartDateBeforeEndDateValidator
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class StartDateBeforeEndDateValidatorImpl : StartDateBeforeEndDateValidator {

    override fun isEndNotBeforeStart(start: ZonedDateTime, end: ZonedDateTime): Boolean {
        return !end.isBefore(start)
    }

}
