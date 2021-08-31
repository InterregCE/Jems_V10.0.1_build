package io.cloudflight.jems.server.project.entity.workpackage

import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_work_package")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "WorkPackageEntity.withTranslatedValues",
        attributeNodes = [NamedAttributeNode(value = "translatedValues")],
    ),
)
class WorkPackageEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @field:NotNull
    val project: ProjectEntity,

    @Column
    var number: Int? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity", fetch = FetchType.EAGER)
    val translatedValues: MutableSet<WorkPackageTransl> = mutableSetOf(),

    @OneToMany(mappedBy = "outputId.workPackageId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val outputs: MutableList<WorkPackageOutputEntity> = mutableListOf(),

    @OneToMany(mappedBy = "activityId.workPackageId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val activities: MutableList<WorkPackageActivityEntity> = mutableListOf(),
){

    fun copy(translatedValues: MutableSet<WorkPackageTransl>?): WorkPackageEntity =
        WorkPackageEntity(
            id = this.id,
            project = this.project,
            number = this.number,
            translatedValues = translatedValues ?: this.translatedValues,
            outputs = this.outputs,
            activities = this.activities
        )

}
