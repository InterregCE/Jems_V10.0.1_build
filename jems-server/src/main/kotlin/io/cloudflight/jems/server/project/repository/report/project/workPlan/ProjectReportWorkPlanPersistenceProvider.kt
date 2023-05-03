package io.cloudflight.jems.server.project.repository.report.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.inLang
import io.cloudflight.jems.server.common.entity.updateWith
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageInvestmentTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageOutputTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageTranslEntity
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOnlyUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputLineOverview
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportWorkPlanPersistenceProvider(
    private val reportRepository: ProjectReportRepository,
    private val workPlanRepository: ProjectReportWorkPackageRepository,
    private val workPlanActivityRepository: ProjectReportWorkPackageActivityRepository,
    private val workPlanActivityDeliverableRepository: ProjectReportWorkPackageActivityDeliverableRepository,
    private val workPlanOutputRepository: ProjectReportWorkPackageOutputRepository,
    private val workPlanInvestmentRepository: ProjectReportWorkPackageInvestmentRepository,
    private val projectPersistence: ProjectPersistence,
    private val fileService: JemsProjectFileService
) : ProjectReportWorkPlanPersistence {

    @Transactional(readOnly = true)
    override fun getReportWorkPlanById(projectId: Long, reportId: Long): List<ProjectReportWorkPackage> {
        val reportEntity = reportRepository.getByIdAndProjectId(id = reportId, projectId = projectId)

        val activitiesByWorkPackage = workPlanActivityRepository
            .findAllByWorkPackageEntityReportEntityOrderByNumber(reportEntity)
            .groupBy { it.workPackageEntity }
        val deliverablesByActivity = workPlanActivityDeliverableRepository
            .findAllByActivityEntityWorkPackageEntityReportEntityOrderByNumber(reportEntity)
            .groupBy { it.activityEntity }
        val outputsByWorkPackage = workPlanOutputRepository
            .findAllByWorkPackageEntityReportEntityOrderByNumber(reportEntity)
            .groupBy { it.workPackageEntity }
        val investmentsByWorkPackage = workPlanInvestmentRepository
            .findAllByWorkPackageEntityReportEntityOrderByNumber(reportEntity)
            .groupBy { it.workPackageEntity }

        val periods = projectPersistence.getProjectPeriods(projectId, reportEntity.applicationFormVersion)
            .associateBy { it.number }

        return workPlanRepository.findAllByReportEntityOrderByNumber(reportEntity).toModel(
            periods = periods,
            retrieveActivities = { wp -> activitiesByWorkPackage[wp] ?: emptyList() },
            retrieveDeliverables = { activity -> deliverablesByActivity[activity] ?: emptyList() },
            retrieveOutputs = { wp -> outputsByWorkPackage[wp] ?: emptyList() },
            retrieveInvestments = { wp -> investmentsByWorkPackage[wp] ?: emptyList() },
        )
    }

    @Transactional(readOnly = true)
    override fun getReportWorkPackageOutputsById(projectId: Long, reportId: Long): List<ProjectReportOutputLineOverview> =
        workPlanOutputRepository.findAllByWorkPackageEntityReportEntityOrderByNumber(
            reportEntity = reportRepository.getByIdAndProjectId(id = reportId, projectId = projectId)
        ).toOverviewModel()

    @Transactional(readOnly = true)
    override fun existsByActivityId(projectId: Long, reportId: Long, workPackageId: Long, activityId: Long) =
        workPlanActivityRepository.existsByActivityId(activityId = activityId,
            workPackageId = workPackageId, reportId = reportId, projectId = projectId)

    @Transactional(readOnly = true)
    override fun existsByDeliverableId(projectId: Long, reportId: Long, workPackageId: Long, activityId: Long, deliverableId: Long) =
        workPlanActivityDeliverableRepository
            .existsByDeliverableId(deliverableId = deliverableId, activityId = activityId,
                workPackageId = workPackageId, reportId = reportId, projectId = projectId)

    @Transactional(readOnly = true)
    override fun existsByOutputId(projectId: Long, reportId: Long, workPackageId: Long, outputId: Long) =
        workPlanOutputRepository.existsByOutputId(outputId = outputId,
            workPackageId = workPackageId, reportId = reportId, projectId = projectId)

    @Transactional
    override fun updateReportWorkPackage(workPackageId: Long, data: ProjectReportWorkPackageOnlyUpdate) {
        workPlanRepository.findById(workPackageId).get().apply {
            this.specificStatus = data.specificStatus
            this.communicationStatus = data.communicationStatus
            this.completed = data.completed

            translatedValues.updateWith(
                entitySupplier = { lang -> ProjectReportWorkPackageTranslEntity(TranslationId(this, lang), "", "", "", "", "") },
                allTranslations = listOf(data.specificExplanation, data.communicationExplanation, data.description),
                { e -> e.specificExplanation = data.specificExplanation.inLang(e.language()) },
                { e -> e.communicationExplanation = data.communicationExplanation.inLang(e.language()) },
                { e -> e.description = data.description.inLang(e.language()) },
            )
        }
    }

    @Transactional
    override fun updateReportWorkPackageActivity(activityId: Long, status: ProjectReportWorkPlanStatus?, progress: Set<InputTranslation>) {
        workPlanActivityRepository.findById(activityId).get().apply {
            this.status = status
            translatedValues.updateWith(
                entitySupplier = { lang -> ProjectReportWorkPackageActivityTranslEntity(TranslationId(this, lang), "", "") },
                allTranslations = listOf(progress),
                { e -> e.progress = progress.inLang(e.language()) },
            )
        }
    }

    @Transactional
    override fun updateReportWorkPackageDeliverable(deliverableId: Long, currentReport: BigDecimal, progress: Set<InputTranslation>) {
        workPlanActivityDeliverableRepository.findById(deliverableId).get().apply {
            this.currentReport = currentReport
            this.translatedValues.updateWith(
                entitySupplier = { lang -> ProjectReportWorkPackageActivityDeliverableTranslEntity(TranslationId(this, lang), "", "") },
                allTranslations = listOf(progress),
                { e -> e.progress = progress.inLang(e.language()) },
            )
        }
    }

    @Transactional
    override fun updateReportWorkPackageOutput(outputId: Long, currentReport: BigDecimal, progress: Set<InputTranslation>) {
        workPlanOutputRepository.findById(outputId).get().apply {
            this.currentReport = currentReport
            this.translatedValues.updateWith(
                entitySupplier = { lang -> ProjectReportWorkPackageOutputTranslEntity(TranslationId(this, lang), "", "") },
                allTranslations = listOf(progress),
                { e -> e.progress = progress.inLang(e.language()) },
            )
        }
    }

    @Transactional
    override fun updateReportWorkPackageInvestment(investmentId: Long, progress: Set<InputTranslation>) {
        workPlanInvestmentRepository.findById(investmentId).get().apply {
            this.translatedValues.updateWith(
                entitySupplier = { lang ->
                    ProjectReportWorkPackageInvestmentTranslEntity(TranslationId(this, lang)) },
                allTranslations = listOf(progress),
                { e -> e.progress = progress.inLang(e.language()) },
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getDeliverableCumulative(reportIds: Set<Long>): Map<Int, Map<Int, Map<Int, BigDecimal>>> =
        workPlanActivityDeliverableRepository.getCumulativeValues(reportIds)
            .groupBy { it.wpNumber }
            .mapValues { it.value
                .groupBy { it.activityNumber }
                .mapValues { it.value
                    .associateBy({ it.deliverableNumber }, { it.cumulative })
                }
            }

    @Transactional(readOnly = true)
    override fun getOutputCumulative(reportIds: Set<Long>): Map<Int, Map<Int, BigDecimal>> =
        workPlanOutputRepository.getCumulativeValues(reportIds)
            .groupBy { it.wpNumber }
            .mapValues { it.value
                .associateBy({ it.outputNumber }, { it.cumulative })
            }

    @Transactional
    override fun deleteWorkPlan(projectId: Long, reportId: Long) {
        val reportEntity = reportRepository.getByIdAndProjectId(id = reportId, projectId = projectId)

        workPlanActivityRepository
            .findAllByWorkPackageEntityReportEntityOrderByNumber(reportEntity)
            .forEach { it.attachment.deleteIfPresent() }
        workPlanActivityDeliverableRepository
            .findAllByActivityEntityWorkPackageEntityReportEntityOrderByNumber(reportEntity)
            .forEach { it.attachment.deleteIfPresent() }
        workPlanOutputRepository
            .findAllByWorkPackageEntityReportEntityOrderByNumber(reportEntity)
            .forEach { it.attachment.deleteIfPresent() }

        workPlanRepository.deleteAllByReportEntity(reportEntity)
    }

    private fun JemsFileMetadataEntity?.deleteIfPresent() {
        if (this != null) {
            fileService.delete(this)
        }
    }

}
