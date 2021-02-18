package io.cloudflight.jems.server.common.controller

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

const val ERROR_PATH = "/error"

@RestController
@RequestMapping(ERROR_PATH)
class CustomErrorController(errorAttributes: ErrorAttributes) : AbstractErrorController(errorAttributes) {

    @RequestMapping
    fun error(request: HttpServletRequest?): ResponseEntity<Map<String, Any>>? {
        val body = this.getErrorAttributes(request, ErrorAttributeOptions.defaults())
        val status = getStatus(request)
        return ResponseEntity(body, status)
    }

    override fun getErrorPath(): String {
        return ERROR_PATH
    }
}
