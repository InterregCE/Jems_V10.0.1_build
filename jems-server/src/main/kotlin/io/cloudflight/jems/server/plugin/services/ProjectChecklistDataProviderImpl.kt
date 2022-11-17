package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.project.checklist.ChecklistTypeData
import io.cloudflight.jems.plugin.contract.services.ProjectChecklistDataProvider
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectChecklistDataProviderImpl(
    private val persistence: ChecklistInstancePersistence,
) : ProjectChecklistDataProvider {

    companion object {
        private val logger = LoggerFactory.getLogger(ProjectChecklistDataProviderImpl::class.java)
    }

    @Transactional(readOnly = true)
    override fun getChecklistDetail(checklistId: Long) =
        persistence.getChecklistDetail(checklistId).toDataModel().also {
            logger.info("Retrieved checklist instance id=$checklistId via plugin.")
        }

    @Transactional(readOnly = true)
    override fun getChecklistsForProject(projectId: Long, type: ChecklistTypeData) =
        persistence.findChecklistInstances(
            ChecklistInstanceSearchRequest(
                relatedToId = projectId,
                type = ProgrammeChecklistType.valueOf(type.name),
            )
        ).toDataModel().also {
            logger.info("Retrieved checklist instances for project id=$projectId via plugin.")
        }
}
