package io.cloudflight.jems.server.plugin.services.auditAndControl

import io.cloudflight.jems.plugin.contract.models.common.file.JemsFileMetadataData
import io.cloudflight.jems.plugin.contract.models.common.paging.Page
import io.cloudflight.jems.plugin.contract.models.common.paging.Pageable
import io.cloudflight.jems.plugin.contract.services.auditAndControl.ProjectAuditControlFileDataProvider
import io.cloudflight.jems.server.common.file.service.toSimpleModelMedataData
import io.cloudflight.jems.server.plugin.services.toJpaPage
import io.cloudflight.jems.server.plugin.services.toPluginPage
import io.cloudflight.jems.server.project.service.auditAndControl.file.list.ListAuditControlFileService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuditAndControlFileDataProviderImpl(
    private val listAuditControlFileService: ListAuditControlFileService
): ProjectAuditControlFileDataProvider {

    @Transactional(readOnly = true)
    override fun list(auditControlId: Long, pageable: Pageable): Page<JemsFileMetadataData> {
        return listAuditControlFileService.list(auditControlId, pageable.toJpaPage())
            .toPluginPage { it.toSimpleModelMedataData() }
    }


}
