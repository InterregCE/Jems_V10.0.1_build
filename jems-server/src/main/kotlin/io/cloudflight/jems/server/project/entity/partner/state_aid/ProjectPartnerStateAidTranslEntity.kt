package io.cloudflight.jems.server.project.entity.partner.state_aid

import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * project partner lang table
 */
@Entity(name = "project_partner_state_aid_transl")
class ProjectPartnerStateAidTranslEntity(
    @EmbeddedId
    val translationId: TranslationPartnerId,

    val justification1: String? = null,
    val justification2: String? = null,
    val justification3: String? = null,
    val justification4: String? = null,
)
