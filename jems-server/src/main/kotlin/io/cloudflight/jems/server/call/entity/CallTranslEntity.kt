package io.cloudflight.jems.server.call.entity

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_call_transl")
data class CallTranslEntity(

    @EmbeddedId
    val translationId: CallTranslId,

    @Column
    val description: String? = null,
)
