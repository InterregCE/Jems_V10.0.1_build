package io.cloudflight.jems.server.project.repository.auditAndControl

import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class AuditControlPersistenceProvider(
    private val projectRepository: ProjectRepository,
    private val auditControlRepository: AuditControlRepository,
): AuditControlPersistence {

    @Transactional(readOnly = true)
    override fun getProjectIdForAuditControl(auditControlId: Long): Long =
        auditControlRepository.getById(auditControlId).project.id

    @Transactional
    override fun createControl(projectId: Long, auditControl: AuditControlCreate): AuditControl {
        val project = projectRepository.getById(projectId)
        return auditControlRepository.save(auditControl.toEntity(project)).toModel()
    }

    @Transactional
    override fun updateControl(auditControlId: Long, auditControl: AuditControlUpdate): AuditControl {
        val entity = auditControlRepository.getById(auditControlId)

        entity.controllingBody = auditControl.controllingBody
        entity.controlType = auditControl.controlType
        entity.startDate = auditControl.startDate
        entity.endDate = auditControl.endDate
        entity.finalReportDate = auditControl.finalReportDate
        entity.totalControlledAmount = auditControl.totalControlledAmount
        entity.comment = auditControl.comment

        return entity.toModel()
    }

    @Transactional(readOnly = true)
    override fun getById(auditControlId: Long): AuditControl =
        auditControlRepository.getById(auditControlId).toModel()

    @Transactional(readOnly = true)
    override fun findAllProjectAudits(projectId: Long, pageable: Pageable): Page<AuditControl> {
       return auditControlRepository.findAllByProjectId(projectId, pageable).map { it.toModel() }
    }

    @Transactional
    override fun updateAuditControlStatus(auditControlId: Long, status: AuditControlStatus): AuditControl {
        val entity = auditControlRepository.findById(auditControlId).get()
        entity.status = status
        return entity.toModel()
    }

    @Transactional(readOnly = true)
    override fun countAuditsForProject(projectId: Long): Int =
        auditControlRepository.countAllByProjectId(projectId).toInt()

}
