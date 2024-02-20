package io.cloudflight.jems.server.project.repository.report.project.identification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileId
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupTranslEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfileReportedValues
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.Optional
import kotlin.collections.HashSet

@Repository
class ProjectReportIdentificationPersistenceProvider(
    private val projectReportRepository: ProjectReportRepository,
    private val targetGroupRepository: ProjectReportIdentificationTargetGroupRepository,
    private val spendingProfileRepository: ProjectReportSpendingProfileRepository,
    private val projectPartnerReportRepository: ProjectPartnerReportRepository
): ProjectReportIdentificationPersistence {

    companion object {
        private val log = LoggerFactory.getLogger(ProjectReportIdentificationPersistenceProvider::class.java)
    }

    @Transactional(readOnly = true)
    override fun getReportIdentification(projectId: Long, reportId: Long): ProjectReportIdentification {
        val projectReport = projectReportRepository.getByIdAndProjectId(reportId, projectId)
        return projectReport.toProjectReportIdentification(
            targetGroupEntities = targetGroupRepository.findAllByProjectReportEntityOrderBySortNumber(projectReport),
            spendingProfilePerPartner = null,
        )
    }

    @Transactional(readOnly = true)
    override fun getSpendingProfileReportedValues(reportId: Long): List<ProjectReportSpendingProfileReportedValues> {
        return spendingProfileRepository.findAllByIdProjectReportIdOrderByPartnerNumber(reportId).toReportedValuesModel()
    }

    @Transactional(readOnly = true)
    override fun getSpendingProfileCumulative(reportIds: Set<Long>): Map<Long, BigDecimal> {
        return spendingProfileRepository.findCumulativeForReportIds(reportIds).toMap()
    }

    @Transactional
    override fun updateReportIdentification(
        projectId: Long,
        reportId: Long,
        identification: ProjectReportIdentificationUpdate
    ): ProjectReportIdentification {
        val projectReport = projectReportRepository.getByIdAndProjectId(reportId, projectId)
        val targetGroups = updateTargetGroups(projectReport, identification)
        val translatedValues = updateTranslations(identification, projectReport)
        return ProjectReportIdentification(
            targetGroups = targetGroups.toModel(),
            highlights = translatedValues.map { InputTranslation(it.language(), it.highlights) }.toSet(),
            deviations = translatedValues.map { InputTranslation(it.language(), it.deviations) }.toSet(),
            partnerProblems = translatedValues.map { InputTranslation(it.language(), it.partnerProblems) }.toSet(),
            spendingProfilePerPartner = null,
        )
    }

    @Transactional(readOnly = true)
    override fun getSpendingProfileCurrentValues(reportId: Long): Map<Long, BigDecimal> =
        projectPartnerReportRepository.findTotalAfterControlPerPartner(reportId).toMap()

    @Transactional
    override fun updateSpendingProfile(reportId: Long, currentValuesByPartnerId: Map<Long, BigDecimal>) {
        val spendingProfilesById =
            spendingProfileRepository.findAllByIdProjectReportIdOrderByPartnerNumber(reportId).associateBy { it.id.partnerId }
        currentValuesByPartnerId.forEach { (partnerId, partnerCurrentValue) ->
            spendingProfilesById.getById(partnerId).let {
                when {
                    it.isPresent -> it.get().currentlyReported = partnerCurrentValue
                    else -> spendingProfileRepository.save(
                        profile(
                            reportId,
                            partnerId = partnerId,
                            currentValue = partnerCurrentValue,
                            totalEligibleBudget = BigDecimal.ZERO
                         )
                    ).also { log.error("We are persisting current partner value for partner ${it.id.partnerId} " +
                        "(project reportId=$reportId) that was not available during creation") }
                }
            }
        }
    }

    @Transactional
    override fun deleteSpendingProfileReportedValues(reportId: Long, partnerId: Long) {
        spendingProfileRepository.deleteByIdProjectReportIdAndIdPartnerId(reportId, partnerId)
    }

    private fun updateTranslations(
        identification: ProjectReportIdentificationUpdate,
        projectReport: ProjectReportEntity
    ):  MutableSet<ProjectReportIdentificationTranslEntity> {
        val highlightsAsMap = identification.getHighlightsAsMap()
        val partnerProblemsAsMap = identification.getPartnerProblemsAsMap()
        val deviationsAsMap = identification.getDeviationsAsMap()
        projectReport.addIdentificationMissingLanguagesIfNeeded(
            languages = highlightsAsMap.keys union partnerProblemsAsMap.keys union deviationsAsMap.keys
        )

        projectReport.translatedValues.forEach {
            it.highlights = highlightsAsMap[it.language()]
            it.partnerProblems = partnerProblemsAsMap[it.language()]
            it.deviations = deviationsAsMap[it.language()]
        }
        return projectReport.translatedValues
    }

    private fun updateTargetGroups(
        projectReport: ProjectReportEntity,
        identification: ProjectReportIdentificationUpdate
    ): List<ProjectReportIdentificationTargetGroupEntity> {
        val targetGroups = targetGroupRepository.findAllByProjectReportEntityOrderBySortNumber(projectReport)
        targetGroups.forEachIndexed { index, targetGroup ->
            val translationsByLanguage = identification.targetGroups
                .getOrElse(index) { emptySet() }
                .associateBy({ it.language }, { it.translation })

            targetGroup.addTargetGroupMissingLanguagesIfNeeded(languages = translationsByLanguage.keys)
            targetGroup.translatedValues.forEach {
                it.description = translationsByLanguage[it.language()]
            }
        }
        return targetGroups
    }


    private fun ProjectReportIdentificationTargetGroupEntity.addTargetGroupMissingLanguagesIfNeeded(languages: Set<SystemLanguage>) {
        val existingLanguages = translatedValues.mapTo(HashSet()) { it.translationId.language }
        languages.filter { !existingLanguages.contains(it) }.forEach { language ->
            translatedValues.add(
                ProjectReportIdentificationTargetGroupTranslEntity(
                    translationId = TranslationId(this, language = language),
                    description = null,
                )
            )
        }
    }

    private fun ProjectReportEntity.addIdentificationMissingLanguagesIfNeeded(languages: Set<SystemLanguage>) {
        val existingLanguages = translatedValues.mapTo(HashSet()) { it.translationId.language }
        languages.filter { !existingLanguages.contains(it) }.forEach { language ->
            translatedValues.add(
                ProjectReportIdentificationTranslEntity(
                    translationId = TranslationId(this, language = language),
                    highlights = null,
                    deviations = null,
                    partnerProblems = null,
                )
            )
        }
    }

    private fun Map<Long, ProjectReportSpendingProfileEntity>.getById(id: Long): Optional<ProjectReportSpendingProfileEntity> {
        val value = this[id]
        return if (value != null)
            Optional.of(value)
        else
            Optional.empty()
    }

    private fun profile(reportId: Long, partnerId: Long, currentValue: BigDecimal, totalEligibleBudget: BigDecimal) = ProjectReportSpendingProfileEntity(
        ProjectReportSpendingProfileId(
            projectReport = projectReportRepository.getById(reportId),
            partnerId = partnerId,
        ),
        partnerNumber = 0,
        partnerAbbreviation = "",
        partnerRole = ProjectPartnerRole.PARTNER,
        country = null,
        previouslyReported = BigDecimal.ZERO,
        currentlyReported = currentValue,
        partnerTotalEligibleBudget = totalEligibleBudget
    )

}
