package io.cloudflight.jems.api.audit.dto

import java.time.ZonedDateTime

data class AuditDTO(
    val timestamp: ZonedDateTime? = null,
    val action: AuditAction? = null,
    val project: AuditProjectDTO? = null,
    val user: AuditUserDTO? = null,
    val description: String? = null,
)
