package io.cloudflight.jems.server.project.repository.report.project.closure

import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure
import io.cloudflight.jems.server.project.service.report.project.closure.ProjectReportProjectClosurePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportProjectClosurePersistenceProvider(
    private val projectReportProjectClosureStoryRepository: ProjectReportProjectClosureStoryRepository,
    private val projectReportProjectClosurePrizeRepository: ProjectReportProjectClosurePrizeRepository,
): ProjectReportProjectClosurePersistence {

    @Transactional(readOnly = true)
    override fun getProjectReportProjectClosure(reportId: Long): ProjectReportProjectClosure =
        toModel(
            closureStoryEntity = projectReportProjectClosureStoryRepository.getByReportId(reportId),
            closurePrizeEntities = projectReportProjectClosurePrizeRepository.findAllByReportId(reportId)
        )

    @Transactional
    override fun updateProjectReportProjectClosure(
        reportId: Long,
        updatedProjectClosure: ProjectReportProjectClosure
    ): ProjectReportProjectClosure {
        updateClosureStory(reportId, updatedProjectClosure)
        updateClosurePrizes(reportId, updatedProjectClosure)
        return getProjectReportProjectClosure(reportId)
    }

    @Transactional
    override fun deleteProjectReportProjectClosure(reportId: Long) {
        projectReportProjectClosurePrizeRepository.deleteAllByReportId(reportId)
        projectReportProjectClosureStoryRepository.deleteById(reportId)
    }

    private fun updateClosureStory(reportId: Long, updatedProjectClosure: ProjectReportProjectClosure) {
        val storyEntity = projectReportProjectClosureStoryRepository.getByReportId(reportId)
        if (storyEntity != null) {
            storyEntity.updateTranslations(updatedProjectClosure.toUpdatedStoryTranslEntities(storyEntity))
        } else {
            projectReportProjectClosureStoryRepository.save(updatedProjectClosure.toClosureStoryEntity(reportId))
        }
    }

    private fun updateClosurePrizes(reportId: Long, updatedProjectClosure: ProjectReportProjectClosure) {
        val storedPrizes = projectReportProjectClosurePrizeRepository.findAllByReportId(reportId)
        storedPrizes.forEach { prizeEntity ->
            val updatedPrize = updatedProjectClosure.prizes.find { it.id == prizeEntity.id }
            if (updatedPrize != null) {
                prizeEntity.sortNumber = updatedPrize.orderNum
                prizeEntity.updateTranslations(updatedPrize.toUpdatedPrizeTranslEntities(prizeEntity))
            } else {
                projectReportProjectClosurePrizeRepository.delete(prizeEntity)
            }
        }

        val prizesToBeCreated = updatedProjectClosure.prizes.filter { it.id == null }
        projectReportProjectClosurePrizeRepository.saveAll(prizesToBeCreated.toClosurePrizeEntities(reportId))
    }

}
