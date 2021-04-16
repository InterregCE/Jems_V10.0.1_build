package io.cloudflight.jems.server.call.service.validator

import io.cloudflight.jems.api.common.dto.I18nMessage
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
            validateEndDateStep1(call),
        )
    }

    fun validateEndDateStep1(call: Call): Map<String, I18nMessage> {
        return mutableMapOf<String, I18nMessage>().apply {
            if (call.is2StepProcedureEnabled() && (call.endDateStep1 == null
                    || call.endDateStep1.isBefore(call.startDate)
                    || call.endDateStep1.isAfter(call.endDate))
            ) this["endDateTimeStep1"] = I18nMessage(i18nKey = "endDateTimeStep1.is.invalid")
        }
    }

}
