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
class ProjectRelevanceEntity(

    @Id
    @Column(name = "project_id")
    val projectId: Long,

    // C2.1, C2.2, C2.3, 2.7
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.projectId")
    val translatedValues: Set<ProjectRelevanceTransl> = emptySet(),

    // C2.4
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "project_relevance_id", nullable = false, insertable = true)
    val projectBenefits: Set<ProjectRelevanceBenefitEntity> = emptySet(),

    // C2.4a
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "project_relevance_id", nullable = false, insertable = true)
    val projectSpfRecipients: Set<ProjectRelevanceSpfRecipientEntity> = emptySet(),

    // C2.5
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "project_relevance_id", nullable = false, insertable = true)
    val projectStrategies: Set<ProjectRelevanceStrategyEntity> = emptySet(),

    // C2.6
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "project_relevance_id", nullable = false, insertable = true)
    val projectSynergies: Set<ProjectRelevanceSynergyEntity> = emptySet()

)
