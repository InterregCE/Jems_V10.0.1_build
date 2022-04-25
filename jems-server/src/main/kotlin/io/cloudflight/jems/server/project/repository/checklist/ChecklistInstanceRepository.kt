package io.cloudflight.jems.server.project.repository.checklist

import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.entity.checklist.ChecklistInstanceEntity
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ChecklistInstanceRepository : JpaRepository<ChecklistInstanceEntity, Long> {

    fun findByRelatedToIdAndCreatorIdAndProgrammeChecklistType(
        relatedToId: Long,
        creatorId: Long,
        programmeChecklistType: ProgrammeChecklistType,
    ): List<ChecklistInstanceEntity>


    @Query("SELECT checklist.status FROM #{#entityName} checklist where checklist.id=:id")
    fun findStatusForId(id: Long): ChecklistInstanceStatus

    @Query("SELECT COUNT(checklist) FROM #{#entityName} checklist where checklist.programmeChecklist.id=:checklistTemplateId")
    fun countByProgrammeChecklistId(checklistTemplateId: Long): Long
}
