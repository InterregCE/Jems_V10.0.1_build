package io.cloudflight.jems.server.common.gson

import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata

fun String?.toOptionsToggleInstance(): OptionsToggleInstanceMetadata {
    return gson.fromJson(this, OptionsToggleInstanceMetadata::class.java)
}

fun String?.toHeadlineInstance(): HeadlineInstanceMetadata = gson.fromJson(this, HeadlineInstanceMetadata::class.java)

