package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_description_c2_relevance_spf_recipient_transl")
class ProjectRelevanceSpfRecipientTransl(
    @EmbeddedId
    override val translationId: TranslationId<ProjectRelevanceSpfRecipientEntity>,
    val specification: String? = null
) : TranslationEntity()
