package io.cloudflight.jems.server.common.exception

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.WebRequest
import org.springframework.web.util.WebUtils

const val UNKNOWN_ERROR_MESSAGE = "an error occurred"

class CustomErrorAttributes : DefaultErrorAttributes() {

    override fun getErrorAttributes(webRequest: WebRequest?, options: ErrorAttributeOptions?): MutableMap<String, Any> {
        val defaultErrorCode =
            getStatusCode(webRequest)?.toString() ?: DEFAULT_ERROR_CODE
        return createAPIErrorDTO(
            this.getError(webRequest),
            defaultErrorCode,
            defaultMessage = getDefaultErrorMessage(webRequest)
        ).toMap()
    }

    private fun getDefaultErrorMessage(webRequest: WebRequest?): String {
        val defaultErrorMessage: String

        val errorMessage = webRequest?.getAttribute(WebUtils.ERROR_MESSAGE_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST)?.toString()
        defaultErrorMessage = if (!errorMessage.isNullOrBlank()) {
            errorMessage
        } else {
            try {
                val statusCode = getStatusCode(webRequest)
                if (statusCode == null) UNKNOWN_ERROR_MESSAGE else HttpStatus.valueOf(statusCode).reasonPhrase
            } catch (e: Exception) {
                UNKNOWN_ERROR_MESSAGE
            }
        }
        return defaultErrorMessage
    }

    private fun getStatusCode(webRequest: WebRequest?): Int? {
        val statusCodeAttribute = webRequest?.getAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST)
        return if (statusCodeAttribute != null) statusCodeAttribute as Int else null
    }
}
