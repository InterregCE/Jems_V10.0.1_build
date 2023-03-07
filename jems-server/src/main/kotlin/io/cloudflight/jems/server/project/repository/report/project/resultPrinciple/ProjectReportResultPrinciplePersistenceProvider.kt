package io.cloudflight.jems.server.project.repository.report.project.resultPrinciple

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportHorizontalPrincipleEntity
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportHorizontalPrincipleTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportProjectResultEntity
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportProjectResultTranslEntity
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrincipleUpdate
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultUpdate
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportResultPrinciplePersistenceProvider(
    private val projectPersistence: ProjectPersistence,
    private val projectResultRepository: ProjectReportProjectResultRepository,
    private val horizontalPrincipleRepository: ProjectReportHorizontalPrincipleRepository,
) : ProjectReportResultPrinciplePersistence {

    @Transactional(readOnly = true)
    override fun getProjectResultPrinciples(projectId: Long, reportId: Long): ProjectReportResultPrinciple {
        val projectResults = projectResultRepository.findByProjectReportId(reportId = reportId)
        val periods = fetchAvailablePeriodsFor(projectResults)

        val horizontalPrinciples = horizontalPrincipleRepository.getByProjectReportId(reportId)

        return toResultPrincipleModel(projectResults, horizontalPrinciples, periodResolver = { periods[it] })
    }

    @Transactional
    override fun updateProjectReportResultPrinciple(
        projectId: Long,
        reportId: Long,
        newResultsAndPrinciples: ProjectReportResultPrincipleUpdate,
    ): ProjectReportResultPrinciple {
        val projectResults = projectResultRepository.findByProjectReportId(reportId)
        projectResults.updateProjectResults(newValues = newResultsAndPrinciples.projectResults)
        val periods = fetchAvailablePeriodsFor(projectResults)

        val horizontalPrinciples = horizontalPrincipleRepository.getByProjectReportId(reportId)
        horizontalPrinciples.updateTranslationsWith(newResultsAndPrinciples)

        return toResultPrincipleModel(projectResults, horizontalPrinciples, periodResolver = { periods[it] })
    }

    @Transactional(readOnly = true)
    override fun getResultCumulative(reportIds: Set<Long>): Map<Int, BigDecimal> =
        projectResultRepository.getCumulativeValues(reportIds).toMap()

    private fun fetchAvailablePeriodsFor(results: List<ProjectReportProjectResultEntity>) =
        if (results.isEmpty()) emptyMap() else
            projectPersistence
                .getProjectPeriods(results.first().projectReport.projectId, results.first().projectReport.applicationFormVersion)
                .associateBy { it.number }

    private fun List<ProjectReportProjectResultEntity>.updateProjectResults(
        newValues: Map<Int, ProjectReportResultUpdate>
    ) = forEach { entity ->
        if (!newValues.containsKey(entity.resultNumber))
            return@forEach

        val newValue = newValues[entity.resultNumber]!!
        entity.currentReport = newValue.currentValue

        val languages = newValue.description.mapTo(HashSet()) { it.language }
        val descriptionMap = newValue.description.associateBy { it.language }
        entity.translatedValues.removeIf { !languages.contains(it.language()) }
        entity.translatedValues.forEach {
            it.description = descriptionMap[it.translationId.language]?.translation
        }
        languages.filter { !entity.translatedValues.map { it.translationId.language }.contains(it) }
            .mapTo(entity.translatedValues) {
                ProjectReportProjectResultTranslEntity(
                    translationId = TranslationId(entity, it),
                    description = descriptionMap[it]?.translation,
                )
            }
    }

    private fun ProjectReportHorizontalPrincipleEntity.updateTranslationsWith(
        resultPrinciple: ProjectReportResultPrincipleUpdate,
    ) {
        val sustainableDevelopmentMap = resultPrinciple.sustainableDevelopmentDescription.associateBy({ it.language }, { it.translation })
        val equalOpportunitiesMap = resultPrinciple.equalOpportunitiesDescription.associateBy({ it.language }, { it.translation })
        val sexualEqualityMap = resultPrinciple.sexualEqualityDescription.associateBy({ it.language }, { it.translation })

        val languages = mutableSetOf<SystemLanguage>()
        languages.addAll(sustainableDevelopmentMap.keys)
        languages.addAll(equalOpportunitiesMap.keys)
        languages.addAll(sexualEqualityMap.keys)


        translatedValues.removeIf { !languages.contains(it.translationId.language) }
        translatedValues.forEach {
            it.apply {
                sustainableDevelopmentDescription = sustainableDevelopmentMap[it.translationId.language]
                equalOpportunitiesDescription = equalOpportunitiesMap[it.translationId.language]
                sexualEqualityDescription = sexualEqualityMap[it.translationId.language]
            }
        }
        languages.filter { !translatedValues.map { it.translationId.language }.contains(it) }
            .mapTo(translatedValues) {
                ProjectReportHorizontalPrincipleTranslEntity(
                    TranslationId(this, it),
                    sustainableDevelopmentMap[it],
                    equalOpportunitiesMap[it],
                    sexualEqualityMap[it],
                )
            }
    }

}
