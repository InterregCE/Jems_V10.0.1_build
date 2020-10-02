package io.cloudflight.ems.indicator.service.validator

import io.cloudflight.ems.api.indicator.validator.UniqueIndicatorIdentifierValidator
import io.cloudflight.ems.indicator.service.IndicatorService
import org.springframework.stereotype.Component

@Component
class UniqueIndicatorIdentifierValidatorImpl(private val indicatorService: IndicatorService) : UniqueIndicatorIdentifierValidator {

    override fun isUniqueForResult(identifier: String): Boolean {
        return !indicatorService.existsResultByIdentifier(identifier)
    }

    override fun isUniqueForOutput(identifier: String): Boolean {
        return !indicatorService.existsOutputByIdentifier(identifier)
    }

}
