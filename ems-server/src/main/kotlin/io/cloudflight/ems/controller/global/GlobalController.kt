package io.cloudflight.ems.controller.global

import io.cloudflight.ems.exception.DataValidationException
import io.cloudflight.ems.exception.DuplicateFileException
import io.cloudflight.ems.exception.ResourceNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalController {

    @ExceptionHandler(DataValidationException::class)
    fun validationErrorTransformer(exception: DataValidationException): ResponseEntity<Map<String, List<String>>> {
        return ResponseEntity
            .unprocessableEntity()
            .body(exception.errors)
    }

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

}
