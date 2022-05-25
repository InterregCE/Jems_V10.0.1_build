package io.cloudflight.jems.server.project.repository.checklist

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Predicate
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.entity.checklist.ChecklistInstanceEntity
import io.cloudflight.jems.server.project.entity.checklist.QChecklistInstanceEntity
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository

@Repository
interface ChecklistInstanceRepository : JpaRepository<ChecklistInstanceEntity, Long>,
    QuerydslPredicateExecutor<ChecklistInstanceEntity> {

    companion object {
        private val instanceEntity = QChecklistInstanceEntity.checklistInstanceEntity

        private fun withRelatedToId(id: Long?) = if (id == null) null else instanceEntity.relatedToId.eq(id)
        private fun withType(type: ProgrammeChecklistType?) = if (type == null) null else instanceEntity.programmeChecklist.type.eq(type)
        private fun withCreatorId(id: Long?) = if (id == null) null else instanceEntity.creator.id.eq(id)
        private fun withStatus(status: ChecklistInstanceStatus?) = if (status == null) null else instanceEntity.status.eq(status)
        private fun isVisible(visible: Boolean?) = if (visible == null) null else instanceEntity.visible.eq(visible)

        fun buildSearchPredicate(searchRequest: ChecklistInstanceSearchRequest?): Predicate? =
            ExpressionUtils.allOf(
                withRelatedToId(searchRequest?.relatedToId),
                withType(searchRequest?.type),
                withCreatorId(searchRequest?.creatorId),
                withStatus(searchRequest?.status),
                isVisible(searchRequest?.visible)
            )
    }

    @Query("SELECT COUNT(checklist) FROM #{#entityName} checklist where checklist.programmeChecklist.id=:checklistTemplateId")
    fun countByProgrammeChecklistId(checklistTemplateId: Long): Long
}
