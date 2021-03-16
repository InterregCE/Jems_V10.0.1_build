package io.cloudflight.jems.server.call.service.validator

import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import org.springframework.stereotype.Service

@Service
class CallValidator(private val validator: GeneralValidatorService) {

    fun validateCommonCall(call: Call) {
        validator.throwIfAnyIsInvalid(
            validator.notBlank(call.name, "name"),
            validator.maxLength(call.name, 250, "description"),
            validator.maxLength(call.description, 1000, "description"),
            validator.numberBetween(call.lengthOfPeriod, 1, 99, "lengthOfPeriod"),
            validator.startDateBeforeEndDate(call.startDate, call.endDate, "startDateTime", "endDateTime"),
        )
    }

}
