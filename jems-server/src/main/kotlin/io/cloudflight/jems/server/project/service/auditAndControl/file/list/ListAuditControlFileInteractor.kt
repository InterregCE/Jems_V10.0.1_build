package io.cloudflight.jems.server.project.service.auditAndControl.file.list

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListAuditControlFileInteractor {

    fun list(projectId: Long, auditControlId: Long, pageable: Pageable): Page<JemsFile>
}
