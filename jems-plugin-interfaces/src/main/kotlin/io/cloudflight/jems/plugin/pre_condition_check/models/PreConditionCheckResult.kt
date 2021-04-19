package io.cloudflight.jems.plugin.pre_condition_check.models

data class PreConditionCheckResult(
    val messages: List<PreConditionCheckMessage>,
    val isSubmissionAllowed: Boolean
)
