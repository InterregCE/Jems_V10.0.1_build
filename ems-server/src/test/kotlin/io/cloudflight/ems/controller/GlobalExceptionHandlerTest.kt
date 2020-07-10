package io.cloudflight.ems.controller

import io.cloudflight.ems.exception.DuplicateFileException
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClientException
import java.time.ZonedDateTime

internal class GlobalExceptionHandlerTest {

    lateinit var globalExceptionHandler: GlobalExceptionHandler

    val MSG_EXPRESS_SURPRISE = "wehee!"

    @BeforeEach
    fun setup() {
        globalExceptionHandler = GlobalExceptionHandler()
    }

    @Test
    fun `duplicate file exception is transformed`() {
        val exception = DuplicateFileException(1, "identifier", ZonedDateTime.now())
        val result = globalExceptionHandler.duplicateFileExceptionTransformer(exception)

        assertThat(result).isNotNull
        assertThat(result.body).isEqualTo(exception.error)
    }

    @Test
    fun `resource not found exception is transformed`() {
        val exception = ResourceNotFoundException()
        val result = globalExceptionHandler.error404Transformer(exception)

        assertThat(result.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `exception thrown on non specific nested exception`() {
        assertThrows<RestClientException> {
            globalExceptionHandler.nestedRuntimeExceptionTransformer(RestClientException(MSG_EXPRESS_SURPRISE))
        }
    }

    @Test
    fun `exception transformed on white listed nested exception`() {
        val result = globalExceptionHandler.nestedRuntimeExceptionTransformer(
            RestClientException(MSG_EXPRESS_SURPRISE, Throwable(DB_ERROR_WHITE_LIST[0])))

        assertThat(result.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat(result.body?.i18nKey).isEqualTo(DB_ERROR_WHITE_LIST[0])
    }

    @Test
    fun `validation exception is transformed`() {
        val exception = I18nValidationException(MSG_EXPRESS_SURPRISE, arrayListOf(), HttpStatus.IM_USED)
        val result = globalExceptionHandler.customErrorTransformer(exception)

        assertThat(result.statusCode).isEqualTo(HttpStatus.IM_USED)
    }
}
