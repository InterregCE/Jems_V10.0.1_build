package io.cloudflight.jems.server.common.gson

import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.project.service.checklist.model.metadata.ScoreInstanceMetadata
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata

fun String?.toOptionsToggleInstance(): OptionsToggleInstanceMetadata {
    return gson.fromJson(this, OptionsToggleInstanceMetadata::class.java)
}

fun String?.toTextInputInstance(): TextInputInstanceMetadata {
    return gson.fromJson(this, TextInputInstanceMetadata::class.java)
}

fun String?.toHeadlineInstance(): HeadlineInstanceMetadata = gson.fromJson(this, HeadlineInstanceMetadata::class.java)

fun String?.toScoreInstance(): ScoreInstanceMetadata {
    return gson.fromJson(this, ScoreInstanceMetadata::class.java)
}

