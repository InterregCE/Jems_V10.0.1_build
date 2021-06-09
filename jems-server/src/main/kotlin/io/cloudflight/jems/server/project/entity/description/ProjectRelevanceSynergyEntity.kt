package io.cloudflight.jems.server.project.entity.description

import java.util.Objects
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "project_description_c2_relevance_synergy")
data class ProjectRelevanceSynergyEntity(

    @Id
    val id: UUID,

    @ManyToOne
    @JoinColumn(name = "project_relevance_id", insertable = false, updatable = false)
    val projectRelevance: ProjectRelevanceEntity? = null,

    // synergy, specification
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.id")
    val translatedValues: MutableSet<ProjectRelevanceSynergyTransl> = mutableSetOf()

) {

    override fun hashCode(): Int {
        return Objects.hash(projectRelevance?.projectId, translatedValues)
    }

    override fun equals(other: Any?): Boolean = (other is ProjectRelevanceSynergyEntity)
        && projectRelevance?.projectId == other.projectRelevance?.projectId
        && translatedValues == other.translatedValues

    override fun toString(): String {
        return "${this.javaClass.simpleName}(projectRelevance.projectId=${projectRelevance?.projectId}, translatedValues=$translatedValues)"
    }

}
