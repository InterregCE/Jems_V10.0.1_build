package io.cloudflight.jems.server.project.entity.workpackage.activity

import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableEntity
import javax.persistence.CascadeType
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.NamedSubgraph
import javax.persistence.OneToMany

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

    @EmbeddedId
    val activityId: WorkPackageActivityId,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<WorkPackageActivityTranslationEntity> = mutableSetOf(),

    val startPeriod: Int? = null,

    val endPeriod: Int? = null,

    @OneToMany(mappedBy = "deliverableId.activityId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val deliverables: Set<WorkPackageActivityDeliverableEntity> = emptySet(),
)
