package io.cloudflight.jems.server.project.repository.report.identification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupTranslEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTranslEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.partner.identification.UpdateProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ProjectPartnerControlReportChange
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportFileFormat
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportType
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.Optional
import kotlin.collections.HashSet

@Repository
class ProjectReportIdentificationPersistenceProvider(
    private val reportRepository: ProjectPartnerReportRepository,
    private val identificationRepository: ProjectPartnerReportIdentificationRepository,
    private val identificationTargetGroupRepository: ProjectPartnerReportIdentificationTargetGroupRepository,
    private val reportBudgetPerPeriodRepository: ProjectPartnerReportBudgetPerPeriodRepository,
) : ProjectReportIdentificationPersistence {

    @Transactional(readOnly = true)
    override fun getPartnerReportIdentification(partnerId: Long, reportId: Long): Optional<ProjectPartnerReportIdentification> =
        identificationRepository.findByReportEntityIdAndReportEntityPartnerId(
            reportId = reportId,
            partnerId = partnerId,
        ).map { it.toModel(
            targetGroups = identificationTargetGroupRepository.findAllByReportIdentificationEntityOrderBySortNumber(
                reportIdentificationEntity = it,
            ),
            periodResolver = { periodNumber -> periodNumber?.let {
                reportBudgetPerPeriodRepository.findByIdReportIdAndIdPeriodNumber(reportId = reportId, periodNumber = periodNumber)
            } },
        ) }

    @Transactional
    override fun updatePartnerReportIdentification(
        partnerId: Long,
        reportId: Long,
        data: UpdateProjectPartnerReportIdentification,
    ): ProjectPartnerReportIdentification {
        val entity = identificationRepository.findByReportEntityIdAndReportEntityPartnerId(
            reportId = reportId,
            partnerId = partnerId,
        ).orElse(null) ?: createIdentificationEntityFrom(
            entity = reportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)
        )

        val targetGroupsExisting = identificationTargetGroupRepository
            .findAllByReportIdentificationEntityOrderBySortNumber(entity)

        updateBaseData(entity = entity, data)
        updateTranslations(entity = entity, data)
        updateTargetGroups(targetGroupsExisting, data)

        return entity.toModel(
            targetGroups = targetGroupsExisting,
            periodResolver = { periodNumber -> periodNumber?.let {
                reportBudgetPerPeriodRepository.findByIdReportIdAndIdPeriodNumber(reportId = reportId, periodNumber = periodNumber)
            } },
        )
    }

    @Transactional(readOnly = true)
    override fun getAvailablePeriods(partnerId: Long, reportId: Long): List<ProjectPartnerReportPeriod> =
        reportBudgetPerPeriodRepository.findAllByIdReportPartnerIdAndIdReportIdOrderByIdPeriodNumber(partnerId = partnerId, reportId)
            .toPeriodModel()

    @Transactional
    override fun updatePartnerControlReportIdentification(
        partnerId: Long,
        reportId: Long,
        data: ProjectPartnerControlReportChange,
    ): ProjectPartnerReportIdentification {
        val entity = identificationRepository.findByReportEntityIdAndReportEntityPartnerId(
            reportId = reportId,
            partnerId = partnerId,
        ).orElseThrow { ResourceNotFoundException("controlReport") }

        entity.formatOriginals = data.controllerFormats.contains(ReportFileFormat.Originals)
        entity.formatCopy = data.controllerFormats.contains(ReportFileFormat.Copy)
        entity.formatElectronic = data.controllerFormats.contains(ReportFileFormat.Electronic)
        entity.type = data.type

        return entity.toModel(
            targetGroups = identificationTargetGroupRepository.findAllByReportIdentificationEntityOrderBySortNumber(entity),
            periodResolver = { periodNumber -> periodNumber?.let {
                reportBudgetPerPeriodRepository.findByIdReportIdAndIdPeriodNumber(reportId = reportId, periodNumber = periodNumber)
            } },
        )
    }

    private fun updateBaseData(
        entity: ProjectPartnerReportIdentificationEntity,
        data: UpdateProjectPartnerReportIdentification
    ) {
        entity.startDate = data.startDate
        entity.endDate = data.endDate
        entity.periodNumber = data.period
        entity.nextReportForecast = data.nextReportForecast
    }

    private fun updateTranslations(
        entity: ProjectPartnerReportIdentificationEntity,
        data: UpdateProjectPartnerReportIdentification
    ) {
        val summaryAsMap = data.getSummaryAsMap()
        val problemsAsMap = data.getProblemsAndDeviationsAsMap()
        val spendingAsMap = data.getSpendingDeviationsAsMap()

        entity.addMissingLanguagesIfNeeded(languages = summaryAsMap.keys union problemsAsMap.keys union spendingAsMap.keys)
        entity.translatedValues.forEach {
            it.summary = summaryAsMap[it.language()]
            it.problemsAndDeviations = problemsAsMap[it.language()]
            it.spendingDeviations = spendingAsMap[it.language()]
        }
    }

    private fun updateTargetGroups(
        targetGroupsExisting: List<ProjectPartnerReportIdentificationTargetGroupEntity>,
        data: UpdateProjectPartnerReportIdentification
    ) {
        targetGroupsExisting.forEachIndexed { index, targetGroupEntity ->
            val translationsByLanguage = data.targetGroups
                .getOrElse(index) { emptySet() }
                .associateBy({ it.language }, { it.translation })

            targetGroupEntity.addMissingLanguagesIfNeeded(languages = translationsByLanguage.keys)
            targetGroupEntity.translatedValues.forEach {
                it.description = translationsByLanguage[it.language()]
            }
        }
    }

    private fun ProjectPartnerReportIdentificationEntity.addMissingLanguagesIfNeeded(languages: Set<SystemLanguage>) {
        val existingLanguages = translatedValues.mapTo(HashSet()) { it.translationId.language }
        languages.filter { !existingLanguages.contains(it) }.forEach { language ->
            translatedValues.add(
                ProjectPartnerReportIdentificationTranslEntity(
                    translationId = TranslationId(this, language = language),
                    summary = null,
                    problemsAndDeviations = null,
                    spendingDeviations = null,
                )
            )
        }
    }

    private fun ProjectPartnerReportIdentificationTargetGroupEntity.addMissingLanguagesIfNeeded(languages: Set<SystemLanguage>) {
        val existingLanguages = translatedValues.mapTo(HashSet()) { it.translationId.language }
        languages.filter { !existingLanguages.contains(it) }.forEach { language ->
            translatedValues.add(
                ProjectPartnerReportIdentificationTargetGroupTranslEntity(
                    translationId = TranslationId(this, language = language),
                    specification = null,
                    description = null,
                )
            )
        }
    }

    private fun createIdentificationEntityFrom(entity: ProjectPartnerReportEntity) =
        identificationRepository.save(
            ProjectPartnerReportIdentificationEntity(
                reportEntity = entity,
                startDate = null,
                endDate = null,
                periodNumber = null,
                nextReportForecast = BigDecimal.ZERO,
                formatOriginals = false,
                formatCopy = false,
                formatElectronic = false,
                type = ReportType.PartnerReport,
                translatedValues = mutableSetOf(),
            )
        )
}
