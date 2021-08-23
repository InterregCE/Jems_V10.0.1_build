package io.cloudflight.jems.server.audit.model

data class AuditProject (
    val id: String,
    val customIdentifier: String? = null,
    val name: String? = null,
)
