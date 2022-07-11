package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.project.checklist.*
import io.cloudflight.jems.server.common.gson.toJson
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ChecklistInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ProgrammeChecklistMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ScoreMetadata
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.metadata.ScoreInstanceMetadata
import java.math.BigDecimal

private val ProgrammeChecklistMetadata?.question: String?
    get() {
        return if (this is ScoreMetadata)
            this.question
        else
            null
    }

private val ProgrammeChecklistMetadata?.weight: BigDecimal?
    get() {
        return if (this is ScoreMetadata)
            this.weight
        else
            null
    }

private val ChecklistInstanceMetadata?.score: BigDecimal?
    get() {
        return if (this is ScoreInstanceMetadata)
            this.score
        else
            null
    }

fun Collection<ChecklistInstance>.toDataModel() = map {
    ChecklistSummaryData(
        id = it.id!!,
        name = it.name!!,
        status = ChecklistStatusData.valueOf(it.status.name),
        type = ChecklistTypeData.valueOf(it.type.name),
        creatorEmail = it.creatorEmail!!,
        relatedToId = it.relatedToId,
        finishedDate = it.finishedDate,
        consolidated = it.consolidated,
        visible = it.visible,
    )
}

fun ChecklistInstanceDetail.toDataModel() = ChecklistInstanceData(
    id = id,
    name = name!!,
    status = ChecklistStatusData.valueOf(status.name),
    type = ChecklistTypeData.valueOf(type!!.name),
    creatorEmail = creatorEmail!!,
    creatorId = creatorId!!,
    relatedToId = relatedToId,
    finishedDate = finishedDate,
    consolidated = consolidated ?: false,
    visible = visible,
    minScore = minScore,
    maxScore = maxScore,
    allowsDecimalScore = allowsDecimalScore ?: false,
    questions = components?.toDataModel() ?: emptyList(),
)

fun List<ChecklistComponentInstance>.toDataModel() = map {
    ChecklistQuestionData(
        id = it.id,
        type = ChecklistQuestionTypeData.valueOf(it.type.name),
        position = it.position,
        question = it.programmeMetadata.question,
        weight = it.programmeMetadata.weight,
        score = it.instanceMetadata.score,
        questionMetadataJson = it.instanceMetadata.toJson(),
        answerMetadataJson = it.programmeMetadata.toJson(),
    )
}
