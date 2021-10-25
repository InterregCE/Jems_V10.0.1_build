package io.cloudflight.jems.api.common.dto

import java.time.ZonedDateTime

data class VersionDTO(
    val version: String,
    val commitId: String? = null,
    val commitIdShort: String? = null,
    val commitTime: ZonedDateTime? = null,
    val helpdeskUrl: String,
    val accessibilityStatementUrl: String,
    val termsAndPrivacyPolicyUrl: String,
)
