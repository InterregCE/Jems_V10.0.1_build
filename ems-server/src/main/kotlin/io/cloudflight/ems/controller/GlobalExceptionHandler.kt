package io.cloudflight.ems.controller

import io.cloudflight.ems.exception.DuplicateFileException
import io.cloudflight.ems.exception.I18nFieldError
import io.cloudflight.ems.exception.I18nValidationError
import io.cloudflight.ems.exception.ResourceNotFoundException
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

    @ExceptionHandler(I18nValidationError::class)
    fun customErrorTransformer(exception: I18nValidationError): ResponseEntity<Any?> {
        return ResponseEntity
            .status(exception.httpStatus)
            .body(exception)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException, headers: HttpHeaders?,
        status: HttpStatus?, request: WebRequest?
    ): ResponseEntity<Any?>? {
        val fieldErrors = ex.bindingResult.fieldErrors.associateBy(
            { it.field }, { I18nFieldError(it.defaultMessage) }
        )
        return customErrorTransformer(
            I18nValidationError(
                httpStatus = HttpStatus.BAD_REQUEST, i18nFieldErrors = fieldErrors
            )
        )
    }
}
