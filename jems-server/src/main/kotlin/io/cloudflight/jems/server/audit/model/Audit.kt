package io.cloudflight.jems.server.audit.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.cloudflight.jems.api.audit.dto.AuditAction
import java.time.ZonedDateTime

// if there are more fields in document that we do not care when deserializing
@JsonIgnoreProperties(ignoreUnknown = true)
data class Audit(
    var id: String? = null,
    val timestamp: ZonedDateTime? = null,
    val action: AuditAction? = null,
    val user: AuditUser? = null,
    val project: AuditProject? = null,
    val entityRelatedId: Long? = null,
    val description: String? = null
)
