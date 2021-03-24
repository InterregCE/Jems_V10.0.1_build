package io.cloudflight.jems.server.common.validator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.project.dto.InputTranslation
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.regex.Pattern

// password should have: at least 10 characters, one upper case letter, one lower case letter and one digit
const val PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{10,}).+\$"

@Service
class GeneralValidatorDefaultImpl : GeneralValidatorService {

    override fun maxLength(input: String?, maxLength: Int, fieldName: String) =
        mutableMapOf<String, I18nMessage>().apply {
            if (!input.isNullOrBlank() && input.length > maxLength)
                this[fieldName] = I18nMessage(
                    "common.error.field.max.length",
                    mapOf("actualLength" to input.length.toString(), "requiredLength" to maxLength.toString())
                )
        }


    override fun maxLength(translations: Set<InputTranslation>, maxLength: Int, fieldName: String) =
        mutableMapOf<String, I18nMessage>().apply {
            translations.forEach {
                if (!it.translation.isNullOrBlank() && it.translation!!.length > maxLength)
                    this["$fieldName.${it.language.translationKey}"] = I18nMessage(
                        "common.error.field.max.length",
                        mapOf(
                            "actualLength" to it.translation!!.length.toString(),
                            "requiredLength" to maxLength.toString()
                        )
                    )
            }
        }

    override fun numberBetween(number: Int, minValue: Int, maxValue: Int, fieldName: String) =
        mutableMapOf<String, I18nMessage>().apply {
            if (number < minValue || number > maxValue)
                this[fieldName] = I18nMessage(
                    i18nKey = "common.error.field.number.out.of.range",
                    i18nArguments = mapOf(
                        "number" to "$number",
                        "min" to "$minValue",
                        "max" to "$maxValue",
                    )
                )
        }

    override fun notBlank(input: String?, fieldName: String) =
        mutableMapOf<String, I18nMessage>().apply {
            if (input.isNullOrBlank())
                this[fieldName] = I18nMessage("common.error.field.blank")
        }

    override fun notNullOrZero(input: Long?, fieldName: String) =
        mutableMapOf<String, I18nMessage>().apply {
            if (input == null || input == 0L)
                this[fieldName] = I18nMessage("common.error.field.should.not.be.null.or.zero")
        }

    override fun nullOrZero(input: Long?, fieldName: String) =
        mutableMapOf<String, I18nMessage>().apply {
            if (input != null && input != 0L)
                this[fieldName] = I18nMessage("common.error.field.should.be.null.or.zero")
        }

    override fun minDecimal(input: BigDecimal?, minValue: BigDecimal, fieldName: String) =
        mutableMapOf<String, I18nMessage>().apply {
            if (input != null && input < minValue)
                this[fieldName] = I18nMessage(
                    "common.error.field.min.decimal",
                    mapOf("minValue" to minValue.toString())
                )
        }

    override fun digits(input: BigDecimal?, maxIntegerLength: Int, maxFractionLength: Int, fieldName: String) =
        mutableMapOf<String, I18nMessage>().apply {
            if (input != null) {
                val integerPartLength: Int = input.precision() - input.scale()
                val fractionPartLength = if (input.scale() < 0) 0 else input.scale()
                if (integerPartLength > maxIntegerLength || fractionPartLength > maxFractionLength)
                    this[fieldName] = I18nMessage(
                        "common.error.field.digit",
                        mapOf(
                            "maxInteger" to maxIntegerLength.toString(),
                            "maxFraction" to maxFractionLength.toString(),
                        )
                    )
            }
        }

    override fun startDateBeforeEndDate(
        start: ZonedDateTime,
        end: ZonedDateTime,
        startDateFieldName: String,
        endDateFieldName: String
    ): Map<String, I18nMessage> =
        mutableMapOf<String, I18nMessage>().apply {
            if (end.isBefore(start)) {
                this[startDateFieldName] = I18nMessage(i18nKey = "common.error.start.before.end")
                this[endDateFieldName] = I18nMessage(i18nKey = "common.error.end.after.start")
            }
        }

    override fun matches(input: String?, regex: String, fieldName: String, errorKey: String?) =
        mutableMapOf<String, I18nMessage>().apply {
            if (!Pattern.matches(regex, input))
                this[fieldName] = I18nMessage(errorKey ?: "common.error.field.pattern")
        }

    override fun throwIfAnyIsInvalid(vararg validationResult: Map<String, I18nMessage>) =
        mutableMapOf<String, I18nMessage>().run {
            validationResult.forEach { this.putAll(it) }
            if (this.isNotEmpty())
                throw AppInputValidationException(this)
        }

}
