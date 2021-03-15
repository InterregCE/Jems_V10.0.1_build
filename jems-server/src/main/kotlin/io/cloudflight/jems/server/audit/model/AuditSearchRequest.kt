package io.cloudflight.jems.server.audit.model

import io.cloudflight.jems.api.audit.dto.AuditAction
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

data class AuditSearchRequest(
    val userId: AuditFilter<Long> = AuditFilter(),
    val userEmail: AuditFilter<String> = AuditFilter(),
    val action: AuditFilter<AuditAction> = AuditFilter(),
    val projectId: AuditFilter<String> = AuditFilter(),
    val timeFrom: ZonedDateTime? = null,
    val timeTo: ZonedDateTime? = null,
    val pageable: Pageable = PageRequest.of(0, 20),
)
