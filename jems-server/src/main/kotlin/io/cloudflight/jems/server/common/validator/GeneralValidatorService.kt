package io.cloudflight.jems.server.common.validator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

interface GeneralValidatorService {

    fun exactLength(input: String?, length: Int, fieldName: String): Map<String, I18nMessage>

    fun maxLength(input: String?, maxLength: Int, fieldName: String): Map<String, I18nMessage>

    fun maxLength(translations: Set<InputTranslation>, maxLength: Int, fieldName: String): Map<String, I18nMessage>

    fun numberBetween(number: Int?, minValue: Int, maxValue: Int, fieldName: String): Map<String, I18nMessage>

    fun numberBetween(
        number: BigDecimal?, minValue: BigDecimal, maxValue: BigDecimal, fieldName: String
    ): Map<String, I18nMessage>

    fun scale(number: BigDecimal?, maxScale: Int, fieldName: String): Map<String, I18nMessage>

    fun notBlank(input: String?, fieldName: String): Map<String, I18nMessage>

    fun notNull(input: Any?, fieldName: String): Map<String, I18nMessage>

    fun notNullOrZero(input: Long?, fieldName: String): Map<String, I18nMessage>

    fun nullOrZero(input: Long?, fieldName: String): Map<String, I18nMessage>

    fun minDecimal(input: BigDecimal?, minValue: BigDecimal, fieldName: String): Map<String, I18nMessage>

    fun digits(
        input: BigDecimal?, maxIntegerLength: Int, maxFractionLength: Int, fieldName: String
    ): Map<String, I18nMessage>

    fun onlyDigits(input: String?, fieldName: String): Map<String, I18nMessage>

    fun startDateBeforeEndDate(
        start: ZonedDateTime?, end: ZonedDateTime?, startDateFieldName: String, endDateFieldName: String
    ): Map<String, I18nMessage>

    fun startDateBeforeEndDate(
        start: LocalDate?, end: LocalDate?, startDateFieldName: String, endDateFieldName: String
    ): Map<String, I18nMessage>

    fun dateNotInFuture(date: LocalDate, fieldName: String): Map<String, I18nMessage>

    fun matches(input: String?, regex: String, fieldName: String, errorKey: String?): Map<String, I18nMessage>

    fun maxSize(items: Collection<Any>?, maxSize: Int, fieldName: String): Map<String, I18nMessage>

    fun minSize(items: Collection<Any>?, minSize: Int, fieldName: String): Map<String, I18nMessage>

    fun notEqualTo(item: String?, compareTo: String, fieldName: String): Map<String, I18nMessage>

    fun onlyValidCurrencies(currencyCodes: Set<String>, fieldName: String): Map<String, I18nMessage>

    fun throwIfAnyIsInvalid(vararg validationResult: Map<String, I18nMessage>)
}
