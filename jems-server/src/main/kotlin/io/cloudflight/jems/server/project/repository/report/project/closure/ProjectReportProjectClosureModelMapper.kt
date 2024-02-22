package io.cloudflight.jems.server.project.repository.report.project.closure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosurePrizeEntity
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosurePrizeTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosureStoryEntity
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosureStoryTranslEntity
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosurePrize


fun toModel(
    closureStoryEntity: ProjectReportProjectClosureStoryEntity?,
    closurePrizeEntities: List<ProjectReportProjectClosurePrizeEntity>,
) = ProjectReportProjectClosure(
    story = closureStoryEntity?.translatedValues?.extractField { it.story } ?: emptySet(),
    prizes = closurePrizeEntities.map { entity ->
        ProjectReportProjectClosurePrize(
            id = entity.id,
            prize = entity.translatedValues.extractField { it.prize },
            orderNum = entity.sortNumber
        )
    }.sortedBy { it.orderNum }
)

fun ProjectReportProjectClosure.toClosureStoryEntity(projectReportId: Long): ProjectReportProjectClosureStoryEntity =
    ProjectReportProjectClosureStoryEntity(
        reportId = projectReportId,
        translatedValues = mutableSetOf()
    ).apply {
        translatedValues.addTranslationEntities(
            { language ->
                ProjectReportProjectClosureStoryTranslEntity(
                    translationId = TranslationId(this, language),
                    story = story.extractTranslation(language),
                )
            }, arrayOf(story)
        )
    }

fun List<ProjectReportProjectClosurePrize>.toClosurePrizeEntities(projectReportId: Long): List<ProjectReportProjectClosurePrizeEntity> =
    this.map {
        ProjectReportProjectClosurePrizeEntity(
            sortNumber = it.orderNum,
            reportId = projectReportId,
            translatedValues = mutableSetOf()
        ).apply {
            translatedValues.addTranslationEntities(
                { language ->
                    ProjectReportProjectClosurePrizeTranslEntity(
                        translationId = TranslationId(this, language),
                        prize = it.prize.extractTranslation(language),
                    )
                }, arrayOf(it.prize)
            )
        }
    }

fun ProjectReportProjectClosure.toUpdatedStoryTranslEntities(
    existingStory: ProjectReportProjectClosureStoryEntity
): Set<ProjectReportProjectClosureStoryTranslEntity> =
    this.story.map {
        ProjectReportProjectClosureStoryTranslEntity(
            translationId = TranslationId(existingStory, it.language),
            story = it.translation
        )
    }.toSet()

fun ProjectReportProjectClosurePrize.toUpdatedPrizeTranslEntities(
    existingPrize: ProjectReportProjectClosurePrizeEntity
): Set<ProjectReportProjectClosurePrizeTranslEntity> =
    this.prize.map {
        ProjectReportProjectClosurePrizeTranslEntity(
            translationId = TranslationId(existingPrize, it.language),
            prize = it.translation
        )
    }.toSet()

