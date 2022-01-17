package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.TranslationUuId
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * C2 benefit lang table
 */
@Entity(name = "project_description_c2_relevance_benefit_transl")
class ProjectRelevanceBenefitTransl(
    @EmbeddedId
    override val translationId: TranslationId<ProjectRelevanceBenefitEntity>,
    val specification: String? = null
) : TranslationEntity()
