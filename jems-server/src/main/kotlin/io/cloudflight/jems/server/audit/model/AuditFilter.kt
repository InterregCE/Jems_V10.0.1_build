package io.cloudflight.jems.server.audit.model

data class AuditFilter<T>(
    val values: Set<T> = emptySet(),
    val isInverted: Boolean = false,
)
