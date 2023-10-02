package io.cloudflight.jems.server.project.repository.auditAndControl

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuditControlRepository: JpaRepository<AuditControlEntity, Long> {

    fun findAllByProjectId(projectId: Long): List<AuditControlEntity>


    fun findByIdAndProjectId(auditControlId: Long, projectId: Long): AuditControlEntity

    fun countAllByProjectId(projectId: Long): Long
}