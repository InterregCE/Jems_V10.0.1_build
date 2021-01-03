package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * project partner lang table
 */
@Entity(name = "project_partner_motivation_transl")
data class ProjectPartnerMotivationTranslEntity(

    @EmbeddedId
    val translationId: TranslationPartnerId,

    val organizationRelevance: String? = null,

    val organizationRole: String? = null,

    val organizationExperience: String? = null

)
