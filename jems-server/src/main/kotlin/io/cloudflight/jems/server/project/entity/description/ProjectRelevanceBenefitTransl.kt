package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.project.entity.TranslationUuId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * C2 benefit lang table
 */
@Entity(name = "project_description_c2_relevance_benefit_transl")
data class ProjectRelevanceBenefitTransl(

    @EmbeddedId
    val translationId: TranslationUuId,

    @Column
    val specification: String? = null

)
