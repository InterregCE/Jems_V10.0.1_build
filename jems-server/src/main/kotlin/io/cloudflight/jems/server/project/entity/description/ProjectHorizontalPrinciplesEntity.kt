package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class ProjectHorizontalPrinciplesEntity(

    @Column
    @Enumerated(EnumType.STRING)
    val sustainableDevelopmentCriteriaEffect: ProjectHorizontalPrinciplesEffect? = null,

    @Column
    @Enumerated(EnumType.STRING)
    val equalOpportunitiesEffect: ProjectHorizontalPrinciplesEffect? = null,

    @Column
    @Enumerated(EnumType.STRING)
    val sexualEqualityEffect: ProjectHorizontalPrinciplesEffect? = null

) {
    fun ifNotEmpty(): ProjectHorizontalPrinciplesEntity? {
        if (sustainableDevelopmentCriteriaEffect != null
            || equalOpportunitiesEffect != null
            || sexualEqualityEffect != null
        )
            return this
        return null
    }
}
