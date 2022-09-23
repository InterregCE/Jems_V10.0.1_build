package io.cloudflight.jems.server.project.repository.checklist.control

import io.cloudflight.jems.server.programme.repository.checklist.ProgrammeChecklistRepository
import io.cloudflight.jems.server.project.entity.checklist.ChecklistInstanceEntity
import io.cloudflight.jems.server.project.repository.checklist.ChecklistInstanceRepository
import io.cloudflight.jems.server.project.repository.checklist.toDetailModel
import io.cloudflight.jems.server.project.repository.checklist.toInstanceEntity
import io.cloudflight.jems.server.project.repository.checklist.toModel
import io.cloudflight.jems.server.project.service.checklist.ControlChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ControlChecklistInstancePersistenceProvider(
    private val repository: ChecklistInstanceRepository,
    private val userRepo: UserRepository,
    private val programmeChecklistRepository: ProgrammeChecklistRepository
) : ControlChecklistInstancePersistence {

    @Transactional
    override fun create(
        createChecklist: CreateChecklistInstanceModel,
        creatorId: Long,
        reportId: Long
    ): ChecklistInstanceDetail {
        val programmeChecklist = programmeChecklistRepository.getById(createChecklist.programmeChecklistId)
        return repository.save(
            ChecklistInstanceEntity(
                status = ChecklistInstanceStatus.DRAFT,
                creator = userRepo.getById(creatorId),
                relatedToId = reportId,
                finishedDate = null,
                programmeChecklist = programmeChecklist,
                components = programmeChecklist.components?.map { it.toInstanceEntity() }?.toMutableSet()
            ).also {
                it.components?.forEach { component -> component.checklistComponentId.checklist = it }
            }
        ).toDetailModel()
    }

    @Transactional(readOnly = true)
    override fun findChecklistInstances(searchRequest: ChecklistInstanceSearchRequest): List<ChecklistInstance> =
        repository.findAll(ChecklistInstanceRepository.buildSearchPredicate(searchRequest)!!).toList().toModel()
}