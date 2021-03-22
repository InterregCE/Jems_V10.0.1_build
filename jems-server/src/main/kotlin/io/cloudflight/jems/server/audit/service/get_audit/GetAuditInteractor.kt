package io.cloudflight.jems.server.audit.service.get_audit

import io.cloudflight.jems.server.audit.model.Audit
import io.cloudflight.jems.server.audit.model.AuditSearchRequest
import org.springframework.data.domain.Page

interface GetAuditInteractor {

    fun getAudit(searchRequest: AuditSearchRequest): Page<Audit>

}
