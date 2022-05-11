package io.cloudflight.jems.server.project.repository.checklist

import io.cloudflight.jems.server.programme.repository.checklist.ProgrammeChecklistRepository
import io.cloudflight.jems.server.project.service.checklist.getMyInstances.GetChecklistInstanceDetailNotFoundException
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.entity.checklist.ChecklistInstanceEntity
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Repository
class ChecklistInstancePersistenceProvider(
    private val repository: ChecklistInstanceRepository,
    private val userRepo: UserRepository,
    private val programmeChecklistRepository: ProgrammeChecklistRepository,
) : ChecklistInstancePersistence {

    @Transactional(readOnly = true)
    override fun getChecklistsByRelationAndCreatorAndType(
        relatedToId: Long,
        creatorId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance> =
        repository.findByRelatedToIdAndCreatorIdAndProgrammeChecklistType(relatedToId, creatorId, type).toModel()

    @Transactional(readOnly = true)
    override fun getChecklistsByRelatedIdAndType(
        relatedToId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance> =
        repository.findByRelatedToIdAndProgrammeChecklistType(relatedToId, type).toModel()

    @Transactional(readOnly = true)
    override fun getChecklistsByRelationAndType(
        relatedToId: Long,
        type: ProgrammeChecklistType,
        visible: Boolean
    ): List<ChecklistInstance> {
        return repository.findByRelatedToIdAndVisibleAndProgrammeChecklistTypeAndVisible(relatedToId, type, visible)
            .toDto()
    }

    @Transactional(readOnly = true)
    override fun getChecklistDetail(id: Long): ChecklistInstanceDetail {
        return getChecklistOrThrow(id).toDetailModel()
    }

    @Transactional(readOnly = true)
    override fun getChecklistSummary(checklistId: Long): ChecklistInstance
        = getChecklistOrThrow(checklistId).toModel()

    @Transactional
    override fun create(createChecklist: CreateChecklistInstanceModel, creatorId: Long): ChecklistInstanceDetail {
        val programmeChecklist = programmeChecklistRepository.getById(createChecklist.programmeChecklistId);
        return repository.save(
            ChecklistInstanceEntity(
                status = ChecklistInstanceStatus.DRAFT,
                creator = userRepo.getById(creatorId),
                relatedToId = createChecklist.relatedToId,
                finishedDate = null,
                programmeChecklist = programmeChecklist,
                components = programmeChecklist.components?.map { it.toInstanceEntity() }?.toMutableSet()
            ).also {
                it.components?.forEach { component -> component.checklistComponentId.checklist = it }
            }
        ).toDetailModel()
    }

    @Transactional
    override fun update(checklist: ChecklistInstanceDetail): ChecklistInstanceDetail {
        val checklistInstance = getChecklistOrThrow(checklist.id)
        checklistInstance.update(checklist)
        return checklistInstance.toDetailModel()
    }

    @Transactional
    override fun updateSelection(checklistId: Long, visible: Boolean): ChecklistInstance {
        val checklistInstance = getChecklistOrThrow(checklistId)
        checklistInstance.visible = visible
        return checklistInstance.toDto()
    }

    @Transactional
    override fun deleteById(id: Long) {
        repository.delete(getChecklistOrThrow(id))
    }

    @Transactional(readOnly = true)
    override fun countAllByChecklistTemplateId(checklistTemplateId: Long): Long {
        return repository.countByProgrammeChecklistId(checklistTemplateId)
    }

    @Transactional
    override fun consolidateChecklistInstance(checklistId: Long, consolidated: Boolean): ChecklistInstance {
        val checklistInstance = getChecklistOrThrow(checklistId)
        checklistInstance.consolidated = consolidated
        return checklistInstance.toModel()
    }

    @Transactional
    override fun changeStatus(checklistId: Long, status: ChecklistInstanceStatus): ChecklistInstance {
        val checklistInstance = getChecklistOrThrow(checklistId)
        checklistInstance.status = status
        checklistInstance.finishedDate =
            if (status == ChecklistInstanceStatus.FINISHED) LocalDate.now()
            else null
        return checklistInstance.toModel()
    }

    private fun getChecklistOrThrow(id: Long): ChecklistInstanceEntity =
        repository.findById(id).orElseThrow { GetChecklistInstanceDetailNotFoundException() }
}
