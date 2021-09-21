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
import org.springframework.expression.ExpressionInvocationTargetException
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.client.RestClientException
import org.springframework.web.context.request.ServletWebRequest
import java.time.ZonedDateTime

internal class GlobalExceptionHandlerTest {

    private lateinit var globalExceptionHandler: GlobalExceptionHandler

    companion object {
        const val MSG_EXPRESS_SURPRISE = "wehee!"
        const val MSG_ILLEGAL_ARG = "$"

        val i18nEx = I18nValidationException(i18nKey = "key")
        val e404Ex = ResourceNotFoundException("entityName")
    }

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
        assertThat((result.body as APIErrorDTO).i18nMessage.i18nKey).isEqualTo("file.upload.message.duplicate")
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
    fun `exception thrown by illegal argument exception`() {
        assertThrows<IllegalArgumentException> {
            globalExceptionHandler.illegalArgumentExceptionHandler(IllegalArgumentException(MSG_ILLEGAL_ARG), ServletWebRequest(MockHttpServletRequest()))
        }
    }

    @Test
    fun `response from i18n validation by illegal argument exception`() {
        val i18nException = IllegalArgumentException(ExpressionInvocationTargetException(MSG_ILLEGAL_ARG, i18nEx))
        val response = globalExceptionHandler.illegalArgumentExceptionHandler(i18nException, ServletWebRequest(MockHttpServletRequest()))
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat(response.body?.i18nMessage?.i18nKey).isEqualTo(i18nEx.i18nKey)
    }

    @Test
    fun `response from 404 error by illegal argument exception`() {
        val e404Exception = IllegalArgumentException(ExpressionInvocationTargetException(MSG_ILLEGAL_ARG, e404Ex))
        val response = globalExceptionHandler.illegalArgumentExceptionHandler(e404Exception, ServletWebRequest(MockHttpServletRequest()))
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body?.i18nMessage?.i18nKey).isEqualTo(e404Ex.entity + ".not.exists")
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
