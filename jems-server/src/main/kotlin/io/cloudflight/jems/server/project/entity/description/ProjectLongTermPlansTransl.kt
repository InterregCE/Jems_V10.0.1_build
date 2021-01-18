package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.project.entity.TranslationId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_description_c8_long_term_plans_transl")
data class ProjectLongTermPlansTransl(

    @EmbeddedId
    val translationId: TranslationId,

    @Column
    val projectOwnership: String?,

    @Column
    val projectDurability: String?,

    @Column
    val projectTransferability: String?
)