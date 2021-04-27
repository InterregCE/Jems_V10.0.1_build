package io.cloudflight.jems.api.plugin.dto

data class PreConditionCheckResultDTO(
    val messages: List<PreConditionCheckMessageDTO>,
    val isSubmissionAllowed: Boolean
)
