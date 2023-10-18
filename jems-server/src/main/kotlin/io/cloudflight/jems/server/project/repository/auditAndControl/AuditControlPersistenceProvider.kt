package io.cloudflight.jems.server.project.repository.auditAndControl

import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class AuditControlPersistenceProvider(
    private val auditControlRepository: AuditControlRepository,
): AuditControlPersistence {

    @Transactional
    override fun saveAuditControl(auditControl: ProjectAuditControl): ProjectAuditControl {
        return auditControlRepository.save(auditControl.toEntity()).toModel()
    }


    @Transactional(readOnly = true)
    override fun getByIdAndProjectId(auditControlId: Long, projectId: Long): ProjectAuditControl =
        auditControlRepository.getByIdAndProjectId(
            auditControlId = auditControlId,
            projectId = projectId
        ).toModel()

    @Transactional(readOnly = true)
    override fun findAllProjectAudits(projectId: Long, pageable: Pageable): Page<ProjectAuditControl> {
       return auditControlRepository.findAllByProjectId(projectId, pageable).map { it.toModel() }
    }

    @Transactional
    override fun updateProjectAuditStatus(projectId: Long, auditControlId: Long, auditStatus: AuditStatus): ProjectAuditControl {
        return auditControlRepository.getByIdAndProjectId(auditControlId = auditControlId, projectId = projectId).apply {
            status = auditStatus
        }.toModel()
    }

    @Transactional(readOnly = true)
    override fun countAuditsForProject(projectId: Long): Long =
        auditControlRepository.countAllByProjectId(projectId)

    @Transactional(readOnly = true)
    override fun existsByIdAndProjectId(auditControlId: Long, projectId: Long): Boolean =
        auditControlRepository.existsByIdAndProjectId(auditControlId, projectId)
}
