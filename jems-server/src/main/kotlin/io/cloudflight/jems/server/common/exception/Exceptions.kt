package io.cloudflight.jems.server.common.exception

import io.cloudflight.jems.api.common.dto.I18nMessage
import org.springframework.http.HttpStatus

open class ApplicationException(
    open val code: String, open val i18nMessage: I18nMessage,
    val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    override val cause: Throwable?, override val message: String = "",
    open val formErrors: Map<String, I18nMessage> = hashMapOf(),
) : RuntimeException()

open class ApplicationNotFoundException(
    override val code: String, override val i18nMessage: I18nMessage,
    override val cause: Throwable?, override val message: String = ""
) : ApplicationException(code, i18nMessage, HttpStatus.NOT_FOUND, cause, message)

open class ApplicationUnprocessableException(
    override val code: String, override val i18nMessage: I18nMessage,
    override val cause: Throwable? = null, override val message: String = "",
    override val formErrors: Map<String, I18nMessage> = hashMapOf()
) : ApplicationException(code, i18nMessage, HttpStatus.UNPROCESSABLE_ENTITY, cause, message)

open class ApplicationBadRequestException(
    override val code: String,
    override val i18nMessage: I18nMessage,
    override val cause: Throwable? = null,
    override val message: String = "",
    override val formErrors: Map<String, I18nMessage> = hashMapOf()
) : ApplicationException(code, i18nMessage, HttpStatus.BAD_REQUEST, cause, message)

open class ApplicationAuthenticationException(
    override val code: String, override val i18nMessage: I18nMessage,
    override val cause: Throwable?, override val message: String = "",
) : ApplicationException(code, i18nMessage, HttpStatus.UNAUTHORIZED, cause, message)

open class ApplicationAccessDeniedException(
    override val code: String, override val i18nMessage: I18nMessage,
    override val cause: Throwable?, override val message: String = "",
) : ApplicationException(code, i18nMessage, HttpStatus.FORBIDDEN, cause, message)
