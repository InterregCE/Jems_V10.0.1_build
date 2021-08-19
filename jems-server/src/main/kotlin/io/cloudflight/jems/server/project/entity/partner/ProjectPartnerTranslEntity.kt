package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * project partner lang table
 */
@Entity(name = "project_partner_transl")
class ProjectPartnerTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectPartnerEntity>,

    val department: String? = null,

    val otherIdentifierDescription: String? = null,

    ) : TranslationEntity()
