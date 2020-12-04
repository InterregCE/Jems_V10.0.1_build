package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.project.entity.TranslationId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * C3 lang table
 */
@Entity(name = "project_description_c3_partnership_transl")
data class ProjectPartnershipTransl(

    @EmbeddedId
    val translationId: TranslationId,

    // C3
    @Column
    val projectPartnership: String?

)
