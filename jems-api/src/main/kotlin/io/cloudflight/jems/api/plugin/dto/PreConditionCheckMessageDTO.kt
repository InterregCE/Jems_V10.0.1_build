package io.cloudflight.jems.api.plugin.dto

data class PreConditionCheckMessageDTO(
    val messageKey: String,
    val messageType: MessageTypeDTO,
    val subSectionMessages: List<PreConditionCheckMessageDTO>
)
