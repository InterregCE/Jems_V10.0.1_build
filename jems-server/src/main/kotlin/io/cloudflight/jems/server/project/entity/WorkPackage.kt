package io.cloudflight.jems.server.project.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_work_package")
data class WorkPackage(

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
    val name: String?,

    @Column(name = "specific_objective")
    val specificObjective: String?,

    @Column(name = "objective_and_audience")
    val objectiveAndAudience: String?

    )
