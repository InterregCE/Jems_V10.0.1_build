package io.cloudflight.jems.api.plugin.dto

import io.cloudflight.jems.api.common.dto.I18nMessage

data class PreConditionCheckMessageDTO(
    var issueCount: Int = 0,
    val message: I18nMessage,
    val messageType: MessageTypeDTO,
    val subSectionMessages: List<PreConditionCheckMessageDTO>
)
