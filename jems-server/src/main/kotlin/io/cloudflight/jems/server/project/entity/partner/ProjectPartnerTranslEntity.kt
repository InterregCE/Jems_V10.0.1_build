package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * project partner lang table
 */
@Entity(name = "project_partner_transl")
data class ProjectPartnerTranslEntity(

    @EmbeddedId
    val translationId: TranslationPartnerId,

    val department: String? = null

)
