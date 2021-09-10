package io.cloudflight.jems.server.project.entity.workpackage.activity

import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableEntity
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.NamedSubgraph
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_work_package_activity")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "WorkPackageActivityEntity.full",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
            NamedAttributeNode(value = "deliverables", subgraph = "deliverables-subgraph"),
        ],
        subgraphs = [
            NamedSubgraph(name = "deliverables-subgraph", attributeNodes = [
                NamedAttributeNode(value = "translatedValues"),
            ]),
        ]
    )
)
class WorkPackageActivityEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val workPackageId: Long,

    @field:NotNull
    val activityNumber: Int,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<WorkPackageActivityTranslationEntity> = mutableSetOf(),

    val startPeriod: Int? = null,

    val endPeriod: Int? = null,

    @OneToMany(mappedBy = "id", cascade = [CascadeType.ALL], orphanRemoval = true)
    val deliverables: Set<WorkPackageActivityDeliverableEntity> = emptySet()
)
