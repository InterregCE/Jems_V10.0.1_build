package io.cloudflight.ems.controller.global

import io.cloudflight.ems.exception.DataValidationException
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

}
