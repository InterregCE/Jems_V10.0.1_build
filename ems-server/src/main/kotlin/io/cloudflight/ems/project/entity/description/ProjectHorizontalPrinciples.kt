package io.cloudflight.ems.project.entity.description

import io.cloudflight.ems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class ProjectHorizontalPrinciples(

    @Column
    @Enumerated(EnumType.STRING)
    val sustainableDevelopmentCriteriaEffect: ProjectHorizontalPrinciplesEffect? = null,

    @Column
    val sustainableDevelopmentDescription: String? = null,

    @Column
    @Enumerated(EnumType.STRING)
    val equalOpportunitiesEffect: ProjectHorizontalPrinciplesEffect? = null,

    @Column
    val equalOpportunitiesDescription: String? = null,

    @Column
    @Enumerated(EnumType.STRING)
    val sexualEqualityEffect: ProjectHorizontalPrinciplesEffect? = null,

    @Column
    val sexualEqualityDescription: String? = null

) {
    fun ifNotEmpty(): ProjectHorizontalPrinciples? {
        if (sustainableDevelopmentCriteriaEffect != null
            || sustainableDevelopmentDescription != null
            || equalOpportunitiesEffect != null
            || equalOpportunitiesDescription != null
            || sexualEqualityEffect != null
            || sexualEqualityDescription != null)
            return this
        return null
    }
}
