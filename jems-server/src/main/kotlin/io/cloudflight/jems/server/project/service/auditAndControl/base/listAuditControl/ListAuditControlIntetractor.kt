package io.cloudflight.jems.server.project.service.auditAndControl.base.listAuditControl

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListAuditControlIntetractor {

    fun listForProject(projectId: Long, pageable: Pageable): Page<AuditControl>

}
