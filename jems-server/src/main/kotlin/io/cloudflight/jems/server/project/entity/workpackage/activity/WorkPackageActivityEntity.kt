package io.cloudflight.jems.server.project.entity.workpackage.activity

import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableEntity
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
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
            NamedSubgraph(
                name = "deliverables-subgraph", attributeNodes = [
                    NamedAttributeNode(value = "translatedValues"),
                ]
            ),
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
    var activityNumber: Int,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<WorkPackageActivityTranslationEntity> = mutableSetOf(),

    var startPeriod: Int? = null,

    var endPeriod: Int? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "activity_id", nullable = false, insertable = true)
    val deliverables: MutableSet<WorkPackageActivityDeliverableEntity> = mutableSetOf()
) {
    fun updateDeliverables(newDeliverables: Set<WorkPackageActivityDeliverableEntity>) {
        this.deliverables.removeIf { deliverable -> !newDeliverables.map { it.id }.contains(deliverable.id) }
        this.deliverables.forEach { currentDeliverable ->
            val newDeliverable = newDeliverables.first { it.id == currentDeliverable.id }
            currentDeliverable.deliverableNumber = newDeliverable.deliverableNumber
            currentDeliverable.startPeriod = newDeliverable.startPeriod
        }
        this.deliverables.addAll(newDeliverables.filter { it.id == 0L })
    }

    fun updateTranslations(newTranslations: Set<WorkPackageActivityTranslationEntity>) {
        this.translatedValues.removeIf { translation ->
            !newTranslations.map { it.language() }.contains(translation.language())
        }
        this.translatedValues.forEach { currentTranslation ->
            val newTranslation = newTranslations.first { it.language() == currentTranslation.language() }
            currentTranslation.description = newTranslation.description
            currentTranslation.title = newTranslation.title
        }
        val newLanguages = translatedValues.map { it.language() }.subtract(newTranslations.map { it.language() })
        this.translatedValues.addAll(newTranslations.filter { newLanguages.contains(it.language()) })
    }
}
