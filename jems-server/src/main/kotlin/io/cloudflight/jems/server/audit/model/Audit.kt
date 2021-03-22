package io.cloudflight.jems.server.audit.model

import io.cloudflight.jems.api.audit.dto.AuditAction
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class Audit(
    val id: String? = null,
    val timestamp: ZonedDateTime? = ZonedDateTime.now(ZoneOffset.UTC),
    val action: AuditAction? = null,
    val user: AuditUser? = null,
    val project: AuditProject? = null,
    val entityRelatedId: Long? = null,
    val description: String? = null
) {

    companion object {
        fun getTimeFromString(timeString: String?): ZonedDateTime? =
            timeString?.let { ZonedDateTime.parse(it!!, DateTimeFormatter.ISO_DATE_TIME) }
    }

    fun getTimestampString(): String? = timestamp?.format(DateTimeFormatter.ISO_DATE_TIME)

}
