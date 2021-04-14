package io.cloudflight.jems.server.common

import io.cloudflight.jems.api.common.dto.APIErrorDTO
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.common.exception.DEFAULT_ERROR_CODE
import io.cloudflight.jems.server.common.exception.DEFAULT_ERROR_MESSAGE
import io.cloudflight.jems.server.common.exception.DuplicateFileException
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.exception.createAPIErrorDTO
import org.slf4j.LoggerFactory
import org.springframework.core.NestedRuntimeException
import org.springframework.expression.ExpressionInvocationTargetException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.util.WebUtils


@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(DuplicateFileException::class)
    fun duplicateFileExceptionTransformer(
        exception: DuplicateFileException,
        request: WebRequest
    ): ResponseEntity<APIErrorDTO> {
        return handleApplicationException(
            ApplicationUnprocessableException(
                DEFAULT_ERROR_CODE,
                I18nMessage(
                    "file.upload.message.duplicate", hashMapOf(
                        Pair("name", exception.error.name),
                        Pair("origin", exception.error.origin.name),
                        Pair("updated", exception.error.updated.toString())
                    )
                ),
                exception
            ),
            request
        )
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun error404Transformer(exception: ResourceNotFoundException, request: WebRequest): ResponseEntity<APIErrorDTO> {
        return handleApplicationException(
            ApplicationNotFoundException(
                code = DEFAULT_ERROR_CODE,
                i18nMessage = if (exception.entity != null) I18nMessage("${exception.entity}.not.exists") else DEFAULT_ERROR_MESSAGE,
                cause = exception
            ),
            request
        )
    }

    /**
     * If there is an exception during @PreAuthorize or @PostAuthorize,
     * this exception is wrapped by Spring into IllegalArgumentException.
     *
     * As this exception is important for us, we need to catch it inside the cause.
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgumentExceptionHandler(
        exception: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<APIErrorDTO> {
        val invTarget = exception.cause
        if (invTarget is ExpressionInvocationTargetException) {
            val cause = invTarget.cause
            if (cause is I18nValidationException)
                return customErrorTransformer(cause, request)
            if (cause is ResourceNotFoundException)
                return error404Transformer(cause, request)
        }
        throw exception
    }

    @ExceptionHandler(NestedRuntimeException::class)
    fun nestedRuntimeExceptionTransformer(
        exception: NestedRuntimeException,
        request: WebRequest
    ): ResponseEntity<APIErrorDTO> {
        if (!DB_ERROR_WHITE_LIST.contains(exception.rootCause?.message))
            throw exception
        return handleApplicationException(
            ApplicationUnprocessableException(
                DEFAULT_ERROR_CODE,
                I18nMessage(exception.rootCause?.message),
                exception
            )
            , request
        )

    }

    @ExceptionHandler(I18nValidationException::class)
    fun customErrorTransformer(exception: I18nValidationException, request: WebRequest): ResponseEntity<APIErrorDTO> {
        val validationError = exception.getData()
        return handleApplicationException(
            ApplicationUnprocessableException(
                DEFAULT_ERROR_CODE,
                I18nMessage(
                    validationError.i18nKey,
                    toI18nArguments(validationError.i18nArguments)
                ),
                exception,
                formErrors = validationError.i18nFieldErrors?.mapValues { it.value.toI18nMessage() } ?: hashMapOf()
            ),
            request
        )
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException, headers: HttpHeaders,
        status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        val result = ex.bindingResult
        val applicationBadRequestException = ApplicationBadRequestException(
            code = "C-INP-ERR",
            i18nMessage = I18nMessage("common.error.input.invalid"),
            cause = ex,
            formErrors = result.fieldErrors.associateBy(
                { it.field }, { I18nMessage(it.defaultMessage) }
            )
        )

        return handleApplicationException(applicationBadRequestException, request) as ResponseEntity<Any>

    }


    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(exception: ApplicationException, request: WebRequest): ResponseEntity<APIErrorDTO> {
        val apiErrorDTO = createAPIErrorDTO(exception)
        GlobalExceptionHandler.logger.error("Error = $apiErrorDTO", exception)
        val httpStatus = getHttpStatus(exception)

        if (HttpStatus.INTERNAL_SERVER_ERROR == httpStatus) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, exception, WebRequest.SCOPE_REQUEST)
        }
        return ResponseEntity<APIErrorDTO>(apiErrorDTO, HttpHeaders(), httpStatus)
    }

    private fun getHttpStatus(exception: ApplicationException): HttpStatus {
        var httpStatus = exception.httpStatus
        var cause = exception.cause
        while (cause != null) {
            if (cause is ApplicationException) {
                httpStatus = cause.httpStatus
            }else if (cause is AccessDeniedException){
                httpStatus= HttpStatus.FORBIDDEN
            }
            cause = cause.cause
        }
        return httpStatus
    }

    private fun I18nFieldError.toI18nMessage() =
        I18nMessage(
            this.i18nKey,
            toI18nArguments(this.i18nArguments)
        )

    private fun toI18nArguments(i18nArgumentList: List<String>?) =
        if (!i18nArgumentList.isNullOrEmpty())
            hashMapOf(*i18nArgumentList.mapIndexed { index, it -> Pair(index.toString(), it) }.toTypedArray())
        else
            hashMapOf()


}
