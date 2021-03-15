package io.cloudflight.jems.server.audit.service.get_audit

import io.cloudflight.jems.server.audit.model.Audit
import io.cloudflight.jems.server.audit.model.AuditSearchRequest
import io.cloudflight.jems.server.audit.service.AuditPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.config.AUDIT_ENABLED
import io.cloudflight.jems.server.config.AUDIT_PROPERTY_PREFIX
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(prefix = AUDIT_PROPERTY_PREFIX, name = [AUDIT_ENABLED], havingValue = "true")
class GetAudit(private val auditPersistence: AuditPersistence) : GetAuditInteractor {

    @CanUpdateProgrammeSetup
    @ExceptionWrapper(GetAuditException::class)
    override fun getAudit(searchRequest: AuditSearchRequest): Page<Audit> =
        auditPersistence.getAudit(searchRequest)

}
