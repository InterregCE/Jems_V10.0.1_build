package io.cloudflight.jems.server.project.entity.workpackage

import io.cloudflight.jems.server.programme.entity.indicator.IndicatorOutput
import io.cloudflight.jems.server.project.entity.ProjectPeriod
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_work_package_output")
data class WorkPackageOutput(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "work_package_id")
    @field:NotNull
    val workPackage: WorkPackage,

    @ManyToOne
    val period: ProjectPeriod? = null,

    @Column
    val outputNumber: Int,

    @ManyToOne
    @JoinColumn(name = "indicator_output_id")
    val programmeOutputIndicator: IndicatorOutput? = null,

    @Column
    val title: String? = null,

    @Column
    val targetValue: String? = null,

    @Column
    val description: String? = null,

    )