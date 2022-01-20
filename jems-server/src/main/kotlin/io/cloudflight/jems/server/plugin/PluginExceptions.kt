package io.cloudflight.jems.server.plugin

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationFailedDependencyException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val PLUGIN_ERROR_CODE_PREFIX = "S-P"
const val PLUGIN_ERROR_KEY_PREFIX = "plugin"

class PluginNotFoundException(pluginKey: String) : ApplicationFailedDependencyException(
    code = "$PLUGIN_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$PLUGIN_ERROR_KEY_PREFIX.not.found", mapOf("pluginKey" to pluginKey))
)
class PluginTypeIsNotValidException(pluginKey: String) : ApplicationFailedDependencyException(
    code = "$PLUGIN_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$PLUGIN_ERROR_KEY_PREFIX.type.is.not.valid", mapOf("pluginKey" to pluginKey))
)

class PluginErrorException(throwable: Throwable, pluginKey: String) : ApplicationUnprocessableException(
    code = "$PLUGIN_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$PLUGIN_ERROR_KEY_PREFIX.has.error", mapOf("pluginKey" to pluginKey)),
    cause = throwable
)

class PluginKeyIsNullOrBlankException : ApplicationBadRequestException(
    code = "$PLUGIN_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$PLUGIN_ERROR_KEY_PREFIX.key.is.null.or.blank")
)

class UnknownPluginTypeException(pluginType: String) : ApplicationBadRequestException(
    code = "$PLUGIN_ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage("$PLUGIN_ERROR_KEY_PREFIX.type.is.unknown", mapOf("pluginType" to pluginType))
)
