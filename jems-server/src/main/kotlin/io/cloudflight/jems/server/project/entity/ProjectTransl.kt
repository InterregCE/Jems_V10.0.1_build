package io.cloudflight.jems.server.project.entity

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_transl")
data class ProjectTransl(

    @EmbeddedId
    val translationId: TranslationId,

    @Column
    val title: String? = null
)