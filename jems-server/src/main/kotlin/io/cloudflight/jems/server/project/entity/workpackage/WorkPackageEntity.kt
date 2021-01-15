package io.cloudflight.jems.server.project.entity.workpackage

import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_work_package")
data class WorkPackageEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @field:NotNull
    val project: ProjectEntity,

    @Column
    val number: Int? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.workPackageId")
    val translatedValues: Set<WorkPackageTransl> = mutableSetOf(),

    @OneToMany(mappedBy = "outputId.workPackageId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val workPackageOutputs: List<WorkPackageOutputEntity> = emptyList(),

    @OneToMany(mappedBy = "activityId.workPackageId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val activities: List<WorkPackageActivityEntity> = emptyList(),
)
