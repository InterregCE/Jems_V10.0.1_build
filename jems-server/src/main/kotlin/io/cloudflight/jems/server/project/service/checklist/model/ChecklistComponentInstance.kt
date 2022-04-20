package io.cloudflight.jems.server.programme.service.checklist.model

import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ChecklistInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ProgrammeChecklistMetadata

data class ChecklistComponentInstance(
    val id: Long,
    val type: ProgrammeChecklistComponentType,
    val position: Int,
    var programmeMetadata: ProgrammeChecklistMetadata?,
    var instanceMetadata: ChecklistInstanceMetadata?
)
