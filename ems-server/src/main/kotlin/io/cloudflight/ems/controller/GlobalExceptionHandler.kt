package io.cloudflight.ems.controller

import io.cloudflight.ems.exception.DuplicateFileException
import io.cloudflight.ems.exception.I18nFieldError
import io.cloudflight.ems.exception.I18nValidationError
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import org.springframework.core.NestedRuntimeException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(DuplicateFileException::class)
    fun duplicateFileExceptionTransformer(exception: DuplicateFileException): ResponseEntity<DuplicateFileException.DuplicateFileError> {
        return ResponseEntity
            .unprocessableEntity()
            .body(exception.error)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun error404Transformer(exception: ResourceNotFoundException): ResponseEntity<Void> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(NestedRuntimeException::class)
    fun nestedRuntimeExceptionTransformer(exception: NestedRuntimeException): ResponseEntity<I18nValidationError> {
        if (!DB_ERROR_WHITE_LIST.contains(exception.rootCause?.message))
            throw exception

        return ResponseEntity
            .unprocessableEntity()
            .body(
                I18nValidationError(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = exception.rootCause?.message
                )
            )
    }

    @ExceptionHandler(I18nValidationException::class)
    fun customErrorTransformer(exception: I18nValidationException): ResponseEntity<I18nValidationError> {
        return ResponseEntity
            .status(exception.httpStatus)
            .body(exception.getData())
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException, headers: HttpHeaders?,
        status: HttpStatus?, request: WebRequest?
    ): ResponseEntity<Any> {
        val result = ex.bindingResult

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                I18nValidationError(
                    httpStatus = HttpStatus.BAD_REQUEST,
                    i18nKey = result.globalError?.defaultMessage,
                    i18nFieldErrors = result.fieldErrors.associateBy(
                        { it.field }, { I18nFieldError(it.defaultMessage) }
                    )
                )
            )
    }
}
