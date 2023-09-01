package io.cloudflight.jems.server.project.repository.report.project.verification

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportVerificationClarificationEntity
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.repository.report.project.base.toEntity
import io.cloudflight.jems.server.project.repository.report.project.base.toModel
import io.cloudflight.jems.server.project.repository.report.project.base.toVerificationConclusion
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationClarification
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationConclusion
import io.cloudflight.jems.server.project.service.report.project.verification.ProjectReportVerificationPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportVerificationPersistenceProvider(
    private val projectReportRepository: ProjectReportRepository,
    private val projectReportVerificationClarificationRepository: ProjectReportVerificationClarificationRepository
): ProjectReportVerificationPersistence {

    @Transactional(readOnly = true)
    override fun getVerificationConclusion(
        projectId: Long,
        reportId: Long
    ): ProjectReportVerificationConclusion =
        projectReportRepository.getByIdAndProjectId(id = reportId, projectId = projectId).toVerificationConclusion()

    @Transactional
    override fun updateVerificationConclusion(
        projectId: Long,
        reportId: Long,
        projectReportVerificationConclusion: ProjectReportVerificationConclusion
    ): ProjectReportVerificationConclusion {
        return projectReportRepository.getByIdAndProjectId(id = reportId, projectId = projectId).apply {
            this.verificationDate = projectReportVerificationConclusion.startDate
            this.verificationConclusionJs = projectReportVerificationConclusion.conclusionJS
            this.verificationConclusionMa = projectReportVerificationConclusion.conclusionMA
            this.verificationFollowup = projectReportVerificationConclusion.verificationFollowUp
        }.toVerificationConclusion()
    }

    @Transactional(readOnly = true)
    override fun getVerificationClarifications(reportId: Long): List<ProjectReportVerificationClarification> {
        return projectReportVerificationClarificationRepository.findByProjectReportIdOrderByNumber(reportId).map { it.toModel() }
    }

    @Transactional
    override fun updateVerificationClarifications(
        projectId: Long,
        reportId: Long,
        clarifications: List<ProjectReportVerificationClarification>,
    ): List<ProjectReportVerificationClarification> {
        val existing = projectReportVerificationClarificationRepository.findByProjectReportIdOrderByNumber(reportId)
        val report = projectReportRepository.getByIdAndProjectId(reportId, projectId)

        projectReportVerificationClarificationRepository.deleteAllByIdInBatch(clarifications.getClarificationsToDelete(existing))

        val newData = clarifications.filter { it.id == 0L }
        existing.updateWith(clarifications.minus(newData))
        newData.save(report)

        return projectReportVerificationClarificationRepository.findByProjectReportIdOrderByNumber(reportId).map { it.toModel() }
    }

    private fun List<ProjectReportVerificationClarification>.getClarificationsToDelete(
        existingClarifications: List<ProjectReportVerificationClarificationEntity>
    ): List<Long> {
        val existingClarificationIds = existingClarifications.map { it.id }
        val updatedClarificationsIds = this.map { it.id }
        return existingClarificationIds.filter { it !in updatedClarificationsIds }
    }

    private fun List<ProjectReportVerificationClarificationEntity>.updateWith(newData: List<ProjectReportVerificationClarification>) {
        val existingById = this.associateBy { it.id }
        newData.forEach { updatedClarification ->
            existingById[updatedClarification.id]?.apply {
                this.number = updatedClarification.number
                this.requestDate = updatedClarification.requestDate
                this.answerDate = updatedClarification.answerDate
                this.comment = updatedClarification.comment
            }

        }
    }

    private fun List<ProjectReportVerificationClarification>.save(projectReport: ProjectReportEntity) =
        projectReportVerificationClarificationRepository.saveAll(this.map { it.toEntity(projectReport) })
}
