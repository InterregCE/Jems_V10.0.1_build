package io.cloudflight.jems.server.project.repository.auditAndControl

import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
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
    override fun findByIdAndProjectId(auditControlId: Long, projectId: Long): ProjectAuditControl =
        auditControlRepository.findByIdAndProjectId(
            auditControlId = auditControlId,
            projectId = projectId
        ).toModel()


    @Transactional(readOnly = true)
    override fun findAllProjectAudits(projectId: Long): List<ProjectAuditControl> {
       return auditControlRepository.findAllByProjectId(projectId).map { it.toModel() }
    }

    @Transactional
    override fun updateProjectAuditStatus(projectId: Long, auditControlId: Long, auditStatus: AuditStatus): ProjectAuditControl {
        return auditControlRepository.findByIdAndProjectId(auditControlId = auditControlId, projectId = projectId).apply {
            status = auditStatus
        }.toModel()
    }

    @Transactional(readOnly = true)
    override fun countAuditsForProject(projectId: Long): Long =
        auditControlRepository.countAllByProjectId(projectId)
}