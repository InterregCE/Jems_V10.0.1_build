package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityTranslEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageTranslEntity
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.toEntity
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class ProjectReportPersistenceProvider(
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository,
    private val legalStatusRepository: ProgrammeLegalStatusRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val workPlanRepository: ProjectPartnerReportWorkPackageRepository,
    private val workPlanActivityRepository: ProjectPartnerReportWorkPackageActivityRepository,
    private val workPlanActivityDeliverableRepository: ProjectPartnerReportWorkPackageActivityDeliverableRepository,
    private val workPlanOutputRepository: ProjectPartnerReportWorkPackageOutputRepository,
) : ProjectReportPersistence {

    @Transactional
    override fun createPartnerReport(report: ProjectPartnerReportCreate): ProjectPartnerReportSummary {
        val reportEntity = persistReport(report)
        persistCoFinancingToReport(report.identification.coFinancing, report = reportEntity)
        persistWorkPlanToReport(report.workPackages, report = reportEntity)
        return reportEntity.toModelSummary()
    }

    private fun persistReport(report: ProjectPartnerReportCreate): ProjectPartnerReportEntity =
        partnerReportRepository.save(
            report.toEntity(
                legalStatus = report.identification.legalStatusId?.let { legalStatusRepository.getById(it) }
            )
        )

    private fun persistCoFinancingToReport(
        coFinancing: List<ProjectPartnerCoFinancing>,
        report: ProjectPartnerReportEntity,
    ) {
        partnerReportCoFinancingRepository.saveAll(
            coFinancing.toEntity(
                reportEntity = report,
                programmeFundResolver = { programmeFundRepository.getById(it) },
            )
        )
    }

    private fun persistWorkPlanToReport(
        workPackages: List<CreateProjectPartnerReportWorkPackage>,
        report: ProjectPartnerReportEntity,
    ) {
        workPackages.forEach { wp ->
            // save WP
            val wpEntity = workPlanRepository.save(wp.toEntity(report))
            // save WP activities
            wp.activities.forEach { activity ->
                val activityEntity = workPlanActivityRepository.save(activity.toEntity(wpEntity))
                // save nested WP activity deliverables
                workPlanActivityDeliverableRepository.saveAll(activity.deliverables.toEntity(activityEntity))
            }
            // save WP outputs
            workPlanOutputRepository.saveAll(wp.outputs.toEntity(wpEntity))
        }
    }

    @Transactional
    override fun submitReportById(partnerId: Long, reportId: Long, submissionTime: ZonedDateTime): ProjectPartnerReportSubmissionSummary =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)
            .apply {
                status = ReportStatus.Submitted
                firstSubmission = submissionTime
            }.toSubmissionSummary()

    @Transactional(readOnly = true)
    override fun getPartnerReportStatusById(partnerId: Long, reportId: Long): ReportStatus =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId).status

    @Transactional(readOnly = true)
    override fun getPartnerReportById(partnerId: Long, reportId: Long): ProjectPartnerReport =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId).toModel(
            coFinancing = partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId)
        )

    @Transactional(readOnly = true)
    override fun listPartnerReports(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportSummary> =
        partnerReportRepository.findAllByPartnerId(partnerId = partnerId, pageable = pageable).map { it.toModelSummary() }

    @Transactional(readOnly = true)
    override fun getCurrentLatestReportNumberForPartner(partnerId: Long): Int =
        partnerReportRepository.getMaxNumberForPartner(partnerId = partnerId)

    @Transactional(readOnly = true)
    override fun getPartnerReportWorkPlanById(partnerId: Long, reportId: Long): List<ProjectPartnerReportWorkPackage> {
        val reportEntity = partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)

        val activitiesByWorkPackage = workPlanActivityRepository
            .findAllByWorkPackageEntityReportEntityOrderByNumber(reportEntity)
            .groupBy { it.workPackageEntity }
        val deliverablesByActivity = workPlanActivityDeliverableRepository
            .findAllByActivityEntityWorkPackageEntityReportEntityOrderByNumber(reportEntity)
            .groupBy { it.activityEntity }
        val outputsByWorkPackage = workPlanOutputRepository
            .findAllByWorkPackageEntityReportEntityOrderByNumber(reportEntity)
            .groupBy { it.workPackageEntity }

        return workPlanRepository.findAllByReportEntityOrderByNumber(reportEntity).toModel(
            retrieveActivities = { wp -> activitiesByWorkPackage[wp] ?: emptyList() },
            retrieveDeliverables = { activity -> deliverablesByActivity[activity] ?: emptyList() },
            retrieveOutputs = { wp -> outputsByWorkPackage[wp] ?: emptyList() },
        )
    }

    @Transactional
    override fun updatePartnerReportWorkPackage(workPackageId: Long, translations: Set<InputTranslation>) {
        val toBeUpdatedLanguages = translations.associateBy({ it.language }, { it.translation })

        workPlanRepository.findById(workPackageId).get().apply {
            addMissingLanguagesIfNeeded(languages = toBeUpdatedLanguages.keys)

            // update values of all languages
            translatedValues.forEach {
                it.description = toBeUpdatedLanguages[it.language()]
            }
        }
    }

    private fun ProjectPartnerReportWorkPackageEntity.addMissingLanguagesIfNeeded(languages: Set<SystemLanguage>) {
        val existingLanguages = translatedValues.mapTo(HashSet()) { it.translationId.language }
        languages.filter { !existingLanguages.contains(it) }.forEach { language ->
            translatedValues.add(
                ProjectPartnerReportWorkPackageTranslEntity(
                    translationId = TranslationId(this, language = language),
                    description = null,
                )
            )
        }
    }

    @Transactional
    override fun updatePartnerReportWorkPackageActivity(activityId: Long, translations: Set<InputTranslation>) {
        val toBeUpdatedLanguages = translations.associateBy({ it.language }, { it.translation })

        workPlanActivityRepository.findById(activityId).get().apply {
            addMissingLanguagesIfNeeded(languages = toBeUpdatedLanguages.keys)

            // update values of all languages
            translatedValues.forEach {
                it.description = toBeUpdatedLanguages[it.language()]
            }
        }
    }

    private fun ProjectPartnerReportWorkPackageActivityEntity.addMissingLanguagesIfNeeded(languages: Set<SystemLanguage>) {
        val existingLanguages = translatedValues.mapTo(HashSet()) { it.translationId.language }
        languages.filter { !existingLanguages.contains(it) }.forEach { language ->
            translatedValues.add(
                ProjectPartnerReportWorkPackageActivityTranslEntity(
                    translationId = TranslationId(this, language = language),
                    title = null,
                    description = null,
                )
            )
        }
    }

    @Transactional
    override fun updatePartnerReportWorkPackageDeliverable(
        deliverableId: Long,
        contribution: Boolean?,
        evidence: Boolean?,
    ) {
        workPlanActivityDeliverableRepository.findById(deliverableId).get().apply {
            this.contribution = contribution
            this.evidence = evidence
        }
    }

    @Transactional
    override fun updatePartnerReportWorkPackageOutput(outputId: Long, contribution: Boolean?, evidence: Boolean?) {
        workPlanOutputRepository.findById(outputId).get().apply {
            this.contribution = contribution
            this.evidence = evidence
        }
    }

}
