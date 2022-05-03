package io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable

import io.cloudflight.jems.server.common.entity.resetTranslations
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "project_work_package_activity_deliverable")
class WorkPackageActivityDeliverableEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "deliverable_number")
    @field:NotNull
    var deliverableNumber: Int,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<WorkPackageActivityDeliverableTranslationEntity> = mutableSetOf(),

    var startPeriod: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="activity_id")
    @field:NotNull
    var workPackageActivity: WorkPackageActivityEntity
){
    fun updateTranslations(newTranslations: Set<WorkPackageActivityDeliverableTranslationEntity>) {
        this.translatedValues.resetTranslations(newTranslations
        ) { currentTranslation, newTranslation ->
            currentTranslation.description = newTranslation.description
            currentTranslation.title = newTranslation.title
        }
    }
}
