package io.cloudflight.jems.server.project.entity.workpackage

import io.cloudflight.jems.server.project.entity.Project
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
    val project: Project,

    @Column
    val number: Int? = null,

    @Column
    val name: String? = null,

    @Column(name = "specific_objective")
    val specificObjective: String? = null,

    @Column(name = "objective_and_audience")
    val objectiveAndAudience: String? = null,

    @OneToMany(
        mappedBy = "workPackage",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val workPackageOutputs: MutableSet<WorkPackageOutputEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "activityId.workPackageId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val activities: List<WorkPackageActivityEntity> = emptyList(),
)
