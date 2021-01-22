package io.cloudflight.jems.server.project.entity.workpackage

import io.cloudflight.jems.server.programme.entity.indicator.IndicatorOutput
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "project_work_package_output")
data class WorkPackageOutputEntity(

    @EmbeddedId
    val outputId: WorkPackageOutputId,

    @ManyToOne
    val period: ProjectPeriodEntity? = null,

    @ManyToOne
    @JoinColumn(name = "indicator_output_id")
    val programmeOutputIndicator: IndicatorOutput? = null,

    @Column
    val targetValue: String? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.workPackageOutputId")
    val translatedValues: Set<WorkPackageOutputTransl> = emptySet()

)
