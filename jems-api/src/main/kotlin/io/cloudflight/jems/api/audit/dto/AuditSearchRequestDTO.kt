package io.cloudflight.jems.api.audit.dto

import java.time.ZonedDateTime

data class AuditSearchRequestDTO(
    val userIds: Set<Long?> = emptySet(),
    val userEmails: Set<String?> = emptySet(),
    val actions: Set<AuditAction?> = emptySet(),
    val projectIds: Set<String?> = emptySet(),
    val timeFrom: ZonedDateTime? = null,
    val timeTo: ZonedDateTime? = null,
)
