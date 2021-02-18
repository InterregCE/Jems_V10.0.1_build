package io.cloudflight.jems.server.authentication

import io.cloudflight.jems.api.common.dto.APIErrorDTO
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.GlobalExceptionHandler
import io.cloudflight.jems.server.common.exception.ApplicationAuthenticationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

const val AUTHENTICATION_ERROR_CODE = "AUTH"

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class AuthenticationExceptionHandler : GlobalExceptionHandler() {

    @ExceptionHandler(AuthenticationException::class)
    fun authenticationTransformer(exception: AuthenticationException, request: WebRequest): ResponseEntity<APIErrorDTO> {
        return handleApplicationException(
            ApplicationAuthenticationException(
                AUTHENTICATION_ERROR_CODE,
                if (isBadCredentials(exception)) I18nMessage("authentication.bad.credentials") else I18nMessage("authentication.failed"),
                exception
            ),
            request
        )
    }

    private fun isBadCredentials(exception: AuthenticationException): Boolean {
        return exception is BadCredentialsException || exception.cause is BadCredentialsException
    }
}
