package io.cloudflight.jems.server.programme.entity

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "programme_priority_transl")
data class ProgrammePriorityTranslEntity(

    @EmbeddedId
    val translationId: ProgrammePriorityTranslId,

    @Column
    val title: String? = null,
)
