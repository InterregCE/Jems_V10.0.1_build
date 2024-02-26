package io.cloudflight.jems.server.project.repository.report.project.closure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.inLang
import io.cloudflight.jems.server.common.entity.updateWith
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosurePrizeEntity
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosurePrizeTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosureStoryTranslEntity
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure
import io.cloudflight.jems.server.project.service.report.project.closure.ProjectReportProjectClosurePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportProjectClosurePersistenceProvider(
    private val projectReportRepository: ProjectReportRepository,
    private val projectReportProjectClosureStoryRepository: ProjectReportProjectClosureStoryRepository,
    private val projectReportProjectClosurePrizeRepository: ProjectReportProjectClosurePrizeRepository,
): ProjectReportProjectClosurePersistence {

    @Transactional(readOnly = true)
    override fun getProjectReportProjectClosure(reportId: Long): ProjectReportProjectClosure {
        val report = projectReportRepository.getById(reportId)
        return ProjectReportProjectClosure(
            story = projectReportProjectClosureStoryRepository.findAllByTranslationIdSourceEntity(report)
                .extractField { it.story },
            prizes = projectReportProjectClosurePrizeRepository.findAllByReportIdOrderBySortNumberAsc(reportId)
                .map { it.translatedValues.extractField { it.prize } },
        )
    }

    @Transactional
    override fun updateProjectReportProjectClosure(
        reportId: Long,
        updatedProjectClosure: ProjectReportProjectClosure
    ): ProjectReportProjectClosure = ProjectReportProjectClosure(
        story = updateClosureStory(reportId, updatedProjectClosure.story),
        prizes = updateClosurePrizes(reportId, updatedProjectClosure.prizes),
    )

    @Transactional
    override fun deleteProjectReportProjectClosure(reportId: Long) {
        val report = projectReportRepository.getById(reportId)
        projectReportProjectClosurePrizeRepository.deleteAllByReportId(report.id)
        projectReportProjectClosureStoryRepository.deleteAllByTranslationIdSourceEntity(report)
    }

    private fun updateClosureStory(reportId: Long, story: Set<InputTranslation>): Set<InputTranslation> {
        val report = projectReportRepository.getById(reportId)
        val storyTranslations = projectReportProjectClosureStoryRepository.findAllByTranslationIdSourceEntity(report)

        storyTranslations.updateWith(
            entitySupplier = { lang -> projectReportProjectClosureStoryRepository.save(
                ProjectReportProjectClosureStoryTranslEntity(TranslationId(report, lang), "")
            ) },
            allTranslations = listOf(story),
            { e -> e.story = story.inLang(e.language()) },
        )

        return storyTranslations.extractField { it.story }
    }

    private fun updateClosurePrizes(reportId: Long, prizes: List<Set<InputTranslation>>): List<Set<InputTranslation>> {
        val storedPrizes = projectReportProjectClosurePrizeRepository.findAllByReportIdOrderBySortNumberAsc(reportId)

        val result = prizes.mapIndexed { index, prize ->
            val orderNr = index.plus(1)

            val prizeEntity = storedPrizes.getOrElse(index, defaultValue = {
                projectReportProjectClosurePrizeRepository.save(ProjectReportProjectClosurePrizeEntity(0L, orderNr, reportId))
            })
            prizeEntity.translatedValues.updateWith(
                entitySupplier = { lang -> ProjectReportProjectClosurePrizeTranslEntity(TranslationId(prizeEntity, lang), "") },
                allTranslations = listOf(prize),
                { e -> e.prize = prize.inLang(e.language()) },
            )
            return@mapIndexed prizeEntity
        }

        // remove deleted lines (leftovers after update)
        storedPrizes.removeAll(result)
        projectReportProjectClosurePrizeRepository.deleteAll(storedPrizes)

        return result.toModel()
    }

}
