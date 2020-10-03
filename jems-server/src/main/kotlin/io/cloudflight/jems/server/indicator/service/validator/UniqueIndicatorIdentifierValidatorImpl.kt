package io.cloudflight.jems.server.indicator.service.validator

import io.cloudflight.jems.api.indicator.validator.UniqueIndicatorIdentifierValidator
import io.cloudflight.jems.server.indicator.service.IndicatorService
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
