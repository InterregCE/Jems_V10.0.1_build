package io.cloudflight.jems.server.common.validator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Test
import java.math.BigDecimal


internal class GeneralValidatorDefaultImplTest : UnitTest() {

    private val generalValidator = GeneralValidatorDefaultImpl()

    @Test
    fun `should return correct validation result when input length is more than max allowed length`() {
        val input = "input text"
        val maxLength = 3
        val validationResult = generalValidator.maxLength(input, maxLength, "input")

        assertThat(validationResult["input"])
            .isEqualTo(
                I18nMessage(
                    "common.error.field.max.length",
                    mapOf("actualLength" to input.length.toString(), "requiredLength" to maxLength.toString())
                )
            )
    }

    @Test
    fun `should throw TranslationMaxLengthException when translations input length is more than max allowed length`() {
        val input = mutableSetOf(InputTranslation(SystemLanguage.EN, "translation text"))
        val maxLength = 3

        val validationResult = generalValidator.maxLength(input, maxLength, "input")

        assertThat(validationResult["input.${input.first().language.translationKey}"])
            .isEqualTo(
                I18nMessage(
                    "common.error.field.max.length",
                    mapOf(
                        "actualLength" to input.first().translation!!.length.toString(),
                        "requiredLength" to maxLength.toString()
                    )
                )
            )
    }

    @Test
    fun `should throw NotBlankException when input is blank`() {

        val validationResult = generalValidator.notBlank("  ", "input")
        assertThat(validationResult["input"])
            .isEqualTo(I18nMessage("common.error.field.blank"))
    }

    @Test
    fun `should throw MinDecimalException when input is less than min allowed value`() {
        val input = BigDecimal(0)
        val minValue = BigDecimal.ONE
        val validationResult = generalValidator.minDecimal(input, minValue, "input")
        assertThat(validationResult["input"])
            .isEqualTo(
                I18nMessage(
                    "common.error.field.min.decimal",
                    mapOf("minValue" to minValue.toString())
                )
            )
    }

    @Test
    fun `should throw DigitException when input fraction part length is more than max allowed length`() {
        val input = BigDecimal(3.01)
        val maxIntegerLength = 1
        val maxFractionLength = 1
        val validationResult = generalValidator.digits(input, maxIntegerLength, maxFractionLength, "input")

        assertThat(validationResult["input"])
            .isEqualTo(
                I18nMessage(
                    "common.error.field.digit",
                    mapOf(
                        "maxInteger" to maxIntegerLength.toString(),
                        "maxFraction" to maxFractionLength.toString(),
                    )
                )
            )
    }

    @Test
    fun `should throw DigitException when input integer part length is more than max allowed length`() {
        val input = BigDecimal(30.0)
        val maxIntegerLength = 1
        val maxFractionLength = 1
        val validationResult = generalValidator.digits(input, maxIntegerLength, maxFractionLength, "input")

        assertThat(validationResult["input"])
            .isEqualTo(
                I18nMessage(
                    "common.error.field.digit",
                    mapOf(
                        "maxInteger" to maxIntegerLength.toString(),
                        "maxFraction" to maxFractionLength.toString(),
                    )
                )
            )
    }

    @Test
    fun `should throw AppInputValidationException when there is at least one validation error`() {
        val input = "input text"
        val validInput = "12"
        val maxLength = 3
        val exception = catchThrowableOfType(
            {
                generalValidator.throwIfAnyIsInvalid(
                    generalValidator.maxLength(input, maxLength, "input"),
                    generalValidator.maxLength(validInput, maxLength, "validInput")
                )
            },
            AppInputValidationException::class.java
        )
        assertThat(exception.i18nMessage.i18nKey).isEqualTo("common.error.input.invalid")

        assertThat(exception.formErrors["input"])
            .isEqualTo(
                I18nMessage(
                    "common.error.field.max.length",
                    mapOf("actualLength" to input.length.toString(), "requiredLength" to maxLength.toString())
                )
            )

        assertThat(exception.formErrors["validInput"]).isNull()
    }
}
