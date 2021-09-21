package io.cloudflight.jems.api.common.dto

data class VersionDTO(
    val version: String,
    val commitId: String? = null,
    val helpdeskUrl: String,
    val accessibilityStatementUrl: String,
    val termsAndPrivacyPolicyUrl: String,
)
