package io.cloudflight.jems.server.programme.service.validator

import io.cloudflight.jems.api.programme.validator.UniqueIndicatorIdentifierValidator
import io.cloudflight.jems.server.programme.service.indicator.IndicatorService
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
