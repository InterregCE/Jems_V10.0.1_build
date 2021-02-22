package io.cloudflight.jems.server.controller

import io.cloudflight.jems.api.common.dto.APIErrorDTO
import io.cloudflight.jems.server.common.DB_ERROR_WHITE_LIST
import io.cloudflight.jems.server.common.GlobalExceptionHandler
import io.cloudflight.jems.server.common.exception.DuplicateFileException
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.client.RestClientException
import org.springframework.web.context.request.ServletWebRequest
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
        val result = globalExceptionHandler.duplicateFileExceptionTransformer(
            exception,
            ServletWebRequest(MockHttpServletRequest())
        )

        assertThat(result).isNotNull
        assertThat(result.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat((result.body as APIErrorDTO).i18nMessage.i18nKey).isEqualTo("file.duplicate.error")
        assertThat((result.body as APIErrorDTO).i18nMessage.i18nArguments).isEqualTo(hashMapOf(
            Pair("name", exception.error.name),
            Pair("origin", exception.error.origin.name),
            Pair("updated", exception.error.updated.toString())
        ))
    }

    @Test
    fun `resource not found exception is transformed`() {
        val exception = ResourceNotFoundException()
        val result = globalExceptionHandler.error404Transformer(exception, ServletWebRequest(MockHttpServletRequest()))

        assertThat(result.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `exception thrown on non specific nested exception`() {
        assertThrows<RestClientException> {
            globalExceptionHandler.nestedRuntimeExceptionTransformer(RestClientException(MSG_EXPRESS_SURPRISE), ServletWebRequest(MockHttpServletRequest()))
        }
    }

    @Test
    fun `exception transformed on white listed nested exception`() {
        val result = globalExceptionHandler.nestedRuntimeExceptionTransformer(
            RestClientException(MSG_EXPRESS_SURPRISE, Throwable(DB_ERROR_WHITE_LIST[0])), ServletWebRequest(MockHttpServletRequest())
        )

        assertThat(result.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat(result.body?.i18nMessage?.i18nKey).isEqualTo(DB_ERROR_WHITE_LIST[0])
    }

    @Test
    fun `validation exception is transformed`() {
        val exception = I18nValidationException(MSG_EXPRESS_SURPRISE, arrayListOf(), HttpStatus.IM_USED)
        val result = globalExceptionHandler.customErrorTransformer(exception, ServletWebRequest(MockHttpServletRequest()))

        assertThat(result.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
