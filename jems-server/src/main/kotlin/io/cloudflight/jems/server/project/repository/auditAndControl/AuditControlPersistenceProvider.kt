package io.cloudflight.jems.server.project.repository.auditAndControl

import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionFinanceEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal


@Repository
class AuditControlPersistenceProvider(
    private val projectRepository: ProjectRepository,
    private val auditControlRepository: AuditControlRepository,
    private val jpaQueryFactory: JPAQueryFactory,
) : AuditControlPersistence {

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

        return entity.toModel(
            totalCorrectionsResolver = { id -> getTotalCorrectionsAmount(id) }
        )
    }

    @Transactional(readOnly = true)
    override fun getById(auditControlId: Long): AuditControl =
        auditControlRepository.getById(auditControlId).toModel(
            totalCorrectionsResolver = { id -> getTotalCorrectionsAmount(id) }
        )

    @Transactional(readOnly = true)
    override fun findAllProjectAudits(projectId: Long, pageable: Pageable): Page<AuditControl> {
        return auditControlRepository.findAllByProjectId(projectId, pageable).map {
            it.toModel(totalCorrectionsResolver = { id -> getTotalCorrectionsAmount(id) })
        }
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

    private fun getTotalCorrectionsAmount(auditControlId: Long): BigDecimal {
        val financeSpec = QAuditControlCorrectionFinanceEntity.auditControlCorrectionFinanceEntity
        val unsignedTotalExpr = financeSpec.fundAmount
            .add(financeSpec.publicContribution)
            .add(financeSpec.autoPublicContribution)
            .add(financeSpec.privateContribution)
        val signedTotalExpr = CaseBuilder().`when`(financeSpec.deduction.isTrue)
            .then(unsignedTotalExpr.negate())
            .otherwise(unsignedTotalExpr)

        return jpaQueryFactory
            .select(signedTotalExpr.sum())
            .from(financeSpec)
            .where(financeSpec.correction.auditControl.id.eq(auditControlId))
            .groupBy(financeSpec.correction.auditControl.id)
            .fetchOne()
            ?: BigDecimal.ZERO

    }

}
