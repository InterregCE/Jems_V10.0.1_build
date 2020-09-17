package io.cloudflight.ems.project.entity.description

import io.cloudflight.ems.api.strategy.ProgrammeStrategy
import java.util.Objects
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "project_description_c2_relevance_strategy")
data class ProjectRelevanceStrategy(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "project_relevance_id", insertable = false, updatable = false)
    val projectRelevance: ProjectRelevance? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val strategy: ProgrammeStrategy,

    @Column
    val specification: String?

){

    override fun hashCode(): Int {
        return Objects.hash(projectRelevance?.projectId, strategy)
    }

    override fun equals(other: Any?): Boolean = (other is ProjectRelevanceStrategy)
        && projectRelevance?.projectId == other.projectRelevance?.projectId
        && strategy == other.strategy
        && specification == other.specification

    override fun toString(): String {
        return "${this.javaClass.simpleName}(projectRelevance.projectId=${projectRelevance?.projectId}, strategy=$strategy, specification=$specification)"
    }

}
