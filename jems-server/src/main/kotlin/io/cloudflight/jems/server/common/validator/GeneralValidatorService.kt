package io.cloudflight.jems.server.common.validator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

interface GeneralValidatorService {

    fun maxLength(input: String?, maxLength: Int, fieldName: String): Map<String, I18nMessage>

    fun maxLength(translations: Set<InputTranslation>, maxLength: Int, fieldName: String): Map<String, I18nMessage>

    fun notBlank(input: String?, fieldName: String): Map<String, I18nMessage>

    fun notNullOrZero(input: Long?, fieldName: String): Map<String, I18nMessage>

    fun nullOrZero(input: Long?, fieldName: String): Map<String, I18nMessage>

    fun minDecimal(input: BigDecimal?, minValue: BigDecimal, fieldName: String): Map<String, I18nMessage>

    fun digits(input: BigDecimal?, maxIntegerLength: Int, maxFractionLength: Int, fieldName: String): Map<String, I18nMessage>

    fun throwIfAnyIsInvalid(vararg validationResult: Map<String, I18nMessage>)
}
