package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import java.util.Objects
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_description_c2_relevance_benefit")
data class ProjectRelevanceBenefit(

    @Id
    val id: UUID,

    @ManyToOne
    @JoinColumn(name = "project_relevance_id", insertable = false, updatable = false)
    val projectRelevance: ProjectRelevance? = null,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val targetGroup: ProjectTargetGroup,

    // specification
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.id")
    val translatedValues: MutableSet<ProjectRelevanceBenefitTransl> = mutableSetOf()

) {

    override fun hashCode(): Int {
        return Objects.hash(projectRelevance?.projectId, targetGroup)
    }

    override fun equals(other: Any?): Boolean = (other is ProjectRelevanceBenefit)
        && projectRelevance?.projectId == other.projectRelevance?.projectId
        && targetGroup == other.targetGroup
        && translatedValues == other.translatedValues

    override fun toString(): String {
        return "${this.javaClass.simpleName}(projectRelevance.projectId=${projectRelevance?.projectId}, targetGroup=$targetGroup, translatedValues=$translatedValues)"
    }

}
