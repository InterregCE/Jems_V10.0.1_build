package io.cloudflight.jems.server.project.repository.auditAndControl

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
        return auditControlRepository.save(auditControl.toEntity(project)).toModel(totalCorrections = BigDecimal.ZERO)
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

        return entity.toModel(totalCorrections = getTotalCorrectionsAmount(auditControlId))
    }

    @Transactional(readOnly = true)
    override fun getById(auditControlId: Long): AuditControl =
        auditControlRepository.getById(auditControlId)
            .toModel(totalCorrections = getTotalCorrectionsAmount(auditControlId))

    @Transactional(readOnly = true)
    override fun findAllProjectAudits(projectId: Long, pageable: Pageable): Page<AuditControl> {
        val result = auditControlRepository.findAllByProjectId(projectId, pageable)
            .map { it.toModel(totalCorrections = BigDecimal.ZERO /* temporary */) }

        val auditControlIdsUsed = result.content.mapTo(HashSet()) { it.id }
        val totalsById = getTotalCorrectionsAmount(auditControlIdsUsed)

        result.onEach {
            it.totalCorrectionsAmount = totalsById[it.id] ?: BigDecimal.ZERO
        }

        return result
    }

    @Transactional
    override fun updateAuditControlStatus(auditControlId: Long, status: AuditControlStatus): AuditControl {
        val entity = auditControlRepository.findById(auditControlId).get()
        entity.status = status
        return entity.toModel(totalCorrections = getTotalCorrectionsAmount(auditControlId))
    }

    @Transactional(readOnly = true)
    override fun countAuditsForProject(projectId: Long): Int =
        auditControlRepository.countAllByProjectId(projectId).toInt()

    private fun getTotalCorrectionsAmount(auditControlId: Long): BigDecimal =
        getTotalCorrectionsAmount(setOf(auditControlId))[auditControlId] ?: BigDecimal.ZERO

    private fun getTotalCorrectionsAmount(auditControlIds: Set<Long>): Map<Long, BigDecimal> {
        val correctionFinance = QAuditControlCorrectionFinanceEntity.auditControlCorrectionFinanceEntity

        return jpaQueryFactory
            .select(
                correctionFinance.correction.auditControl.id,
                correctionFinance.fundAmount
                    .add(correctionFinance.publicContribution)
                    .add(correctionFinance.autoPublicContribution)
                    .add(correctionFinance.privateContribution)
                    .sum(),
            )
            .from(correctionFinance)
            .where(correctionFinance.correction.auditControl.id.`in`(auditControlIds))
            .groupBy(correctionFinance.correction.auditControl.id)
            .fetch().associate { Pair(
                it.get(0, Long::class.java)!!,
                it.get(1, BigDecimal::class.java) ?: BigDecimal.ZERO,
            ) }
    }

}
