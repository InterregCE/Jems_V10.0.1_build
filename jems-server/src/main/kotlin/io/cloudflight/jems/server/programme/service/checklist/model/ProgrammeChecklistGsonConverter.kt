package io.cloudflight.jems.server.common.gson

import com.google.gson.Gson
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata

val gson = Gson()

fun String?.toOptionsToggle(): OptionsToggleMetadata {
    return gson.fromJson(this, OptionsToggleMetadata::class.java)
}

fun String?.toHeadline(): HeadlineMetadata = gson.fromJson(this, HeadlineMetadata::class.java)

fun Any?.toJson(): String = gson.toJson(this)

