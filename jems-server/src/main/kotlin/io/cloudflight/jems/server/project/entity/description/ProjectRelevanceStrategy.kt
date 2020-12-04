package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import java.util.Objects
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "project_description_c2_relevance_strategy")
data class ProjectRelevanceStrategy(

    @Id
    val id: UUID,

    @ManyToOne
    @JoinColumn(name = "project_relevance_id", insertable = false, updatable = false)
    val projectRelevance: ProjectRelevance? = null,

    @Column
    @Enumerated(EnumType.STRING)
    val strategy: ProgrammeStrategy?,

    // specification
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.id")
    val translatedValues: MutableSet<ProjectRelevanceStrategyTransl> = mutableSetOf()

) {

    override fun hashCode(): Int {
        return Objects.hash(projectRelevance?.projectId, strategy)
    }

    override fun equals(other: Any?): Boolean = (other is ProjectRelevanceStrategy)
        && projectRelevance?.projectId == other.projectRelevance?.projectId
        && strategy == other.strategy
        && translatedValues == other.translatedValues

    override fun toString(): String {
        return "${this.javaClass.simpleName}(projectRelevance.projectId=${projectRelevance?.projectId}, strategy=$strategy, translatedValues=$translatedValues)"
    }

}
