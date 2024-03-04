package io.cloudflight.jems.server.project.repository.auditAndControl

import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionFinanceEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.tmpModel.AuditControlCorrectionRelatedData
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

    private val emptyCorrectionsRelatedData = AuditControlCorrectionRelatedData(
        totalCorrections = BigDecimal.ZERO,
        existsOngoing = false,
        existsClosed = false,
    )

    @Transactional(readOnly = true)
    override fun getProjectIdForAuditControl(auditControlId: Long): Long =
        auditControlRepository.getReferenceById(auditControlId).project.id

    @Transactional
    override fun createControl(projectId: Long, auditControl: AuditControlCreate): AuditControl {
        val project = projectRepository.getReferenceById(projectId)
        return auditControlRepository.save(auditControl.toEntity(project))
            .toModel(correctionsRelatedData = emptyCorrectionsRelatedData)
    }

    @Transactional
    override fun updateControl(auditControlId: Long, auditControl: AuditControlUpdate): AuditControl {
        val entity = auditControlRepository.getReferenceById(auditControlId)

        entity.controllingBody = auditControl.controllingBody
        entity.controlType = auditControl.controlType
        entity.startDate = auditControl.startDate
        entity.endDate = auditControl.endDate
        entity.finalReportDate = auditControl.finalReportDate
        entity.totalControlledAmount = auditControl.totalControlledAmount
        entity.comment = auditControl.comment

        return entity.toModel(correctionsRelatedData = getCorrectionsRelatedData(auditControlId))
    }

    @Transactional(readOnly = true)
    override fun getById(auditControlId: Long): AuditControl =
        auditControlRepository.getReferenceById(auditControlId)
            .toModel(correctionsRelatedData = getCorrectionsRelatedData(auditControlId))

    @Transactional(readOnly = true)
    override fun findAllProjectAudits(projectId: Long, pageable: Pageable): Page<AuditControl> {
        val result = auditControlRepository.findAllByProjectId(projectId, pageable)
            .map { it.toModel(correctionsRelatedData = emptyCorrectionsRelatedData /* temporary */) }

        val auditControlIdsUsed = result.content.mapTo(HashSet()) { it.id }
        val dataById = getCorrectionsRelatedData(auditControlIdsUsed)

        result.onEach {
            it.totalCorrectionsAmount = (dataById[it.id] ?: emptyCorrectionsRelatedData).totalCorrections
            it.existsOngoing = (dataById[it.id] ?: emptyCorrectionsRelatedData).existsOngoing
            it.existsClosed = (dataById[it.id] ?: emptyCorrectionsRelatedData).existsClosed
        }

        return result
    }

    @Transactional
    override fun updateAuditControlStatus(auditControlId: Long, status: AuditControlStatus): AuditControl {
        val entity = auditControlRepository.findById(auditControlId).get()
        entity.status = status
        return entity.toModel(correctionsRelatedData = getCorrectionsRelatedData(auditControlId))
    }

    @Transactional(readOnly = true)
    override fun countAuditsForProject(projectId: Long): Int =
        auditControlRepository.countAllByProjectId(projectId).toInt()

    private fun getCorrectionsRelatedData(auditControlId: Long): AuditControlCorrectionRelatedData =
        getCorrectionsRelatedData(setOf(auditControlId))[auditControlId] ?: emptyCorrectionsRelatedData

    private fun getCorrectionsRelatedData(auditControlIds: Set<Long>): Map<Long, AuditControlCorrectionRelatedData> {
        val correctionFinance = QAuditControlCorrectionFinanceEntity.auditControlCorrectionFinanceEntity

        return jpaQueryFactory
            .select(
                correctionFinance.correction.auditControl.id,
                correctionFinance.fundAmount
                    .add(correctionFinance.publicContribution)
                    .add(correctionFinance.autoPublicContribution)
                    .add(correctionFinance.privateContribution)
                    .sum(),
                CaseBuilder().`when`(correctionFinance.correction.status.eq(AuditControlStatus.Ongoing))
                    .then(1).otherwise(0).sum(),
                CaseBuilder().`when`(correctionFinance.correction.status.eq(AuditControlStatus.Closed))
                    .then(1).otherwise(0).sum(),
                correctionFinance.correction.count(),
            )
            .from(correctionFinance)
            .where(correctionFinance.correction.auditControl.id.`in`(auditControlIds))
            .groupBy(correctionFinance.correction.auditControl.id)
            .fetch().associate {
                val correctionsCount = it.get(4, Int::class.java)!!
                val ongoingCount = it.get(2, Int::class.java)!!
                val closedCount = it.get(3, Int::class.java)!!
                return@associate Pair(
                    it.get(0, Long::class.java)!!,
                    AuditControlCorrectionRelatedData(
                        totalCorrections = it.get(1, BigDecimal::class.java) ?: BigDecimal.ZERO,
                        existsOngoing = correctionsCount != closedCount,
                        existsClosed = correctionsCount != ongoingCount,
                    ),
                )
            }
    }

}
