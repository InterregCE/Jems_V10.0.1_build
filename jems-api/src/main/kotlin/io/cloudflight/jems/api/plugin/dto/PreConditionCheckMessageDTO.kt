package io.cloudflight.jems.api.plugin.dto

data class PreConditionCheckMessageDTO(
    var issueCount: Int = 0,
    val messageKey: String,
    val messageType: MessageTypeDTO,
    val subSectionMessages: List<PreConditionCheckMessageDTO>
)
