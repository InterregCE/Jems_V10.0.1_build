package io.cloudflight.jems.server.call.entity

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_call_selected_checklist")
class CallSelectedChecklistEntity(
    @EmbeddedId
    val id: CallSelectedChecklistId,
)
