package io.cloudflight.jems.server.project.service.auditAndControl.file.list

import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListAuditControlFileService(
    private val filePersistence: JemsFilePersistence,
    private val auditControlPersistence: AuditControlPersistence,
) {

    @Transactional(readOnly = true)
    fun list(auditControlId: Long, pageable: Pageable): Page<JemsFile> {
        val projectId = auditControlPersistence.getProjectIdForAuditControl(auditControlId)

        val filePathPrefix = JemsFileType.AuditControl.generatePath(projectId, auditControlId)
        return filePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = filePathPrefix,
            filterSubtypes = setOf(JemsFileType.AuditControl),
            filterUserIds = emptySet(),
        )
    }

}
