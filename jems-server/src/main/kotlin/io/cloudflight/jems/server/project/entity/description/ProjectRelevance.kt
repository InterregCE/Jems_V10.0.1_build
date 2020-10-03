package io.cloudflight.jems.server.project.entity.description

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

/**
 * C2
 */
@Entity(name = "project_description_c2_relevance")
data class ProjectRelevance(

    @Id
    @Column(name = "project_id")
    val projectId: Long,

    // C2.1
    @Column
    val territorialChallenge: String? = null,

    // C2.2
    @Column
    val commonChallenge: String? = null,

    // C2.3
    @Column
    val transnationalCooperation: String? = null,

    // C2.4
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "project_relevance_id", nullable = false, insertable = true)
    val projectBenefits: Set<ProjectRelevanceBenefit> = emptySet(),

    // C2.5
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "project_relevance_id", nullable = false, insertable = true)
    val projectStrategies: Set<ProjectRelevanceStrategy> = emptySet(),

    // C2.6
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "project_relevance_id", nullable = false, insertable = true)
    val projectSynergies: Set<ProjectRelevanceSynergy> = emptySet(),

    // C2.7
    @Column
    val availableKnowledge: String? = null

) {

    override fun toString(): String {
        return "${this.javaClass.simpleName}(projectId=${projectId})"
    }

}
