package io.cloudflight.jems.api.audit.dto

import java.time.ZonedDateTime

data class AuditDTO(
    val id: String?,
    val timestamp: ZonedDateTime?,
    val action: AuditAction?,
    val project: AuditProjectDTO?,
    val user: AuditUserDTO?,
    val description: String?,
)
