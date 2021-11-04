package io.cloudflight.jems.server.audit.model

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

data class AuditSearchRequest(
    val userId: AuditFilter<Long> = AuditFilter(),
    val userEmail: AuditFilter<String> = AuditFilter(),
    val action: AuditFilter<String> = AuditFilter(),
    val projectId: AuditFilter<String> = AuditFilter(),
    val timeFrom: ZonedDateTime? = null,
    val timeTo: ZonedDateTime? = null,
    val pageable: Pageable = PageRequest.of(0, 20),
)
