package io.cloudflight.jems.server.project.service.checklist.clone

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

internal fun ChecklistInstanceDetail.updateWith(existingChecklist: ChecklistInstanceDetail): ChecklistInstanceDetail {
    return ChecklistInstanceDetail(
        id = this.id,
        programmeChecklistId = this.programmeChecklistId,
        type = this.type,
        name = this.name,
        creatorEmail = this.creatorEmail,
        creatorId = this.creatorId,
        createdAt = this.createdAt,
        status = this.status,
        finishedDate = this.finishedDate,
        relatedToId = this.relatedToId,
        consolidated = this.consolidated,
        visible = this.visible,
        minScore = existingChecklist.minScore,
        maxScore = existingChecklist.maxScore,
        allowsDecimalScore = existingChecklist.allowsDecimalScore,
        components = existingChecklist.components?.toList()
    )
}
