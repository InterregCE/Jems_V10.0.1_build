package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.server.audit.model.Audit
import io.cloudflight.jems.server.audit.model.AuditSearchRequest
import org.springframework.data.domain.Page


interface AuditPersistence {

    fun saveAudit(audit: Audit): String

    fun getAudit(searchRequest: AuditSearchRequest): Page<Audit>

}
