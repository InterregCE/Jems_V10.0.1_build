package io.cloudflight.jems.server.project.entity.description

import java.util.Objects
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "project_description_c2_relevance_synergy")
data class ProjectRelevanceSynergy(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "project_relevance_id", insertable = false, updatable = false)
    val projectRelevance: ProjectRelevance? = null,

    @Column
    val synergy: String?,

    @Column
    val specification: String?

) {

    override fun hashCode(): Int {
        return Objects.hash(projectRelevance?.projectId, synergy)
    }

    override fun equals(other: Any?): Boolean = (other is ProjectRelevanceSynergy)
        && projectRelevance?.projectId == other.projectRelevance?.projectId
        && synergy == other.synergy
        && specification == other.specification

    override fun toString(): String {
        return "${this.javaClass.simpleName}(projectRelevance.projectId=${projectRelevance?.projectId}, synergy=$synergy, specification=$specification)"
    }

}
