package io.cloudflight.jems.plugin.pre_condition_check.models

data class PreConditionCheckMessage(
    val messageKey: String,
    val messageType: MessageType,
    val subSectionMessages: List<PreConditionCheckMessage>
)
