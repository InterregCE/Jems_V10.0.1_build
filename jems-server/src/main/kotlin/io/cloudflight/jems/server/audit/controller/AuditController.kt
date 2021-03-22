package io.cloudflight.jems.server.audit.controller

import io.cloudflight.jems.api.audit.AuditApi
import io.cloudflight.jems.api.audit.dto.AuditDTO
import io.cloudflight.jems.api.audit.dto.AuditSearchRequestDTO
import io.cloudflight.jems.server.audit.service.get_audit.GetAuditInteractor
import io.cloudflight.jems.server.config.AUDIT_ENABLED
import io.cloudflight.jems.server.config.AUDIT_PROPERTY_PREFIX
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
@ConditionalOnProperty(prefix = AUDIT_PROPERTY_PREFIX, name = [AUDIT_ENABLED], havingValue = "true")
class AuditController(
    private val getAudit: GetAuditInteractor,
) : AuditApi {

    override fun getAudits(pageable: Pageable, searchRequest: AuditSearchRequestDTO?): Page<AuditDTO> =
        getAudit.getAudit(searchRequest.toModel(pageable)).toDto()

}
