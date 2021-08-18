package io.cloudflight.jems.server.call.entity

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_call_state_aid")
class ProjectCallStateAidEntity (
    @EmbeddedId
    val setupId: StateAidSetupId,
)
